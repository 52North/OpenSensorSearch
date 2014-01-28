/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
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
        this.results = new ArrayList<>();
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
