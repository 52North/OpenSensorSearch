/**
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

package org.n52.sir.json;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeSensorDescription {

    private ObjectMapper mapper;
    private ByteArrayOutputStream baos;

    @Before
    public void setUp() throws Exception {
        this.mapper = MapperFactory.getMapper();
        this.baos = new ByteArrayOutputStream();
    }

    @Test
    public void test() throws Exception {
        SensorDescription sd = Util.getSensorDescription();
        this.mapper.writeValue(this.baos, sd);
        System.out.println(new String(this.baos.toByteArray()));

        System.out.println("");
        
        sd.getBoundingBox().setSrid(4326);
        this.mapper.writeValue(this.baos, sd);
        System.out.println(new String(this.baos.toByteArray()));
    }

}
