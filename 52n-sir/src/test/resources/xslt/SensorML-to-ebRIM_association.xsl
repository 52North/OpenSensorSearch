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
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:sml="http://www.opengis.net/sensorML/1.0.1"
	xmlns:swe="http://www.opengis.net/swe/1.0.1" version="2.0"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd">

	<xsl:import href="SensorML-to-ebRIM_variables.xsl" />

	<xsl:strip-space elements="*" />

	<!-- COMPOSEDOF -->
	<xsl:template match="sml:Component" mode="ComposedOf-association">
		<xsl:param name="SOURCE_ID"
			select="ancestor::sml:System/sml:identification/sml:IdentifierList/sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::uniqueID' or @definition='urn:ogc:def:identifier:OGC:1.0:uniqueID']/sml:value" />
		<xsl:element name="rim:Association" namespace="{$nsrim}">
			<xsl:attribute name="id"><xsl:value-of
				select="concat($idPrefix, $idMiddlefixAssociation, generate-id())" /></xsl:attribute>
			<xsl:attribute name="associationType"><xsl:value-of
				select="$ComposedOfAssociationType" /></xsl:attribute>
			<xsl:attribute name="sourceObject"><xsl:value-of select="$SOURCE_ID" /></xsl:attribute>
			<xsl:attribute name="targetObject"><xsl:value-of
				select="sml:identification/sml:IdentifierList/sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::uniqueID' or @definition='urn:ogc:def:identifier:OGC:1.0:uniqueID']/sml:value" /></xsl:attribute>
		</xsl:element>
	</xsl:template>

	<!-- ACCESSIBLETHROUGH -->
	<!--
		See OGC 07-144r4, section 7.1 "ISO 19119 services taxonomy", section
		12.2 "OGC service description" (defines the values for
		rim:Classification/@classificationNode and
		rim:ClassificationNode/@classificationScheme), and OGC 09-163r2
		(defines rim:Association).
	-->
	<xsl:template mode="AccessibleThrough-association"
		match="sml:interfaces/sml:InterfaceList/sml:interface/sml:InterfaceDefinition/sml:serviceLayer/swe:DataRecord[@definition='urn:ogc:def:interface:OGC::SWEServiceInterface' or @definition='urn:ogc:def:interface:OGC:1.0:SWEServiceInterface']">
		<xsl:param name="SOURCE_OBJECT_ID" />
		<xsl:variable name="SERVICE_ID"
			select="concat($idPrefix, $idMiddlefixService, generate-id())" />
		<xsl:variable name="URL"
			select="normalize-space(swe:field[@name='urn:ogc:def:interface:OGC::ServiceURL' or @name='urn:ogc:def:interface:OGC:1.0:ServiceURL']/swe:Text/swe:value)" />
		<xsl:variable name="TYPE"
			select="normalize-space(swe:field[@name='urn:ogc:def:interface:OGC::ServiceType' or @name='urn:ogc:def:interface:OGC:1.0:ServiceType']/swe:Text/swe:value)" />
		<xsl:variable name="SSID"
			select="normalize-space(swe:field[@name='urn:ogc:def:interface:OGC::ServiceSpecificSensorID' or @name='urn:ogc:def:interface:OGC:1.0:ServiceSpecificSensorID']/swe:Text/swe:value)" />
		<xsl:variable name="CLASSIFICATION_NODE">
			<xsl:call-template name="deduceServiceType">
				<xsl:with-param name="type">
					<xsl:value-of select="$TYPE" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:element name="rim:Association" namespace="{$nsrim}">
			<xsl:attribute name="id"><xsl:value-of
				select="concat($idPrefix,
			$idMiddlefixAssociation, generate-id())" /></xsl:attribute>
			<xsl:attribute name="associationType"><xsl:value-of
				select="$AccessibleThroughAssociationType" /></xsl:attribute>
			<xsl:attribute name="sourceObject"><xsl:value-of select="$SOURCE_OBJECT_ID" /></xsl:attribute>
			<xsl:attribute name="targetObject"><xsl:value-of select="$SERVICE_ID" /></xsl:attribute>
			<xsl:element name="rim:Slot" namespace="{$nsrim}">
				<xsl:attribute name="name"><xsl:value-of
					select="concat($slotPrefix, 'ServiceSpecificSensorID')" /></xsl:attribute>
				<xsl:element name="rim:ValueList" namespace="{$nsrim}">
					<xsl:element name="rim:Value" namespace="{$nsrim}">
						<xsl:value-of select="$SSID" />
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>

		<!--
			See OGC 07-144r4, section 12.2 "OGC service description" and OGC
			09-163r2 section 9.2.2 "AccessibleThrough association type".
		-->
		<xsl:element name="rim:Service" namespace="{$nsrim}">
			<xsl:attribute name="id"><xsl:value-of select="$SERVICE_ID" /></xsl:attribute>
			<xsl:element name="rim:ServiceBinding" namespace="{$nsrim}">
				<xsl:attribute name="id"><xsl:value-of
					select="concat($idPrefix, $idMiddlefixService, generate-id(swe:field[@name='urn:ogc:def:interface:OGC::ServiceURL' or @name='urn:ogc:def:interface:OGC:1.0:ServiceURL']/swe:Text/swe:value))" /></xsl:attribute>
				<xsl:attribute name="service"><xsl:value-of select="$SERVICE_ID" /></xsl:attribute>
				<xsl:attribute name="accessURI"><xsl:value-of select="$URL" /></xsl:attribute>
				<xsl:element name="rim:Name" namespace="{$nsrim}">
					<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
						<xsl:attribute name="xml:lang"><xsl:value-of
							select="$defaultLocalizedStringLang" /></xsl:attribute>
						<!--
							I do not know ows:ServiceIdentification/ows:Title, use
							sml:interface/@name instead!
						-->
						<xsl:attribute name="value"><xsl:value-of
							select="ancestor::sml:interface/@name" /></xsl:attribute>
					</xsl:element>
				</xsl:element>
				<xsl:element name="rim:Description" namespace="{$nsrim}">
					<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
						<xsl:attribute name="xml:lang"><xsl:value-of
							select="$defaultLocalizedStringLang" /></xsl:attribute>
						<!--
							I do not know ows:ServiceIdentification/ows:Abstract, use what
							instead???
						-->
						<xsl:attribute name="value">TBD</xsl:attribute>
					</xsl:element>
				</xsl:element>

			</xsl:element>
		</xsl:element>

		<!--
			See OGC 07-144r4, section 12.2 "OGC service description"
		-->
		<xsl:element name="rim:Classification" namespace="{$nsrim}">
			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($idPrefix, $idMiddlefixClassification, generate-id())" />
			</xsl:attribute>
			<xsl:attribute name="classifiedObject">
				<xsl:value-of select="$SERVICE_ID" /></xsl:attribute>
			<xsl:attribute name="classificationNode"><xsl:value-of
				select="$CLASSIFICATION_NODE" /></xsl:attribute>
		</xsl:element>

		<xsl:element name="rim:ClassificationNode" namespace="{$nsrim}">
			<xsl:attribute name="id"><xsl:value-of
				select="$CLASSIFICATION_NODE" /></xsl:attribute>
			<xsl:attribute name="parent">
				<xsl:call-template name="deduceServiceParent">
					<xsl:with-param name="type" select="$CLASSIFICATION_NODE" />
				</xsl:call-template>
			</xsl:attribute>
			<!--
				<xsl:element name="rim:Name" namespace="{$nsrim}"> <xsl:element
				name="rim:LocalizedString" namespace="{$nsrim}"> <xsl:attribute
				name="xml:lang"><xsl:value-of select="$defaultLocalizedStringLang"
				/></xsl:attribute> <xsl:attribute name="value"> <xsl:value-of
				select="$TYPE" /></xsl:attribute> </xsl:element> </xsl:element>
			-->
		</xsl:element>

	</xsl:template>

</xsl:transform>