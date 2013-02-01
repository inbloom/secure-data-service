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
package org.slc.sli.sif.domain.converter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for DateConverter
 *
 */
public class DateConverterTest {

    DateConverter converter = new DateConverter();

    @Test
    public void shouldConvertWithCorrectFormat() {

        Calendar date = new GregorianCalendar(2004, Calendar.FEBRUARY, 29);
        String result = converter.convert(date);

        Assert.assertEquals("2004-02-29", result);
    }

    @Test
    public void shouldHandleNullDate() {
        String result = converter.convert(null);

        Assert.assertNull(result);
    }

}
