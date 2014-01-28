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
package org.n52.sir.util;

/**
 * 
 * Simple class to hold a pair of two objects that belong together somehow.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class Pair<T1, T2> {

    private T1 first;

    private T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first
     */
    public T1 getFirst() {
        return this.first;
    }

    /**
     * @return the second
     */
    public T2 getSecond() {
        return this.second;
    }

    /**
     * @param first
     *        the first to set
     */
    public void setFirst(T1 first) {
        this.first = first;
    }

    /**
     * @param second
     *        the second to set
     */
    public void setSecond(T2 second) {
        this.second = second;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pair <");
        sb.append(this.first.getClass().getName());
        sb.append(", ");
        sb.append(this.second.getClass().getName());
        sb.append("> [first=");
        sb.append(this.first);
        sb.append(", second=");
        sb.append(this.first.getClass().getName());
        sb.append("]");
        return sb.toString();
    }
}
