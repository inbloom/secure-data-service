package org.slc.sli.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ProfileAwareTest {
    
    private static final String PROP_VALUE = "DAL Context Test Property";
    
    @Autowired
    @Value("${sli.test.prop}")
    private String testprop = null;
    
    public void setTestprop(String v) {
        testprop = v;
    }
    
    public String getTestprop() {
        return testprop;
    }
    
    @Autowired
    @Value("${env}")
    private String env = null;
    
    public void setEnv(String e) {
        env = e;
    }
    
    public String getEnv() {
        return env;
    }
    
    @Test
    public void testProfileAwareness() {
        assertNotNull(testprop);
        assertNotNull(env);
        System.out.println("Environment(\'" + env + "\' ==> testprop = " + testprop);
        assertTrue(testprop.equalsIgnoreCase(env + " " + PROP_VALUE));
    }
}
