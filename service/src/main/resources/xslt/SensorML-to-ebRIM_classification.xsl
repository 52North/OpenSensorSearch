<?xml version="1.0" encoding="UTF-8"?>
<!--

	﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software 
	GmbH

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
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:sml="http://www.opengis.net/sensorML/1.0.1"
	version="2.0"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd  http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">

	<xsl:strip-space elements="*" />

	<!--
		Classification to a ClassificationNode of the "IntendedApplication"
		ClassificationScheme, used as an internal classification - references
		an existing classification node, see OASIS regrep-rim-3.0
	-->
	<xsl:template mode="classification"
		match="sml:classification/sml:ClassifierList/sml:classifier/sml:Term[@definition='urn:ogc:def:classifier:OGC:1.0:application' or @definition='urn:ogc:def:classifier:OGC::application']">
		<xsl:param name="CLASSIFIED_OBJECT_ID" />

		<xsl:element name="rim:Classification" namespace="{$nsrim}">
			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($idPrefix, $idMiddlefixClassification, generate-id())" />
			</xsl:attribute>
			<xsl:attribute name="classifiedObject">
				<xsl:value-of select="$CLASSIFIED_OBJECT_ID" /></xsl:attribute>
			<xsl:attribute name="classificationNode">
				<xsl:value-of
				select="concat($classificationNodeIdPrefix, generate-id(sml:value))" />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template mode="classificationNode"
		match="sml:classification/sml:ClassifierList/sml:classifier/sml:Term[@definition='urn:ogc:def:classifier:OGC:1.0:application'
		or @definition='urn:ogc:def:classifier:OGC::application']">
		<xsl:element name="rim:ClassificationNode" namespace="{$nsrim}">
			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($classificationNodeIdPrefix, generate-id(sml:value))" />
			</xsl:attribute>
			<xsl:attribute name="parent"><xsl:value-of
				select="$classificationSchemeId.IntendedApplication" /></xsl:attribute>

			<xsl:element name="rim:Name" namespace="{$nsrim}">
				<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
					<xsl:attribute name="xml:lang"><xsl:value-of
						select="$defaultLocalizedStringLang" /></xsl:attribute>
					<xsl:attribute name="value">
						<xsl:value-of select="normalize-space(sml:value)" /></xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<!--
		Classification to a ClassificationNode of the "SystemType"
		ClassificationScheme, used as an internal classification - references
		an existing classification node, see OASIS regrep-rim-3.0
	-->
	<xsl:template mode="classification"
		match="sml:classification/sml:ClassifierList/sml:classifier/sml:Term[@definition='urn:ogc:def:classifier:OGC:1.0:sensorType' or @definition='urn:ogc:def:classifier:OGC::sensorType']">
		<xsl:param name="CLASSIFIED_OBJECT_ID" />

		<xsl:element name="rim:Classification" namespace="{$nsrim}">
			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($idPrefix, $idMiddlefixClassification, generate-id())" />
			</xsl:attribute>
			<xsl:attribute name="classifiedObject">
				<xsl:value-of select="$CLASSIFIED_OBJECT_ID" /></xsl:attribute>
			<xsl:attribute name="classificationNode">
				<xsl:value-of
				select="concat($classificationNodeIdPrefix, generate-id(sml:value))" />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template mode="classificationNode"
		match="sml:classification/sml:ClassifierList/sml:classifier/sml:Term[@definition='urn:ogc:def:classifier:OGC:1.0:sensorType' or @definition='urn:ogc:def:classifier:OGC::sensorType']">
		<xsl:element name="rim:ClassificationNode" namespace="{$nsrim}">
			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($classificationNodeIdPrefix, generate-id(sml:value))" />
			</xsl:attribute>
			<xsl:attribute name="parent"><xsl:value-of
				select="$classificationSchemeId.SystemTypes" /></xsl:attribute>

			<xsl:element name="rim:Name" namespace="{$nsrim}">
				<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
					<xsl:attribute name="xml:lang"><xsl:value-of
						select="$defaultLocalizedStringLang" /></xsl:attribute>
					<xsl:attribute name="value">
						<xsl:value-of select="normalize-space(sml:value)" /></xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="normalized-substring-after-last-colon">
		<xsl:param name="string" />
		<xsl:variable name="delimiter" select="':'" />
		<xsl:choose>
			<xsl:when test="contains($string, $delimiter)">
				<xsl:call-template name="normalized-substring-after-last-colon">
					<xsl:with-param name="string"
						select="substring-after($string, $delimiter)" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="translate(normalize-space($string), ' ', '_')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:transform>