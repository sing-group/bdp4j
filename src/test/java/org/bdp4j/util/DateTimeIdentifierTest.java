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

import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * @author Maria Novo
 * @author Reyes Pavón
 */
public class DateTimeIdentifierTest {
    
    String value;
    LocalDateTime actual;

    @Before
    public void setUp() {
        value = "";
        actual = LocalDateTime.now();
    }

    @After
    public void tearDown() {
        actual = null;
    }

    @Test
    public void testCheckDate() {
        
        // DateTime using localized styles
        value = "Wed Oct 01 00:41:58 CEST 2014";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "30/06/09 07:03:59";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "30-jun-2009 07:03:59";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "30 de junio de 2009 07:03:59";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "martes 30 de junio de 2009 07:03:59";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "30/06/09 07:03";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "30/06/09 07:03:30";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "30/06/09 07H30' CEST";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        // Tuesday, June 30, 2009 07:03:47 AM (ENGLISH)
        value = "Tuesday, June 30, 2009 07:03:47 AM";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual); 
        
        //June 30, 2009 7:03:47 (ENGLISH)
        value = "June 30, 2009 7:03:47 AM";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);       
        
         // 30 June 2009 07:03:47 (UK)
        value = "30 June 2009 07:03:47";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);  
        
         // Tuesday, 30 June 2009 07:03:47 (UK)
        value = "Tuesday, 30 June 2009 07:03:47";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual); 
    
        value = "2015-10-12T23:45";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual); 
        
       // Datetime using patterns
        
        value = "lun mar 25 13:05:34 GMT+01:00 2019";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "lun mar 25 13:05:34 +0100 2019";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "lun, 25 mar 2019 14:19:30 +01:00 (CET)";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
       
        value = "lun, 25 mar 2019 14:21:48 +01:00";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "25 mar 2019 14:23:21 +01:00";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "Tue, Jun 30 07:03:47 2009";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "mar, 25 2019 14:42:33 PM +01:00";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "lun, 25 mar 2019 14:43:59 ++01:00";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "Mon, 25 Mar 2019 18:59:06 CET";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "Mon, Mar 25 19:01:07 CET 2019";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
//        value = " 2019-03-25T13:57:40+01:00"; NO EXISTE ESTE PATRÓN
//        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
//        assertNotNull("The date format is wrong: "+value, actual);
        
        
        // Date using localized styles
        value = "25 Mar 2019";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        
        // Date using patterns
        value = "Wed, 27 Mar 2019";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
        value = "Wed, 6 Mar 2019";
        actual = DateTimeIdentifier.getDefault().checkDateTime(value);
        assertNotNull("The date format is wrong: "+value, actual);
        
    }
    
    public void testCheckDateNull(){
        actual = DateTimeIdentifier.getDefault().checkDateTime(null);
        assertNull(actual);
        
        actual = DateTimeIdentifier.getDefault().checkDateTime("");
        assertNull(actual);
    }
    
}
