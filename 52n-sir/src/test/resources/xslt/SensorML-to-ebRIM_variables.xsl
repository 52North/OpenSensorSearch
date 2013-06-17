<?xml version="1.0" encoding="UTF-8"?>
<!--

    ﻿Copyright (C) 2012
    by 52 North Initiative for Geospatial Open Source Software GmbH

    Contact: Andreas Wytzisk
    52 North Initiative for Geospatial Open Source Software GmbH
    Martin-Luther-King-Weg 24
    48155 Muenster, Germany
    info@52north.org

    This program is free software; you can redistribute and/or modify it under
    the terms of the GNU General Public License version 2 as published by the
    Free Software Foundation.

    This program is distributed WITHOUT ANY WARRANTY; even without the implied
    WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License along with
    this program (see gnu-gpl v2.txt). If not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
    visit the Free Software Foundation web page, http://www.fsf.org.

-->
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<!-- output parameters -->
	<xsl:output method="xml" indent="no" encoding="UTF-8"
		version="1.0" />
		
	<!-- DEBUGGING -->
	<xsl:param name="debugOn" select="false()" />

	<!-- NAMESPACES AND SCHEMA LOCATIONS -->
	<xsl:variable name="nsrim"
		select="'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0'" />
	<xsl:variable name="nswrs" select="'http://www.opengis.net/cat/wrs/1.0'" />
	<xsl:variable name="nsgml" select="'http://www.opengis.net/gml'" />
	<xsl:variable name="gmlSchemaLocation"
		select="'http://www.opengis.net/gml http://schemas.opengis.net/gml/3.1.1/base/gml.xsd'" />
	<xsl:variable name="nsswe" select="'http://www.opengis.net/swe/1.0.1'" />
	<xsl:variable name="sweSchemaLocation"
		select="'http://www.opengis.net/swe/1.0.1 http://schemas.opengis.net/sweCommon/1.0.1/swe.xsd'" />
	<xsl:variable name="wrsSchemaLocation"
		select="'http://www.opengis.net/cat/wrs/1.0 http://schemas.opengis.net/csw/2.0.2/profiles/ebrim/1.0/csw-ebrim.xsd'" />

	<!-- true() or false() -->
	<xsl:variable name="noSWE" select="true()" />
	
	<!-- constant for testing agains valid swe:referenceFrame values -->
	<xsl:variable name="epsgUrnPrefix" select="'urn:ogc:def:crs:EPSG:'" />

	<!-- TYPES -->
	<xsl:variable name="fixedObjectTypeSystem"
		select="'urn:ogc:def:objectType:OGC-CSW-ebRIM-Sensor::System'" />
	<xsl:variable name="fixedObjectTypeComponent"
		select="'urn:ogc:def:objectType:OGC-CSW-ebRIM-Sensor::Component'" />
	<xsl:variable name="ComposedOfAssociationType"
		select="'urn:ogc:def:associationType::OGC-CSW-ebRIM-Sensor::ComposedOf'" />
	<xsl:variable name="AccessibleThroughAssociationType"
		select="'urn:ogc:def:associationType::OGC-CSW-ebRIM-Sensor::AccessibleThrough'" />

	<!-- CLASSIFICATION SCHEMES -->
	<xsl:variable name="classificationSchemeId.SystemTypes"
		select="'urn:ogc:def:classificationScheme:OGC-CSW-ebRIM-Sensor::SystemTypes'" />
	<xsl:variable name="classificationSchemeId.IntendedApplication"
		select="'urn:ogc:def:classificationScheme:OGC-CSW-ebRIM-Sensor::IntendedApplication'" />
	<xsl:variable name="classificationSchemeId.ISO19119Services"
		select="'urn:ogc:def:ebRIM-ClassificationScheme:ISO-19119:2005:Services'" />

	<!-- (ID) PREFIXES -->
	<xsl:variable name="idPrefix" select="'urn:sir:id:'" />
	<xsl:variable name="idMiddlefixOrganization" select="'org:'" />
	<xsl:variable name="idMiddlefixPerson" select="'person:'" />
	<xsl:variable name="idMiddlefixRegPack" select="'package:'" />
	<xsl:variable name="idMiddlefixAssociation" select="'association:'" />
	<xsl:variable name="idMiddlefixClassification" select="'classification:'" />
	<xsl:variable name="idMiddlefixService" select="'service:'" />
	<xsl:variable name="idMiddlefixGml" select="'gmlId:'" />
	<xsl:variable name="slotPrefix"
		select="'urn:ogc:def:slot:OGC-CSW-ebRIM-Sensor::'" />
	<xsl:variable name="oasisDataTypePrefix"
		select="'urn:oasis:names:tc:ebxml-regrep:DataType:'" />
	<xsl:variable name="iso19107DataTypePrefix"
		select="'urn:ogc:def:dataType:ISO-19107:2003:'" />
	<xsl:variable name="classificationNodeIdPrefix"
		select="'urn:ogc:def:objectType:OGC-CSW-ebRIM-Sensor::'" />

	<!-- GLOBAL ATTRIBUTE VALUES -->
	<xsl:variable name="fixedSystemAndComponentClassificationParent"
		select="'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ExtrinsicObject'" />
	<xsl:variable name="defaultLocalizedStringLang" select="'en-US'" />

	<!-- MESSAGES -->
	<xsl:variable name="message.gmlError"
		select="'Element gml:description not found! Maybe I cannot match the gml
						namespace? Transformation requires http://www.opengis.net/gml/3.1.1'" />

	<!-- SERVICE TYPES -->
	<xsl:template name="deduceServiceType">
		<xsl:param name="type" />
		<xsl:choose>
			<!--
				Checking for known service types or service type deduction (as
				described in aforementioned section 12.2) could be added here if
				necessary.
			-->
			<xsl:when test="starts-with($type, 'urn:ogc:serviceType:')">
				<xsl:value-of select="." />
			</xsl:when>
			<!-- Version number handling in urns ist NOT implemented! -->
			<xsl:when
				test="substring($type, (string-length($type) - string-length('SOS')) + 1) = 'SOS'">
				<xsl:value-of select="'urn:ogc:serviceType:SensorObservationService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('SPS')) + 1) = 'SPS'">
				<xsl:value-of select="'urn:ogc:serviceType:SensorPlanningService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('WFS')) + 1) = 'WFS'">
				<xsl:value-of select="'urn:ogc:serviceType:WebFeatureService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('WMS')) + 1) = 'WMS'">
				<xsl:value-of select="'urn:ogc:serviceType:WebMapService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('SES')) + 1) = 'SES'">
				<xsl:value-of select="'urn:ogc:serviceType:SensorAlertService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('SAS')) + 1) = 'SAS'">
				<xsl:value-of select="'urn:ogc:serviceType:SensorAlertService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('WNS')) + 1) = 'WNS'">
				<xsl:value-of select="'urn:ogc:serviceType:WebNotificationService'" />
			</xsl:when>
			<xsl:when
				test="substring($type, (string-length($type) - string-length('CAT')) + 1) = 'CAT'">
				<xsl:value-of select="'urn:ogc:serviceType:CatalogueService'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'Error: Unknown service type!'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="deduceServiceParent">
		<xsl:param name="type" />
		<xsl:choose>
			<xsl:when test="not(starts-with($type, 'urn:ogc:serviceType:'))">
				<xsl:value-of select="'ERROR: Not given a service id!'" />
			</xsl:when>
			<!-- Version number handling in urns ist NOT implemented! -->
			<xsl:when test="$type = 'urn:ogc:serviceType:SensorObservationService'">
				<xsl:value-of
					select="'urn:ogc:def:ebRIM-ClassificationScheme:ISO-19119:2005:Services:InfoManagement'" />
			</xsl:when>
			<xsl:when test="$type = 'urn:ogc:serviceType:SensorPlanningService'">
				<xsl:value-of
					select="'urn:ogc:def:ebRIM-ClassificationScheme:ISO-19119:2005:Services:SystemManagement'" />
			</xsl:when>
			<xsl:when test="$type = 'urn:ogc:serviceType:SensorAlertService'">
				<xsl:value-of
					select="'urn:ogc:def:ebRIM-ClassificationScheme:ISO-19119:2005:Services:Subscription'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="'ERROR: No parent deduction rule found! Check for rules in file SensorML-to-ebRIM_variables.xsl.'" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:transform>