package org.slc.sli.validation;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.slc.sli.domain.NeutralCriteria;


/**
 * Various utility functions for test
 * 
 * @author kmyers
 *
 */
public class NeutralCriteriaTest {

    @Test
    public void testValidParsingAbilities() {
        String key = "key";
        String value = "value";
        
        for (String operator : NeutralCriteria.SUPPORTED_COMPARISON_OPERATORS) {
            NeutralCriteria neutralCriteria = new NeutralCriteria(key + operator + value);
            assertEquals(neutralCriteria.getKey(), key);
            assertEquals(neutralCriteria.getOperator(), operator);
            assertEquals(neutralCriteria.getValue(), value);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyString() {
        new NeutralCriteria("");
    }

    @Test(expected = RuntimeException.class)
    public void testNoKey() {
        new NeutralCriteria("=5");
    }

    @Test(expected = RuntimeException.class)
    public void testNoOperator() {
        new NeutralCriteria("key5");
    }

    @Test(expected = RuntimeException.class)
    public void testNoValue() {
        new NeutralCriteria("key=");
    }
    
}
