/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.domain;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;



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
            assertEquals(neutralCriteria.canBePrefixed(), true);
        }
    }
    
    @Test
    public void testEqualsComparison() {
        NeutralCriteria neutralCriteria1 = new NeutralCriteria("key", "=", "value");
        NeutralCriteria neutralCriteria2 = new NeutralCriteria("key", "=", "value");
        NeutralCriteria neutralCriteria3 = new NeutralCriteria("differentKey", "=", "value");
        NeutralCriteria neutralCriteria4 = new NeutralCriteria("key", "!=", "value");
        NeutralCriteria neutralCriteria5 = new NeutralCriteria("key", "!=", "differentValue");
        NeutralCriteria neutralCriteria6 = new NeutralCriteria("key", "=", "value", true);
        NeutralCriteria neutralCriteria7 = new NeutralCriteria("key", "=", "value", false);

        assertTrue(neutralCriteria1.equals(neutralCriteria2));
        assertFalse(neutralCriteria1 == neutralCriteria2);
        assertFalse(neutralCriteria1.equals(neutralCriteria3));
        assertFalse(neutralCriteria1.equals(neutralCriteria4));
        assertFalse(neutralCriteria1.equals(neutralCriteria5));
        assertTrue(neutralCriteria1.equals(neutralCriteria6));
        assertFalse(neutralCriteria1.equals(neutralCriteria7));

    }
    
    @Test
    public void testGettersAndSetters() {
        NeutralCriteria neutralCriteria = new NeutralCriteria("key", "=", "value");
        String newKey = "newKey";
        String newOperator = "!=";
        Object newValue = "newValue";
        
        neutralCriteria.setKey(newKey);
        assertTrue(neutralCriteria.getKey().equals(newKey));
        neutralCriteria.setOperator(newOperator);
        assertTrue(neutralCriteria.getOperator().equals(newOperator));
        neutralCriteria.setValue(newValue);
        assertTrue(neutralCriteria.getValue().equals(newValue));
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
