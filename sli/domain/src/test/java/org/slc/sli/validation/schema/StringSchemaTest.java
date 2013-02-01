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


package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.validation.schema.Annotation.AnnotationType;
import org.slc.sli.validation.strategy.AbstractBlacklistStrategy;

/**
 * JUnits for StringSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StringSchemaTest {

//  @Autowired
    StringSchema schema;

    @Before
    public void setup() {
        schema = new StringSchema();
    }

    @Test
    public void testStringValidation() throws IllegalArgumentException {
        String stringEntity = "test";
        assertTrue("String entity validation failed", schema.validate(stringEntity));
    }

    @Test
    public void testValidationOfBooleanFailure() {
        Boolean booleanEntity = true;
        assertFalse("Expected StringSchema boolean validation failure did not succeed", schema.validate(booleanEntity));
    }

    @Test
    public void testValidationOfIntegerFailure() {
        Integer integerEntity = 0;
        assertFalse("Expected StringSchema integer validation failure did not succeed", schema.validate(integerEntity));
    }

    @Test
    public void testValidationOfFloatFailure() {
        Float floatEntity = new Float(0);
        assertFalse("Expected StringSchema float validation failure did not succeed", schema.validate(floatEntity));
    }

    @Test
    public void testRestrictions() {
        schema.getProperties().put(Restriction.MIN_LENGTH.getValue(), "2");
        schema.getProperties().put(Restriction.MAX_LENGTH.getValue(), "4");

        assertTrue(schema.validate("12"));
        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("1"));
        assertFalse(schema.validate("12345"));

        schema.getProperties().put(Restriction.LENGTH.getValue(), "4");
        assertTrue(schema.validate("1234"));
        assertFalse(schema.validate("123"));
        assertFalse(schema.validate("12345"));
    }

    @Test
    public void testStrictBlacklist() {
        AppInfo info = Mockito.mock(AppInfo.class);
        Mockito.when(info.isRelaxedBlacklisted()).thenReturn(false);
        Mockito.when(info.getType()).thenReturn(AnnotationType.APPINFO);

        // strict strategy allows input1, fails on input 2
        // relaxed strategy allows input2, fails on input 1
        AbstractBlacklistStrategy mockStrictStrategy = Mockito.mock(AbstractBlacklistStrategy.class);
        Mockito.when(mockStrictStrategy.isValid(Mockito.anyString(), Mockito.eq("input1"))).thenReturn(true);
        Mockito.when(mockStrictStrategy.isValid(Mockito.anyString(), Mockito.eq("input2"))).thenReturn(false);

        AbstractBlacklistStrategy mockRelaxedStrategy = Mockito.mock(AbstractBlacklistStrategy.class);
        Mockito.when(mockRelaxedStrategy.isValid(Mockito.anyString(), Mockito.eq("input2"))).thenReturn(true);
        Mockito.when(mockRelaxedStrategy.isValid(Mockito.anyString(), Mockito.eq("input1"))).thenReturn(false);

        List<AbstractBlacklistStrategy> strictList = new ArrayList<AbstractBlacklistStrategy>();
        strictList.add(mockStrictStrategy);

        List<AbstractBlacklistStrategy> relaxedList = new ArrayList<AbstractBlacklistStrategy>();
        relaxedList.add(mockRelaxedStrategy);

        StringSchema testSchema = new StringSchema("string", strictList, relaxedList);
        testSchema.addAnnotation(info);

        assertFalse("Error in setup, schema should not be relax-blacklisted", testSchema.isRelaxedBlacklisted());

        boolean isValid = testSchema.validate("input1");
        assertTrue("Based on strict strategy, input1 should have passed validation", isValid);

        isValid = testSchema.validate("input2");
        assertFalse("Based on strict strategy, input2 should have failed validation", isValid);

        // the relaxed strategy should be used, the strict strategy should not be used
        Mockito.verify(mockRelaxedStrategy, Mockito.times(0)).isValid(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockStrictStrategy, Mockito.times(2)).isValid(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testRelaxedBlacklist() {
        AppInfo info = Mockito.mock(AppInfo.class);
        Mockito.when(info.isRelaxedBlacklisted()).thenReturn(true);
        Mockito.when(info.getType()).thenReturn(AnnotationType.APPINFO);

        // strict strategy allows input1, fails on input 2
        // relaxed strategy allows input2, fails on input 1
        AbstractBlacklistStrategy mockStrictStrategy = Mockito.mock(AbstractBlacklistStrategy.class);
        Mockito.when(mockStrictStrategy.isValid(Mockito.anyString(), Mockito.eq("input1"))).thenReturn(true);
        Mockito.when(mockStrictStrategy.isValid(Mockito.anyString(), Mockito.eq("input2"))).thenReturn(false);

        AbstractBlacklistStrategy mockRelaxedStrategy = Mockito.mock(AbstractBlacklistStrategy.class);
        Mockito.when(mockRelaxedStrategy.isValid(Mockito.anyString(), Mockito.eq("input2"))).thenReturn(true);
        Mockito.when(mockRelaxedStrategy.isValid(Mockito.anyString(), Mockito.eq("input1"))).thenReturn(false);

        List<AbstractBlacklistStrategy> strictList = new ArrayList<AbstractBlacklistStrategy>();
        strictList.add(mockStrictStrategy);

        List<AbstractBlacklistStrategy> relaxedList = new ArrayList<AbstractBlacklistStrategy>();
        relaxedList.add(mockRelaxedStrategy);

        StringSchema testSchema = new StringSchema("string", strictList, relaxedList);
        testSchema.addAnnotation(info);

        assertTrue("Error in setup, schema should be relax-blacklisted", testSchema.isRelaxedBlacklisted());

        boolean isValid = testSchema.validate("input1");
        assertFalse("Based on relaxed strategy, input1 should have failed validation", isValid);

        isValid = testSchema.validate("input2");
        assertTrue("Based on relaxed strategy, input2 should have passed validation", isValid);

        // the relaxed strategy should be used, the strict strategy should not be used
        Mockito.verify(mockRelaxedStrategy, Mockito.times(2)).isValid(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mockStrictStrategy, Mockito.times(0)).isValid(Mockito.anyString(), Mockito.anyString());
    }
}
