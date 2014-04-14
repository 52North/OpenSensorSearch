
package org.n52.sir.sml;

import java.util.ArrayList;
import java.util.Collection;

import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.ClassificationDocument.Classification.ClassifierList.Classifier;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.KeywordsDocument.Keywords;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.TermDocument.Term;

import org.n52.oss.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorMLStringConverter {

    private static final Object NAME_VALUE_DELIMITER = ": ";

    private static final Object KEYWORDS_PREPEND = "Keywords" + NAME_VALUE_DELIMITER;

    private static final Object CLASSIFICATIONS_PREPEND = "Classifications" + NAME_VALUE_DELIMITER;

    private static final Object IDENTIFICATIONS_PREPEND = "Identifications" + NAME_VALUE_DELIMITER;

    private static final Object SECTION_END = ";";

    private static final Object LIST_ITEM_DELIMITER = ", ";

    private static Logger log = LoggerFactory.getLogger(SensorMLStringConverter.class);

    public SensorMLStringConverter() {
        //
    }

    /**
     * discovery profile expects _one_ {@link SystemType} as a {@link Member}.
     */
    public Collection<String> getText(SensorML sensorML) {
        Collection<String> texts = new ArrayList<>();

        Member[] members = sensorML.getMemberArray();

        Member member = members[0];
        if (member.getProcess() instanceof SystemType) {
            SystemType system = (SystemType) member.getProcess();
            texts.addAll(getText(system));
            return texts;
        }
        log.warn("Could not get text from given document. It is required to contain one member which is a SystemType.");

        return texts;
    }

    public Collection<String> getText(SensorMLDocument sensDoc) {
        return getText(sensDoc.getSensorML());
    }

    /**
     * method for the extraction of string descriptions
     */
    public Collection<String> getText(SystemType system) {
        Collection<String> texts = new ArrayList<>();

        // add identification to text field
        Identification[] identifications = system.getIdentificationArray();
        String sIdent = createIdentifierString(identifications);
        if ( !sIdent.isEmpty())
            texts.add(sIdent);

        // add classification to text field
        Classification[] classifications = system.getClassificationArray();
        String sClass = createClassificationString(classifications);
        if ( !sClass.isEmpty())
            texts.add(sClass);

        // add keywords to text field
        Keywords[] keywords = system.getKeywordsArray();
        String sKeywords = createKeywordsString(keywords);
        if ( !sKeywords.isEmpty())
            texts.add(sKeywords);

        return texts;
    }

    public String createClassificationString(Classification[] classifications) {
        StringBuilder sbClass = new StringBuilder();
        sbClass.append(CLASSIFICATIONS_PREPEND);
        for (Classification classification : classifications) {
            Classifier[] classifiers = classification.getClassifierList().getClassifierArray();
            for (Classifier classifier : classifiers) {
                // sbClass.append(classifier.getName());
                // sbClass.append(" - ");
                sbClass.append(Tools.simplifyString(classifier.getTerm().getValue()));
                sbClass.append(LIST_ITEM_DELIMITER);
            }
        }
        sbClass.replace(sbClass.length() - 2, sbClass.length(), ""); // remove
                                                                     // last
                                                                     // space
        sbClass.append(SECTION_END);
        return sbClass.toString();
    }

    public String createIdentifierString(Identification[] identifications) {
        StringBuilder sbIdent = new StringBuilder();
        sbIdent.append(IDENTIFICATIONS_PREPEND);
        for (Identification identification : identifications) {
            Identifier[] identifiers = identification.getIdentifierList().getIdentifierArray();
            for (Identifier identifier : identifiers) {
                Term term = identifier.getTerm();
                String[] s = term.getDefinition().split(":");
                sbIdent.append(s[s.length - 1]);
                sbIdent.append(NAME_VALUE_DELIMITER);
                sbIdent.append(Tools.simplifyString(term.getValue()));
                sbIdent.append(", ");
            }
        }
        sbIdent.replace(sbIdent.length() - 2, sbIdent.length(), "");
        sbIdent.append(SECTION_END);
        return sbIdent.toString();
    }

    public String createKeywordsString(Keywords[] keywords) {
        StringBuilder sbKeywords = new StringBuilder();
        sbKeywords.append(KEYWORDS_PREPEND);
        for (Keywords keyword : keywords) {
            String[] kwArray = keyword.getKeywordList().getKeywordArray();
            for (String currentKeyword : kwArray) {
                sbKeywords.append(Tools.simplifyString(currentKeyword));
                sbKeywords.append(", ");
            }
        }
        sbKeywords.replace(sbKeywords.length() - 2, sbKeywords.length(), "");
        sbKeywords.append(SECTION_END);
        return sbKeywords.toString();
    }

}
