/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.ar.wikitude;

import org.n52.ar.SirPOI;

/**
 * 
 * @author Arne de Wall
 *
 */
public class WikitudePOI extends SirPOI { 

	void appendARML(StringBuilder sb) {
        sb.append("<Placemark id=\"" + this.id + "\">");

		sb.append("<ar:provider>52North</ar:provider>");
        sb.append("<name><![CDATA[" + this.title + "]]></name>");
        sb.append("<description><![CDATA[" + this.description + "]]></description>");

		sb.append("<Point>");
        sb.append("<coordinates>" + this.lat + "," + this.lon + "," + this.alt
				+ "</coordinates>");
		sb.append("</Point>");

		sb.append("</Placemark>");
	}
}
