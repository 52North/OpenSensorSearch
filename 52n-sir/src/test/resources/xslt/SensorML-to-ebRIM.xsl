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
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:gml="http://www.opengis.net/gml/3.2"
	xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0"
	xmlns:sml="http://www.opengis.net/sensorML/1.0.1" version="2.0"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd 
  http://www.opengis.net/cat/wrs/1.0 http://schemas.opengis.net/csw/2.0.2/profiles/ebrim/1.0/csw-ebrim.xsd">

	<!-- imports -->
	<xsl:import href="SensorML-to-ebRIM_variables.xsl" />
	<xsl:import href="SensorML-to-ebRIM_contact.xsl" />
	<xsl:import href="SensorML-to-ebRIM_system.xsl" />
	<xsl:import href="SensorML-to-ebRIM_component.xsl" />

	<!-- remove all unwanted output, i.e. "ignoring all text nodes" -->
	<xsl:template match="text()|@*" />

	<!-- remove blank lines from the output document -->
	<xsl:strip-space elements="*" />

	<xsl:template match="sml:member">

		<xsl:comment>
			This document was created using the 52North Sensor Instance Registry (SIR).
			Homepage: https://52north.org/communities/sensorweb/incubation/discovery/
			Contact: daniel.nuest@uni-muenster.de
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

			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($idPrefix, $idMiddlefixRegPack, generate-id(sml:System))" />
			</xsl:attribute>

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

			</xsl:element><!-- rim:RegistryObjectList -->
		</xsl:element><!-- rim:RegistryPackage -->

	</xsl:template>

</xsl:transform>