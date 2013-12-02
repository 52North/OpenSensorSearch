<?xml version="1.0" encoding="UTF-8"?>
<!--

    ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:gml="http://www.opengis.net/gml/3.2"
	xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0"
	xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="2.0"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd 
  http://www.opengis.net/cat/wrs/1.0 http://schemas.opengis.net/csw/2.0.2/profiles/ebrim/1.0/csw-ebrim.xsd http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">
	<!-- imports -->
	<xsl:import href="SensorML-to-ebRIM_variables.xsl" />
	<xsl:import href="SensorML-to-ebRIM_contact.xsl" />
	<!-- have just one import of _classification here instead of in both _system
		and _component, see http://stackoverflow.com/questions/10096086/how-to-handle-duplicate-imports-in-xslt -->
	<xsl:import href="SensorML-to-ebRIM_classification.xsl" />
	<xsl:import href="SensorML-to-ebRIM_system.xsl" />
	<xsl:import href="SensorML-to-ebRIM_component.xsl" />
	<!-- remove all unwanted output, i.e. "ignoring all text nodes" -->
	<xsl:template match="text()|@*" />
	<!-- remove blank lines from the output document -->
	<xsl:strip-space elements="*" />
	<xsl:template match="sml:member">
		<xsl:comment>
			This document was created using 52North Open Sensor Search.

			Homepage:
			https://github.com/52North/OpenSensorSearch

			Contact:
			d.nuest@52north.org
		</xsl:comment>
		<xsl:if test="$debugOn">
			<xsl:comment>
				Processor:
				<xsl:value-of select="system-property('xsl:vendor')" />
			</xsl:comment>
		</xsl:if>
		<xsl:element name="rim:RegistryPackage" namespace="{$nsrim}">
			<xsl:choose>
				<xsl:when test="$noSWE">
					<xsl:attribute name="xsi:schemaLocation"><xsl:value-of
						select="concat($gmlSchemaLocation, ' ', $wrsSchemaLocation)" /></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="xsi:schemaLocation"><xsl:value-of
						select="concat($sweSchemaLocation, ' ', $gmlSchemaLocation, ' ', $wrsSchemaLocation)" /></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:attribute name="id"><xsl:value-of
				select="concat($idPrefix, $idMiddlefixRegPack, generate-id(sml:System))" /></xsl:attribute>
			<xsl:attribute name="xsi:schemaLocation"><xsl:value-of
				select="concat($wrsSchemaLocation, ' ', $gmlSchemaLocation)" /></xsl:attribute>
			<xsl:element name="rim:RegistryObjectList" namespace="{$nsrim}">
				<xsl:apply-templates select="sml:System" mode="extrinsic-object" />
				<xsl:apply-templates select="sml:System"
					mode="classification-association" />
				<xsl:comment>
					########## contact information ##########
				</xsl:comment>
				<xsl:apply-templates select="sml:System/sml:contact" />
				<xsl:comment>
					############### components ##############
				</xsl:comment>
				<xsl:apply-templates select="sml:System/sml:components" />
			</xsl:element>
			<!-- rim:RegistryObjectList -->
		</xsl:element>
		<!-- rim:RegistryPackage -->
	</xsl:template>
</xsl:transform>
