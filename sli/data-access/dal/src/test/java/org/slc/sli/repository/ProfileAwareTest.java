package org.slc.sli.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ProfileAwareTest {
    
    private static final String PROP_VALUE = "DAL Context Test Property";
    
    @Autowired
    @Value("${sli.test.prop}")
    private String testprop = null;

    public String getTestprop() {
        return testprop;
    }

    public void setTestprop(String prop) {
        testprop = prop;
    }
    
    @Autowired
    @Value("${sli.env}")
    private String slienv = null;

    public void setSlienv(String prop) {
        slienv = prop;
    }

    public String getSlienv() {
        return slienv;
    }
    
    @Test
    public void testProfileAwareness() {
        assertNotNull(testprop);
        assertNotNull(slienv);
        System.out.println("Environment(\'" + slienv + "\' ==> testprop = " + testprop);
        assertTrue(testprop.equalsIgnoreCase(slienv + " " + PROP_VALUE));
    }
}

