/*
 * BDP4j implements a pipeline framework to allow definining 
 * project pipelines from XML. The main goal of the pipelines of this 
 * application is to transform imput data received from multiple sources 
 * into fully qualified datasets to be used with Machine Learning.
 *
 * Copyright (C) 2018  Sing Group (University of Vigo)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.bdp4j.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class DateIdentifierTest {

    String value;
    Date actual;

    @Before
    public void setUp() {
        value = "";
        actual = new Date();
    }

    @After
    public void tearDown() {
        actual = null;
    }

    @Test
    public void testCheckDate() {
        //EEE MMM dd HH:mm:ss ZZZZ yyyy
        value = "Mon Aug 12 22:29:15 CEST 2002";
        actual = DateIdentifier.getDefault().checkDate(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        //EEE MMM dd HH:mm:ss ZZZ yyyy
        value = "Mon Aug 12 22:29:15 CEST 2002";
        actual = DateIdentifier.getDefault().checkDate(value);
        assertNotNull("The date format is wrong: "+value, actual);

        //EEE, dd MMM yyyy HH:mm:ss ZZZZZ (zzz)
        value = "Mon, 12 Aug 2002 22:29:15 CEST (cest)";
        actual = DateIdentifier.getDefault().checkDate(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
    }
    
    public void testCheckDateNull(){
        actual = DateIdentifier.getDefault().checkDate(null);
        assertNull(actual);
        
        actual = DateIdentifier.getDefault().checkDate("");
        assertNull(actual);
    }
}
