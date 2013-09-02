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
	version="2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:sml="http://www.opengis.net/sensorML/1.0.1"
	xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd">

	<!-- NAMES AND DESCRIPTION -->
	<xsl:template match="sml:identification/sml:IdentifierList">
		<!--
			explicit applying because rim:Name element (with longName) has to be
			after rim:Slot element (with shortName)!
		-->
		<xsl:apply-templates
			select="sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::shortName' or @definition='urn:ogc:def:identifier:OGC:1.0:shortName']" />
		<xsl:apply-templates
			select="sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::longName' or @definition='urn:ogc:def:identifier:OGC:1.0:longName']" />
	</xsl:template>
	<xsl:template
		match="sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::longName' or @definition='urn:ogc:def:identifier:OGC:1.0:longName']">
		<xsl:element name="rim:Name" namespace="{$nsrim}">
			<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
				<xsl:attribute name="xml:lang"><xsl:value-of
					select="$defaultLocalizedStringLang" /></xsl:attribute>
				<xsl:attribute name="value">
					<xsl:apply-templates />
				</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template
		match="sml:identifier/sml:Term[@definition='urn:ogc:def:identifier:OGC::shortName' or @definition='urn:ogc:def:identifier:OGC:1.0:shortName']">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'ShortName')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($oasisDataTypePrefix, 'String')" /></xsl:attribute>
			<xsl:element name="rim:ValueList" namespace="{$nsrim}">
				<xsl:element name="rim:Value" namespace="{$nsrim}">
					<xsl:apply-templates />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="gml:description">
		<!--
			only transform descriptions if the parent is sml:System or
			sml:Component
		-->
		<xsl:if test="parent::sml:System|parent::sml:Component">
			<xsl:element name="rim:Description" namespace="{$nsrim}">
				<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
					<xsl:attribute name="xml:lang"><xsl:value-of
						select="$defaultLocalizedStringLang" /></xsl:attribute>
					<xsl:attribute name="value">
						<xsl:value-of select="." />
					</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="sml:value">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>

	<!-- CAPABILITIES -->
	<xsl:template match="sml:capabilities/swe:DataRecord">
		<!-- all other fields are ignored! -->
		<xsl:apply-templates select="swe:field[@name='observedBBOX']" />
	</xsl:template>

	<xsl:template match="swe:field[@name='observedBBOX']/swe:Envelope">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'BoundedBy')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($iso19107DataTypePrefix, 'GM_Envelope')" /></xsl:attribute>
			<xsl:choose>
				<!-- do not use SWE namespace for compatibility issues -->
				<xsl:when test="$noSWE">
					<xsl:element name="wrs:ValueList" namespace="{$nswrs}">
						<xsl:element name="wrs:AnyValue" namespace="{$nswrs}">
							<xsl:element name="gml:Envelope" namespace="{$nsgml}">
								<xsl:attribute name="srsName"><xsl:value-of
									select="@referenceFrame" /></xsl:attribute>
								<xsl:apply-templates mode="buildGmlEnvelope" />
							</xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="wrs:ValueList" namespace="{$nswrs}">
						<xsl:element name="wrs:AnyValue" namespace="{$nswrs}">
							<!--
								construct envelope manually for correct/consistent namespaces
							-->
							<xsl:element name="swe:Envelope" namespace="{$nsswe}">
								<xsl:attribute name="definition"><xsl:value-of
									select="@definition" /></xsl:attribute>

								<xsl:apply-templates mode="buildEnvelope" />
							</xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template match="gml:description" mode="buildGmlEnvelope">
		<xsl:comment>
			<xsl:value-of select="."></xsl:value-of>
		</xsl:comment>
	</xsl:template>

	<xsl:template match="swe:lowerCorner" mode="buildGmlEnvelope">
		<xsl:element name="gml:lowerCorner" namespace="{$nsgml}">
			<xsl:apply-templates mode="buildGmlEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:upperCorner" mode="buildGmlEnvelope">
		<xsl:element name="gml:upperCorner" namespace="{$nsgml}">
			<xsl:apply-templates mode="buildGmlEnvelope" />
		</xsl:element>
	</xsl:template>

	<!-- set axis order to "y x", though this may differe depending on the used 
		coordinate reference system -->
	<xsl:template match="swe:Vector" mode="buildGmlEnvelope">
		<xsl:value-of
			select="concat(swe:coordinate/swe:Quantity[@axisID='y']/swe:value, ' ', swe:coordinate/swe:Quantity[@axisID='x']/swe:value)" />
	</xsl:template>

	<xsl:template match="swe:lowerCorner" mode="buildEnvelope">
		<xsl:element name="swe:lowerCorner" namespace="{$nsswe}">
			<xsl:apply-templates mode="buildEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:upperCorner" mode="buildEnvelope">
		<xsl:element name="swe:upperCorner" namespace="{$nsswe}">
			<xsl:apply-templates mode="buildEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:Vector" mode="buildEnvelope">
		<xsl:element name="swe:Vector" namespace="{$nsswe}">
			<xsl:apply-templates mode="buildEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:coordinate" mode="buildEnvelope">
		<xsl:element name="swe:coordinate" namespace="{$nsswe}">
			<xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
			<xsl:apply-templates mode="buildEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:Quantity" mode="buildEnvelope">
		<xsl:element name="swe:Quantity" namespace="{$nsswe}">
			<xsl:attribute name="axisID"><xsl:value-of select="@axisID" /></xsl:attribute>
			<xsl:apply-templates mode="buildEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:uom" mode="buildEnvelope">
		<xsl:element name="swe:uom" namespace="{$nsswe}">
			<xsl:attribute name="code"><xsl:value-of select="@code" /></xsl:attribute>
			<xsl:apply-templates mode="buildEnvelope" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="swe:value" mode="buildEnvelope">
		<xsl:element name="swe:value" namespace="{$nsswe}">
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

	<!-- KEYWORDS -->
	<xsl:template match="sml:keywords/sml:KeywordList">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'Keywords')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($oasisDataTypePrefix, 'String')" /></xsl:attribute>
			<xsl:element name="rim:ValueList" namespace="{$nsrim}">
				<xsl:apply-templates select="sml:keyword" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="sml:keyword">
		<xsl:element name="rim:Value" namespace="{$nsrim}">
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

	<!-- INPUTS -->
	<xsl:template match="sml:inputs/sml:InputList">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'Inputs')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($oasisDataTypePrefix, 'String')" /></xsl:attribute>
			<xsl:element name="rim:ValueList" namespace="{$nsrim}">
				<xsl:apply-templates select="sml:input/swe:ObservableProperty" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="swe:ObservableProperty">
		<xsl:element name="rim:Value" namespace="{$nsrim}">
			<xsl:value-of select="@definition" />
		</xsl:element>
	</xsl:template>

	<!-- OUTPUTS -->
	<xsl:template match="sml:outputs/sml:OutputList">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'Outputs')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($oasisDataTypePrefix, 'String')" /></xsl:attribute>
			<xsl:element name="rim:ValueList" namespace="{$nsrim}">
				<xsl:apply-templates select="sml:output/swe:Quantity" />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="sml:output/swe:Quantity">
		<xsl:element name="rim:Value" namespace="{$nsrim}">
			<xsl:value-of select="@definition" />
		</xsl:element>
	</xsl:template>

	<!-- POSITION/LOCATION -->
	<xsl:template match="sml:position/swe:Position">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'Location')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($iso19107DataTypePrefix, 'GM_Point')" /></xsl:attribute>
			<xsl:element name="wrs:ValueList" namespace="{$nswrs}">
				<xsl:element name="wrs:AnyValue" namespace="{$nswrs}">
					<!--
						workaround for usage of <spatialReferenceFrame> in SensorML: If a
						Component refers to a locally defined spatial reference frame,
						then use the position of the enframing System. The test is done by
						checking for a valid EPSG code, i.e. if the attribute contains
						'EPSG:'.
					-->
					<xsl:if test="$debugOn">
						<xsl:message terminate="no">
							<xsl:text> Testing referenceFrame attribute: </xsl:text>
							<xsl:value-of select="@referenceFrame"></xsl:value-of>
							<xsl:text> -- </xsl:text>
							<xsl:value-of select="starts-with(@referenceFrame, $epsgUrnPrefix)"></xsl:value-of>
						</xsl:message>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="starts-with(@referenceFrame, $epsgUrnPrefix)">
							<xsl:element name="gml:Point" namespace="{$nsgml}">
								<xsl:attribute name="gml:id">
									<xsl:value-of select="generate-id(swe:location)" />
								</xsl:attribute>
								<xsl:attribute name="srsName">
									<xsl:value-of select="@referenceFrame" />
								</xsl:attribute>
								<xsl:call-template name="generateGmlPos">
									<xsl:with-param name="swePosition" select="." />
								</xsl:call-template>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:comment>
								<xsl:text>Could not detect valid EPSG code in swe:Position/@referenceSystem, falling back to ancestor sml:System position definition.</xsl:text>
							</xsl:comment>
							<xsl:element name="gml:Point" namespace="{$nsgml}">
								<xsl:variable name="ancestorPosition"
									select="ancestor::sml:System/sml:position/swe:Position" />
								<xsl:attribute name="gml:id">
									<xsl:value-of select="generate-id(swe:location)" />
								</xsl:attribute>
								<xsl:attribute name="srsName">
									<xsl:value-of select="$ancestorPosition/@referenceFrame" />
								</xsl:attribute>
								<xsl:call-template name="generateGmlPos">
									<xsl:with-param name="swePosition" select="$ancestorPosition" />
								</xsl:call-template>
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="generateGmlPos">
		<xsl:param name="swePosition" />
		<xsl:element name="gml:pos" namespace="{$nsgml}">
			<xsl:value-of
				select="concat($swePosition/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID='y']/swe:value, ' ', $swePosition/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID='x']/swe:value)" />
		</xsl:element>
		<xsl:if
			test="$swePosition/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID='z']">
			<xsl:comment>
				<xsl:text>z-value '</xsl:text>
				<xsl:value-of
					select="$swePosition/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID='z']/swe:value" />
				<xsl:text>' omitted!</xsl:text>
			</xsl:comment>
		</xsl:if>
	</xsl:template>

	<!-- VALID TIME -->
	<xsl:template match="sml:validTime">
		<xsl:choose>
			<xsl:when test="count(gml:TimePeriod) > 0">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>
					"gml:TimePeriod" in sml:validTime not found! Maybe I cannot match
					the gml namespace?
				</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="gml:TimePeriod" name="gmlPeriod">
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'ValidTimeBegin')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($oasisDataTypePrefix, 'DateTime')" /></xsl:attribute>
			<xsl:element name="rim:ValueList" namespace="{$nsrim}">
				<xsl:element name="rim:Value" namespace="{$nsrim}">
					<xsl:value-of select="gml:beginPosition" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="rim:Slot" namespace="{$nsrim}">
			<xsl:attribute name="name"><xsl:value-of
				select="concat($slotPrefix, 'ValidTimeEnd')" /></xsl:attribute>
			<xsl:attribute name="slotType"><xsl:value-of
				select="concat($oasisDataTypePrefix, 'DateTime')" /></xsl:attribute>
			<xsl:element name="rim:ValueList" namespace="{$nsrim}">
				<xsl:element name="rim:Value" namespace="{$nsrim}">
					<xsl:value-of select="gml:endPosition" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:transform>