<%--

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

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div id="header">
	<div id="headline"><a href="<%= request.getContextPath() %>" title="Home">
		<span class="title">SOR</span><span class="infotext">Sensor Observable Registry<br />Test Client - Version 0.3</span></a>
	</div>
	<div id="logos">
		<a href="http://52north.org" title="52°North Initiative for Geospatial Open Source Software">
			<img src="<%= request.getContextPath() %>/images/logo.gif" height="62" alt="52N logo" /></a>
		<a href="http://www.genesis-fp7.eu/" title="EC Project: GENeric European Sustainable Information Space for environment">
			<img class="imageSpace" src="<%= request.getContextPath() %>/images/genesis_logo_blue_border__200.jpg" height="63" alt="GENESIS logo" /></a>
		<a href="http://ifgi.uni-muenster.de/" title="Institute for Geoinformatics, University of Münster">
			<img src="<%= request.getContextPath() %>/images/ifgilogo.gif" width="180" height="63" alt="ifgi logo" /></a>
	</div>
	
</div>