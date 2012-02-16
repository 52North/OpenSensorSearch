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
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:gml="http://www.opengis.net/gml"
	xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0"
	xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="2.0"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd 
  http://www.opengis.net/cat/wrs/1.0 http://schemas.opengis.net/csw/2.0.2/profiles/ebrim/1.0/csw-ebrim.xsd">

	<xsl:import href="SensorML-to-ebRIM_classification.xsl" />
	<xsl:import href="SensorML-to-ebRIM_contentForSystemAndComponent.xsl" />

	<xsl:strip-space elements="*" />

	<!-- System -->
	<xsl:template match="sml:System" mode="extrinsic-object">
		<xsl:variable name="UNIQUE_ID">
			<xsl:value-of
				select="sml:identification/sml:IdentifierList/sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::uniqueID' or @definition='urn:ogc:def:identifier:OGC:1.0:uniqueID']/sml:value" />
		</xsl:variable>

		<xsl:element name="wrs:ExtrinsicObject" namespace="{$nswrs}">
			<xsl:attribute name="mimeType">application/xml</xsl:attribute>

			<xsl:attribute name="objectType"><xsl:value-of
				select="$fixedObjectTypeSystem" /></xsl:attribute>
			<xsl:attribute name="lid"><xsl:value-of select="$UNIQUE_ID" /></xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="$UNIQUE_ID" /></xsl:attribute>

			<xsl:apply-templates select="sml:keywords" />
			<xsl:apply-templates select="sml:validTime" />
			<xsl:apply-templates select="sml:capabilities" />
			<xsl:apply-templates select="sml:position" />
			<xsl:apply-templates select="sml:inputs" />
			<xsl:apply-templates select="sml:outputs" />
			<xsl:apply-templates select="sml:identification" />

			<xsl:choose>
				<xsl:when test="count(gml:description) > 0">
					<xsl:apply-templates select="gml:description" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:comment>
						<xsl:value-of select="$message.gmlError" />
					</xsl:comment>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:comment>
				******** classifications ********
			</xsl:comment>
			<xsl:apply-templates select="sml:classification"
				mode="classification">
				<xsl:with-param name="CLASSIFIED_OBJECT_ID">
					<xsl:value-of select="$UNIQUE_ID" />
				</xsl:with-param>
			</xsl:apply-templates>

		</xsl:element>

	</xsl:template>

	<xsl:template match="sml:System" mode="classification-association">
		<xsl:variable name="UNIQUE_ID">
			<xsl:value-of
				select="sml:identification/sml:IdentifierList/sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::uniqueID' or @definition='urn:ogc:def:identifier:OGC:1.0:uniqueID']/sml:value" />
		</xsl:variable>

		<xsl:comment>
			******** classification nodes ********
		</xsl:comment>
		<xsl:apply-templates select="sml:classification"
			mode="classificationNode">
			<xsl:with-param name="CLASSIFIED_OBJECT_ID">
				<xsl:value-of select="$UNIQUE_ID" />
			</xsl:with-param>
		</xsl:apply-templates>

		<xsl:comment>
			******** associations ********
		</xsl:comment>
		<xsl:apply-templates select="sml:components"
			mode="ComposedOf-association">
			<xsl:with-param name="SOURCE_OBJECT_ID">
				<xsl:value-of select="$UNIQUE_ID" />
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="sml:interfaces"
			mode="AccessibleThrough-association">
			<xsl:with-param name="SOURCE_OBJECT_ID">
				<xsl:value-of select="$UNIQUE_ID" />
			</xsl:with-param>
		</xsl:apply-templates>

	</xsl:template>

</xsl:transform>