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

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for DurationSchema
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DurationSchemaTests {

    @Autowired
    DurationSchema schema;

    @Test
    public void testDurationStringValidation() throws IllegalArgumentException {
        String dateTimeString = "P1Y2M3DT10H30M";
        assertTrue("Duration entity validation failed", schema.validate(dateTimeString));
    }

    @Test
    public void testDurationValidation() throws DatatypeConfigurationException {
        String dateTimeString = javax.xml.datatype.DatatypeFactory.newInstance().newDuration(2628000000L).toString();
        assertTrue("DateTime entity validation failed", schema.validate(dateTimeString));
    }

    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected DateTimeSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
}
