
package org.n52.sir.xml.impl;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

class ClasspathResourceURIResolver implements URIResolver {

    private static final String SLASH = "/";

    private String resolveBase;

    public ClasspathResourceURIResolver(String myBase) {
        this.resolveBase = myBase;
        if ( !myBase.endsWith(SLASH))
            this.resolveBase += SLASH;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        StreamSource streamSource = new StreamSource(getClass().getResourceAsStream(this.resolveBase + href));

        return streamSource;
    }

    public String getResolveBase() {
        return this.resolveBase;
    }

    public void setResolveBase(String myBase) {
        this.resolveBase = myBase;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClasspathResourceURIResolver [");
        if (this.resolveBase != null) {
            builder.append("myBase=");
            builder.append(this.resolveBase);
        }
        builder.append("]");
        return builder.toString();
    }

}