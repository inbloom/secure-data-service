/**
 * 
 */
package org.slc.sli.modeling.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for Option class.
 * 
 * @author wscott
 *
 */
public class OptionTest {
    
    private Option option; //class under test
    
    private static final String VALUE = "option-test";
    private static final ArrayList<Documentation> DOC = new ArrayList<Documentation>(0);

    @Before
    public void setup() {
        option = new Option(VALUE, DOC);
    }
    
    @Test (expected = NullPointerException.class)
    public void testNullValue() {
        new Option(null, DOC);
    }
    
    @Test (expected = NullPointerException.class)
    public void testNullDocumentation() {
        new Option(VALUE, null);        
    }

    @Test
    public void testGetValue() {
        assertEquals(VALUE, option.getValue());
    }
    
    @Test
    public void testGetDocumentation() {
        assertEquals(DOC, option.getDocumentation());
    }

}
