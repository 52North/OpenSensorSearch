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
	version="2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:sml="http://www.opengis.net/sensorML/1.0.1"
	xsi:schemaLocation="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0 http://docs.oasis-open.org/regrep/v3.0/schema/rim.xsd">

	<!-- CONTACT -->
	<xsl:template match="sml:contact">
		<xsl:apply-templates select="sml:ResponsibleParty"
			mode="organization" />

		<xsl:if test="sml:ResponsibleParty/sml:individualName">
			<xsl:apply-templates select="sml:ResponsibleParty/sml:individualName"
				mode="primaryContact" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="sml:ResponsibleParty" mode="organization">

		<xsl:element name="rim:Organization" namespace="{$nsrim}">

			<!-- base id on organizationName as it is the only required field -->
			<xsl:attribute name="id"><xsl:value-of
				select="concat($idPrefix, $idMiddlefixOrganization, generate-id(sml:organizationName))" />
			</xsl:attribute>

			<xsl:if test="count(sml:individualName)>0">
				<xsl:attribute name="primaryContact">
					<xsl:value-of
					select="concat($idPrefix, $idMiddlefixOrganization, generate-id(sml:individualName))" />
				</xsl:attribute>
			</xsl:if>

			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="sml:individualName" mode="primaryContact">
		<xsl:element name="rim:Person" namespace="{$nsrim}">
			<xsl:attribute name="id">
				<xsl:value-of
				select="concat($idPrefix, $idMiddlefixPerson, generate-id(.))" />
			</xsl:attribute>
			<xsl:element name="rim:PersonName" namespace="{$nsrim}">
				<xsl:choose>
					<xsl:when test="contains(., ',')">
						<xsl:attribute name="lastName">
							<xsl:value-of
							select="normalize-space(translate(substring-before(., ','), ',', ' '))" />
						</xsl:attribute>
						<xsl:attribute name="firstName">
							<xsl:value-of select="normalize-space(substring-after(., ','))" />
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="contains(., ' ')">
						<xsl:attribute name="lastName">
							<xsl:value-of select="normalize-space(substring-after(., ' '))" />
						</xsl:attribute>
						<xsl:attribute name="firstName">
							<xsl:value-of select="normalize-space(substring-before(., ' '))" />
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="lastName">
					<xsl:value-of select="normalize-space(.)" /></xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="sml:organizationName">
		<xsl:element name="rim:Name" namespace="{$nsrim}">
			<xsl:element name="rim:LocalizedString" namespace="{$nsrim}">
				<xsl:attribute name="value">
				<xsl:value-of select="normalize-space(.)" />
			</xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="sml:contactInfo">
		<xsl:apply-templates mode="address" />
		<xsl:if test="sml:address/sml:electronicMailAddress">
			<xsl:apply-templates select="sml:address/sml:electronicMailAddress"
				mode="email" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="sml:phone" mode="address">
		<!-- ignore phones -->
	</xsl:template>

	<xsl:template match="sml:address" mode="address">
		<xsl:element name="rim:Address" namespace="{$nsrim}">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<xsl:template match="sml:deliveryPoint">
		<xsl:attribute name="street">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="sml:city">
		<xsl:attribute name="city">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="sml:postalCode">
		<xsl:attribute name="postalCode">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="sml:country">
		<xsl:attribute name="country">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="sml:address/sml:electronicMailAddress"
		mode="email">
		<xsl:element name="rim:EmailAddress">
			<xsl:attribute name="address">
				<xsl:value-of select="." />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:transform>