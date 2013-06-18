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
	version="2.0"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd">

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
				<xsl:value-of select="concat($classificationNodeIdPrefix, generate-id(sml:value))" />
			</xsl:attribute>
			<xsl:attribute name="parent"><xsl:value-of select="$classificationSchemeId.IntendedApplication" /></xsl:attribute>

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
				<xsl:value-of select="concat($classificationNodeIdPrefix, generate-id(sml:value))" />
			</xsl:attribute>
			<xsl:attribute name="parent"><xsl:value-of select="$classificationSchemeId.SystemTypes" /></xsl:attribute>

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