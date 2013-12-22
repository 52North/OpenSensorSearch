/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sor.reasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.n52.sor.OwsExceptionReport;
import org.n52.sor.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.shared.WrappedIOException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * 
 * Wrapper for all functions required by the SOR from the Jena Semantic Web Framework
 * (http://jena.sourceforge.net/).
 * 
 * @author Jan Schulte
 * 
 */
public class JenaReasoner implements IReasoner {

    public enum MatchingCode {
        SUPER_CLASS, EQUIVALENT_CLASS, SUB_CLASS
    }

    /**
     * Includes an OntClass and a number of steps.
     * 
     * Makes the management of the visited nodes in the 'lookFor' function easier.
     * 
     */
    private static class OntNode {
        public OntClass oc = null;
        public int steps = 0;

        public OntNode(OntClass oc, int steps) {
            this.oc = oc;
            this.steps = steps;
        }

        public OntClass getOntClass() {
            return this.oc;
        }

        public int getSteps() {
            return this.steps;
        }
    }

    private static Logger log = LoggerFactory.getLogger(JenaReasoner.class);

    private static final Object SYSTEM_PROP_HOST = "proxyHost";

    private static final Object SYSTEM_PROP_PROXY_HOST_VALUE = "proxy";

    private static final Object SYSTEM_PROP_PORT = "proxyPort";

    private static final Object SYSTEM_PROP_PROXY_PORT_VALUE = "8080";

    private String phenomenaOntology;

    private OntModel om = null;

    private Reasoner rsnr = null;

    @SuppressWarnings("unused")
    private InfModel im = null;

    /**
     * 
     * @throws WrappedIOException
     */
    public JenaReasoner() throws WrappedIOException {
        System.getProperties().put(SYSTEM_PROP_HOST, SYSTEM_PROP_PROXY_HOST_VALUE);
        System.getProperties().put(SYSTEM_PROP_PORT, SYSTEM_PROP_PROXY_PORT_VALUE);

        this.phenomenaOntology = PropertiesManager.getInstance().getOntologyFile();

        this.om = ModelFactory.createOntologyModel();
        this.om.read(this.phenomenaOntology);
        this.rsnr = ReasonerRegistry.getOWLMicroReasoner();
        this.im = ModelFactory.createInfModel(this.rsnr, this.om);

        log.info("Instantiated NEW " + this.toString());
    }

    @Override
    public List<String> getMatchingURLs(String ontologyURL, MatchingCode code, int searchDepth) throws OwsExceptionReport {
        List<String> urls = new ArrayList<>();
        OntClass oc = this.om.getOntClass(ontologyURL);
        if (oc == null) {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         null,
                                         "There is no matching class returned from the dictionary using the ontology identified by the URL "
                                                 + ontologyURL + ".");
        }
        if (code.compareTo(MatchingCode.SUPER_CLASS) == 0) {
            urls = lookFor(ontologyURL, MatchingCode.SUPER_CLASS, searchDepth);
        }
        else if (code.compareTo(MatchingCode.EQUIVALENT_CLASS) == 0) {
            if (oc.hasEquivalentClass(null)) {
                for (ExtendedIterator< ? > ei = oc.listEquivalentClasses(); ei.hasNext();) {
                    String url = ei.next().toString();
                    System.out.println(url);
                    urls.add(url);
                }
            }
        }
        else {
            urls = lookFor(ontologyURL, MatchingCode.SUB_CLASS, searchDepth);
        }
        /*
         * case 4: for (ExtendedIterator ei = im.listObjectsOfProperty(oc, null); ei.hasNext(); ) { RDFNode n
         * = (RDFNode) ei.next(); if (n.isURIResource()) urls.add(n.toString()); } break;
         */
        // urls.add(String.valueOf(urls.size()));
        return urls;
    }

    /**
     * 
     * Returns the classes located at a determined distance (edges) from a starting class (node) in the
     * ontology tree.
     * 
     * @param ontClass
     *        URI of the starting class
     * @param steps
     *        Number of edges
     * @return Set containing the achievable classes
     */
    private List<String> lookFor(String ontologyURL, MatchingCode code, int searchDepth) {
        OntClass oc = this.om.getOntClass(ontologyURL);
        Queue<OntNode> queueNodes = new LinkedBlockingQueue<>();
        queueNodes.add(new OntNode(oc, searchDepth));
        Collection<OntNode> visited = new HashSet<>();
        List<String> results = new ArrayList<>();
        OntNode aux = null;
        OntNode on = null;
        OntClass auxOc = null;
        if (code.compareTo(MatchingCode.SUPER_CLASS) == 0) {
            while ( !queueNodes.isEmpty()) {
                aux = queueNodes.poll();
                visited.add(aux);
                if (aux.getSteps() > 0) {
                    for (ExtendedIterator< ? > i = aux.getOntClass().listSuperClasses(true); i.hasNext();) {
                        auxOc = (OntClass) i.next();
                        on = new OntNode(auxOc, aux.getSteps() - 1);
                        if ( !visited.contains(on))
                            queueNodes.add(on);
                        if ( !results.contains(auxOc.toString()) && auxOc.isURIResource()) {
                            results.add(auxOc.toString());
                        }
                    }
                }
            }
        }
        else {
            while ( !queueNodes.isEmpty()) {
                aux = queueNodes.poll();
                visited.add(aux);
                if (aux.getSteps() > 0) {
                    for (ExtendedIterator< ? > i = aux.getOntClass().listSubClasses(true); i.hasNext();) {
                        auxOc = (OntClass) i.next();
                        on = new OntNode(auxOc, aux.getSteps() - 1);
                        if ( !visited.contains(on))
                            queueNodes.add(on);
                        if ( !results.contains(auxOc.toString()) && auxOc.isURIResource()) {
                            results.add(auxOc.toString());
                        }
                    }
                }
            }
        }

        return results;
    }

    @Override
    public String toString() {
        return "JenaReasoner based on " + this.phenomenaOntology;
    }

}