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

package org.n52.sir.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class SearchResult {

    private String source;

    private String query;

    private String url;

    private String description;

    private String author;

    private Date date;

    private Collection<SearchResultElement> results;
    
    public SearchResult() {
        super();
        this.results = new ArrayList<SearchResultElement>();
    }

    public SearchResult(String source, String query, String url, String description, String author, Date date) {
        this();
        this.source = source;
        this.query = query;
        this.url = url;
        this.description = description;
        this.author = author;
        this.date = date;
    }

    public SearchResult(String source,
                        String query,
                        String url,
                        String description,
                        String author,
                        Date date,
                        Collection<SearchResultElement> results) {
        this(source, query, url, description, author, date);
        this.results = results;
    }

    public void addResult(SearchResultElement result) {
        this.results.add(result);
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<SearchResultElement> getResults() {
        return this.results;
    }

    public void setResults(Collection<SearchResultElement> result) {
        this.results = result;
    }

    @Override
    public String toString() {
        return "SearchResult [source=" + this.source + ", query=" + this.query + ", url=" + this.url + ", description="
                + this.description + ", author=" + this.author + ", date=" + this.date + ", result count="
                + this.results.size() + "]";
    }

}
