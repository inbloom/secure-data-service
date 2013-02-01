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

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for TimeSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TimeSchemaTest {

    @Autowired
    TimeSchema schema;

    @Test
    public void testTimeStringValidation() throws IllegalArgumentException {
        String timeString = "12:00:00-05:00";
        assertTrue("Time entity validation failed", schema.validate(timeString));
    }

    @Test
    public void testTimeValidation() {
        Calendar calendar = Calendar.getInstance();
        String timeString = javax.xml.bind.DatatypeConverter.printTime(calendar);
        assertTrue("Time entity validation failed", schema.validate(timeString));
    }

    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected TimeSchema string validation failure did not succeed", schema.validate(stringEntity));
    }

}
