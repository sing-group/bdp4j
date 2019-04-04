
package org.bdp4j.util;

import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * @author 
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
