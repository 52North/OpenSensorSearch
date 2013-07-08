/*
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

$(document).ready(function() {
	$(".search-input").keyup(function() {
		var p = $(".search-input");
		var written = (p.val());
		if (written.toString().length > 1) {
			var tokens = written.toString().split(" ");
			var first = tokens.slice(0,tokens.length-1).join(" ");
			var value = "/SIR/autocomplete?text=" + tokens[tokens.length-1];
			$.get(value, function(data) {
				var d = data.toString();
				var available = d.split(",");
				for(var i=0;i<available.length;i++)
					available[i]=first+" "+available[i];
				$(".search-input").autocomplete({
					source : available
				});
			});
		}
	})
});