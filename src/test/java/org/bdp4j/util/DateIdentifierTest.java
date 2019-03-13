/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        assertNotNull(actual);
        
        //EEE MMM dd HH:mm:ss ZZZ yyyy
        value = "Mon Aug 12 22:29:15 CET 2002";
        actual = DateIdentifier.getDefault().checkDate(value);
        assertNotNull(actual);

        //EEE, dd MMM yyyy HH:mm:ss ZZZZZ (zzz)
        value = "Mon, 12 Aug 2002 22:29:15 CEST (cest)";
        actual = DateIdentifier.getDefault().checkDate(value);
        assertNotNull(actual);
    }
    
    public void testCheckDateNull(){
        actual = DateIdentifier.getDefault().checkDate(null);
        assertNull(actual);
        
        actual = DateIdentifier.getDefault().checkDate("");
        assertNull(actual);
    }
}
