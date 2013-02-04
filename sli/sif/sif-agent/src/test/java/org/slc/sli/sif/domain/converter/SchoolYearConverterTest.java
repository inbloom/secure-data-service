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

import junit.framework.Assert;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * SchoolYearConverter unit tests
 */
public class SchoolYearConverterTest extends AdkTest {

    private final SchoolYearConverter converter = new SchoolYearConverter();

    @Test
    public void testNullList() {
        String result = converter.convert(null);
        Assert.assertNull("school year should be null", result);
    }

    @Test
    public void test() {
        Assert.assertEquals(converter.convert(2011), "2010-2011");
        Assert.assertEquals(converter.convert(2013), "2012-2013");
    }

}
