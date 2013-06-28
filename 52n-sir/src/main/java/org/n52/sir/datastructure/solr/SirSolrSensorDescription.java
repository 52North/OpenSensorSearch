
package org.n52.sir.datastructure.solr;

import java.util.Collection;

import org.n52.sir.datastructure.SirSensorDescription;

public class SirSolrSensorDescription extends SirSensorDescription {
    private String id;
    private Collection<Object> keywords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<Object> getKeywords() {
        return keywords;
    }

    public void setKeywords(Collection<Object> keywords) {
        this.keywords = keywords;
    }

    public SirSolrSensorDescription() {
        // TODO Auto-generated constructor stub
    }

}
