/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.ar.junaio;

import org.n52.ar.SirPOI;

/**
 * 
 * @author Arne de Wall
 *
 */
public class JunaioPOI extends SirPOI {

	void appendXML(StringBuilder sb){
    	sb.append("<object id=\"" + id + "\">");
    	sb.append("<title><![CDATA[" + title + "]]></title>");
    	sb.append("<location>");
    	sb.append("<lat>" + lat + "</lat>");
    	sb.append("<lon>" + lon + "</lon>");
    	sb.append("<alt>" + 0 + "</alt>");
    	sb.append("</location>");
    	sb.append("<popup>");
    	sb.append("<description><![CDATA[" + description + "]]></description>");
    	sb.append("</popup>");
    	sb.append("</object>");
	}
}
