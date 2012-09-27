package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class XmiFeatureTest {

	private final static String NAME = "NAME";
    private final static boolean EXISTS = true;
    private final static String CLASS_NAME = "CLASS_NAME";
    private final static boolean CLASS_EXISTS = true;
    
    private final static XmiFeature XMI_FEATURE = new XmiFeature(NAME, EXISTS, CLASS_NAME, CLASS_EXISTS);

    @Test
    public void testGetName() {
    	assertEquals(NAME, XMI_FEATURE.getName());
    }

    @Test
    public void testExists() {
    	assertEquals(EXISTS, XMI_FEATURE.exists());
    }

    @Test
    public void testGetOwnerName() {
    	assertEquals(CLASS_NAME, XMI_FEATURE.getOwnerName());
    }

    @Test
    public void testOwnerExists() {
    	assertEquals(CLASS_EXISTS, XMI_FEATURE.ownerExists());
    }
    
    @Test
    public void testToString() {
    	
    	// just test not null response
    	assertNotNull(XMI_FEATURE.toString());
    	
    	// if response matters, re-enable this:
    	
    	/*
    	
    	String expectedResponse = "{name : NAME, exists : true, className : CLASS_NAME, classExists : true}";
    	String receivedResponse = XMI_FEATURE.toString();
    	
    	assertEquals(expectedResponse, receivedResponse);
    	
    	*/
    }

    @Test (expected = NullPointerException.class)
    public void testNullParam1() {
    	new XmiFeature(null, EXISTS, CLASS_NAME, CLASS_EXISTS);
    }

    @Test (expected = NullPointerException.class)
    public void testNullParam2() {
    	new XmiFeature(NAME, EXISTS, null, CLASS_EXISTS);
    }
    
}
