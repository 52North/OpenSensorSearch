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
<!-- ========================================================================== 
	SensorML Profile for Discovery of Sensor Platforms
	
	A Schematron (ISO 19757-3) Schema For SensorML 1.0.1 Version 0.1 
	=========================================================================== 
	
	
	DESCRIPTION:
	This file comprises a Schematron[1] schema for the Sensor Model Language 
	(SensorML) [2] defined by the OGC [3]. This profile can be considered as 
	a means to create SensorML documents for the discovery of sensor platforms 
	(e.g. weather stations). The platform is modelled as a 'System' while its 
	attached sensors are described as 'Components'. The profile is especially 
	intended to be applied to SensorML documents returned by the DescribeSensor 
	operation of a Sensor Observation Service [4].
	
	The flexible structure of 	SensorML is reduced by this profile. It
	restricts the SensorML schema to a sufficient subset which can be used to
	model sensor platforms with intention to be discoverable.
	
	It defines several "optional" elements and attributes as "required" for the
	definition of stations. So, the profile definition eases the use of
	SensorML on the client-side. If services comply to the profile restrictions
	the clients can refer to them. A client which has to make use of SensorML
	descriptions of stations doesn't have to support the whole SensorML schema,
	but just the restricted one defined by the profile.
	
	Please provide any comments or feedback on this validator to Daniel Nüst
	(d.nuest@52north.org).
	
	REFERENCES:
	[1] http://www.schematron.com/iso/Schematron.html
	[2] http://www.opengeospatial.org/standards/sensorml 
	[3] http://www.opengeospatial.org
	[4] http://www.opengeospatial.org/standards/sos
	
	-->
<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:sml="http://www.opengis.net/sensorML/1.0.1"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:swe="http://www.opengis.net/swe/1.0.1"
	xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink"
	xsi:schemaLocation="http://www.opengis.net/sensorML/1.0.1 http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd"
	schemaVersion="ISO19757-3">
	<ns prefix="sml" uri="http://www.opengis.net/sensorML/1.0.1" />
	<ns prefix="gml" uri="http://www.opengis.net/gml" />
	<ns prefix="swe" uri="http://www.opengis.net/swe/1.0.1" />
	<ns prefix="xlink" uri="http://www.w3.org/1999/xlink" />
	<!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
	<!-- System Validation -->
	<!-- This pattern validates the parts of the SensorML document which are 
		specific for a System. -->
	<!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
	<pattern id="SystemValidation">
		<!-- A SensorML document contains one "member" element. Each "member" contains 
			a "Process" instance (either a "Component" or a "System") which shall be 
			discoverable. -->
		<rule context="/">
			<assert test="count(sml:SensorML/sml:member) = 1">Error: station description must have exactly one
				'member'.
			</assert>
			<assert test="count(sml:SensorML/sml:member/sml:System) = 1">Error: 'member' element must contain one 'System'
				element.
			</assert>
		</rule>

		<!-- Each System must contain information about the time it is valid. As 
			the SensorML schema defines this can be either a gml:TimePeriod or a gml:TimeInstant 
			describing the instant in time at which the stated metadata about the System 
			has become valid. The "System" has to contain a "position" element which 
			defines the position of the station itself. -->
		<rule context="//sml:System">
			<assert test="sml:validTime">Error: 'validTime' element has to be present
			</assert>
			<assert test="sml:position/swe:Position">Error: 'sml:System/sml:position/swe:Position' has
				to be present.
			</assert>
		</rule>

		<!-- A "swe:Envelope" must be provided describing the bounding box of the 
			area that is observed by the sensor. In case of an in-situ sensor this bounding 
			box only contains the position of the sensor. One "swe:field" of the "DataRecord" 
			has to contain a "swe:Envelope" element with the definition "urn:ogc:def:property:OGC:1.0:observedBBOX". 
			It describes the bounding box of the area that is observed by the System. 
			In case of an in-situ sensor this bouding box only contains the position 
			of the sensor. -->
		<rule context="//sml:System">
			<assert
				test="count(sml:capabilities/swe:DataRecord/swe:field/swe:Envelope[@definition = 'urn:ogc:def:property:OGC:1.0:observedBBOX']) = 1">Error: one "swe:field" of a "swe:DataRecord" in a "sml:capabilities" has to contain a
				"swe:Envelope" element with the definition
				"urn:ogc:def:property:OGC:1.0:observedBBOX".
			</assert>
		</rule>

		<!-- A "position" element has to contain a "swe:Position" which uses the 
			"referenceFrame" attribute to specify its spatial reference system. -->
		<rule context="//sml:position/swe:Position">
			<assert test="@referenceFrame">Error: 'referenceFrame' attribute has to be
				present.
			</assert>
		</rule>

		<!-- The "swe:Position" element contains a "swe:location" which specifies 
			at least 2 "swe:Vector/swe:coordinate/swe:Quantity" elements.. -->
		<rule context="//sml:position/swe:Position/swe:location">
			<assert test="count(swe:Vector/swe:coordinate/swe:Quantity) > 1">Error: 'swe:location' has to specify at least 2
				'swe:Vector/swe:coordinate/swe:Quantity' elements.
			</assert>
		</rule>

		<!-- It is required that this "swe:Quantity" uses the "axisID" attribute 
			to specify the axis to which it refers. Further more the "swe:value" element 
			is utilized to specify the coordinate value. The "swe:uom" element with a 
			"code" attribute defines the unit of the coordinate value -->
		<rule
			context="//sml:position/swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity">
			<assert test="string-length(@axisID) > 0">Error: 'axisID' attribute has to be present and
				its value has to be > 0.
			</assert>
			<assert test="swe:value">Error: 'swe:value' element has to be present.
			</assert>
			<assert test="swe:uom[@code]">Error: 'swe:uom' element and its "code" attribute
				have to be present.
			</assert>
		</rule>

		<!-- Every "sml:position" definition in the document has to fulfill: One 
			"swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity" has to 
			specify an "axisID" named "x" which refers to the x-axis (= easting- / longitude-axis) 
			of the crs. One "swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity" 
			has to specify an "axisID" named "y" which refers to the y-axis (= northing- 
			/ latitude-axis) of the crs. An optional "swe:coordinate/swe:Quantity" may 
			specify an "axisID" named "z" which refers to the z-axis (= altitude-axis) 
			of the crs. -->
		<rule context="//sml:position">
			<assert
				test="count(swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID = 'x']) = 1">Error: one x-axis coordinate has to be specified.</assert>
			<assert
				test="count(swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID = 'y']) = 1">Error: one y-axis coordinate has to be specified.</assert>
			<assert
				test="count(swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID = 'z']) = 0
                or count(swe:Position/swe:location/swe:Vector/swe:coordinate/swe:Quantity[@axisID = 'z']) = 1">Error: one z-axis coordinate may be specified.</assert>
		</rule>

		<!-- 'sml:component' must contain EITHER attribute 'xlink:href' OR child 
			'sml:Component'. -->
		<rule context="//sml:System/sml:components/sml:ComponentList/sml:component">
			<!-- the following expression means: XOR -->
			<assert
				test="(@xlink:href and not(sml:Component)) or (not (@xlink:href) and sml:Component)">Error: 'sml:component' must contain EITHER attribute
				'xlink:href' OR child 'sml:Component'
			</assert>
		</rule>
	</pattern>

	<pattern id="ComponentValidation">
		<!-- At least one classifier has to contain the definition "urn:ogc:def:classifier:OGC:1.0:sensorType". 
			The value of its contained "Term" element states the type of the sensor. -->
		<rule context="//sml:Component/sml:classification">
			<assert
				test="count(sml:ClassifierList/sml:classifier/sml:Term[@definition = 'urn:ogc:def:classifier:OGC:1.0:sensorType']) >= 1">Error: At least one classifier has to be of the type
				'urn:ogc:def:classifier:OGC:1.0:sensorType'.
			</assert>
		</rule>
	</pattern>

	<pattern id="GeneralValidation">

		<!-- A "description", "keywords", "contact", "inputs" and "outputs" element 
			has to be present. -->
		<rule context="//sml:System">
			<assert test="gml:description">Error: 'gml:description' element has to be
				present
			</assert>
			<assert test="sml:keywords/sml:KeywordList">Error: 'KeywordList' element has to be present
			</assert>
			<assert test="sml:contact">Error: 'sml:contact' element has to be present
			</assert>
			<assert test="sml:inputs">Error: 'sml:inputs' has to be present.</assert>
			<assert test="sml:outputs">Error: 'sml:outputs' has to be present.</assert>
			<assert test="sml:classification">Error: 'sml:classification' has to be present.
			</assert>
		</rule>
		<rule context="//sml:Component">
			<assert test="gml:description">Error: 'gml:description' element has to be
				present
			</assert>
			<assert test="sml:keywords/sml:KeywordList">Error: 'KeywordList' element has to be present
			</assert>
			<assert test="sml:contact">Error: 'sml:contact' element has to be present
			</assert>
			<assert test="sml:inputs">Error: 'sml:inputs' has to be present.</assert>
			<assert test="sml:outputs">Error: 'sml:outputs' has to be present.</assert>
			<assert test="sml:classification">Error: 'sml:classification' has to be present.
			</assert>
		</rule>

		<!-- Each "identifier/Term" element contained in the "IdentifierList" must 
			have a "definition" attribute. This attribute links to the semantics of the 
			identifier. -->
		<rule
			context="//sml:identification/sml:IdentifierList/sml:identifier/sml:Term">
			<assert test="string-length(@definition) > 0">Error: 'definition' attribute has to be present
				and its value has to be > 0.
			</assert>
		</rule>

		<!-- One identifier has to contain the definition "urn:ogc:def:identifier:OGC:1.0:uniqueID". 
			The value of its contained "Term" element uniquely identifies the instance. 
			One identifier has to contain the definition "urn:ogc:def:identifier:OGC:1.0:longName". 
			The value of its contained "Term" element represents a human understandable 
			name for the instance. One identifier has to contain the definition "urn:ogc:def:identifier:OGC:1.0:shortName". 
			The value of its contained "Term" element represents a short representation 
			of the human understandable name for the instance. -->
		<rule context="//sml:identification">
			<assert
				test="count(sml:IdentifierList/sml:identifier/sml:Term[@definition = 'urn:ogc:def:identifier:OGC:1.0:uniqueID']) = 1">Error: one identifier has to be of the type
				'urn:ogc:def:identifier:OGC:1.0:uniqueID'.
			</assert>
			<assert
				test="count(sml:IdentifierList/sml:identifier/sml:Term[@definition = 'urn:ogc:def:identifier:OGC:1.0:longName']) = 1">Error: one identifier has to be of the type
				'urn:ogc:def:identifier:OGC:1.0:longName'.
			</assert>
			<assert
				test="count(sml:IdentifierList/sml:identifier/sml:Term[@definition = 'urn:ogc:def:identifier:OGC:1.0:shortName']) = 1">Error: one identifier has to be of the type
				'urn:ogc:def:identifier:OGC:1.0:shortName'.
			</assert>
		</rule>

		<!-- One identifier has to contain the definition "urn:ogc:def:identifier:OGC:parentSystemUniqueID". 
			The value of its contained "Term" element refers to the system that contains 
			the component. -->
		<rule context="//sml:Component/sml:identification">
			<assert
				test="count(sml:IdentifierList/sml:identifier/sml:Term[@definition = 'urn:ogc:def:identifier:OGC:parentSystemUniqueID']) = 1">Error: one identifier has to be of the type
				'urn:ogc:def:identifier:OGC:parentSystemUniqueID'.
			</assert>
		</rule>

		<!-- ~~~~~~~~~~~~~~ -->
		<!-- Classification -->
		<!-- ~~~~~~~~~~~~~~ -->
		<rule context="//sml:classification">
			<assert
				test="count(sml:ClassifierList/sml:classifier/sml:Term[@definition = 'urn:ogc:def:classifier:OGC:1.0:sensorType']) >= 1">Error: one classifier for the sensorType classification scheme
				has to be given!
			</assert>
			<assert
				test="count(sml:ClassifierList/sml:classifier/sml:Term[@definition = 'urn:ogc:def:classifier:OGC:1.0:application']) >= 1">Error: one classifier for the intendedApplication
				classification scheme has to be given!
			</assert>
		</rule>

		<!-- Each "classifier/Term" element contained in the "ClassifierList" must 
			have a "definition" attribute. This attribute links to the semantics of the 
			identifier. -->
		<rule context="//sml:classifier/sml:Term">
			<assert test="string-length(@definition) > 0">Error: 'definition' attribute has to be present
				and its value has to be > 0.
			</assert>
		</rule>

		<!--~~~~~~~~~~~~~ -->
		<!-- Capabilities -->
		<!--~~~~~~~~~~~~~ -->
		<!-- A "swe:DataRecord" containing a number of "swe:field" elements must 
			be used here to specify the capabilities of the "System". The child-element 
			of each "swe:Field" element has to contain a "definition" attribute. -->
		<rule context="//sml:capabilities/swe:DataRecord/swe:field">
			<assert test="string-length(child::node()[@definition]) > 0">Error: 'definition' attribute has to be present
				and its value has to be > 0.
			</assert>
		</rule>
		<!-- If the child-element of the "swe:Field" is a "swe:Quantity" it has 
			to contain the "swe:uom" element which specifies the "code" attribute. -->
		<rule
			context="//sml:capabilities/swe:DataRecord/swe:field/swe:Quantity/swe:uom">
			<assert test="string-length(@code) > 0">Error: 'code' attribute has to be present and its
				value has to be > 0.
			</assert>
		</rule>

		<!-- ~~~~~~ -->
		<!--Contact -->
		<!-- ~~~~~~ -->
		<!-- Within a "contact" element the organization name has to be present. -->
		<rule context="//sml:contact/sml:ResponsibleParty">
			<assert test="sml:organizationName">Error: 'sml:organizationName' element has to be
				present
			</assert>
		</rule>

		<!-- Each "input" contains a "swe:ObservableProperty" which uses the "definition"-attribute 
			to specify the observed phenomenon. -->
		<rule context="//sml:inputs/sml:InputList/sml:input">
			<assert test="swe:ObservableProperty/@definition">Error: 'swe:ObservableProperty' has to contain
				'definition' attribute.
			</assert>
		</rule>

		<!-- Each child-element of an "output" has to use the "definition"-attribute 
			to specify the URN identifier of the observed phenomenon. -->
		<rule context="//sml:outputs/sml:OutputList/sml:output">
			<assert test="child::node()[@definition]">Error: child-node of 'output' has to contain
				'definition' attribute.
			</assert>
		</rule>

		<!-- If the child-element of the output is a "swe:Quantity" it has to contain 
			the "swe:uom" element which specifies the "code" attribute. -->
		<rule context="//sml:outputs/sml:OutputList/sml:output/swe:Quantity">
			<assert test="string-length(swe:uom/@code) > 0">Error: 'code' attribute has to be present and its
				value has to be > 0.
			</assert>
		</rule>
	</pattern>
</schema>
