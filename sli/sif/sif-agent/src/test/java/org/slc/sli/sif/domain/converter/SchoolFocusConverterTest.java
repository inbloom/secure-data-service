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
import openadk.library.student.SchoolFocus;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolFocusType;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * SchoolFocusConverter unit tests
 */
public class SchoolFocusConverterTest extends AdkTest {

    private final SchoolFocusConverter converter = new SchoolFocusConverter();

    @Test
    public void testNullList() {
        String result = converter.convert(null);
        Assert.assertNull("school type should be null", result);
    }

    @Test
    public void testEmptyList() {
        SchoolFocusList list = new SchoolFocusList();
        String result = converter.convert(list);
        Assert.assertNull("school type should be null", result);
    }

    @Test
    public void testEmptySchoolFocus() {
        SchoolFocusList list = new SchoolFocusList(new SchoolFocus());
        String result = converter.convert(list);
        Assert.assertEquals("Not Supported", result);
    }

    @Test
    public void testAlternative() {
        testType(SchoolFocusType.ALTERNATIVE, "Alternative");
    }

    @Test
    public void testCharter() {
        testType(SchoolFocusType.CHARTER, "JJAEP");
    }

    @Test
    public void testMagnet() {
        testType(SchoolFocusType.MAGNET, "DAEP");
    }

    @Test
    public void testRegular() {
        testType(SchoolFocusType.REGULAR, "Regular");
    }

    @Test
    public void testSpecialEd() {
        testType(SchoolFocusType.SPECIALED, "Special Education");
    }

    @Test
    public void testVocational() {
        testType(SchoolFocusType.VOCATIONAL, "Vocational");
    }

    @Test
    public void testNotSupported() {
        testType(SchoolFocusType.wrap("not supported"), "Not Supported");
    }

    private void testType(SchoolFocusType type, String expected) {
        SchoolFocusList list = new SchoolFocusList(new SchoolFocus(type));
        String result = converter.convert(list);
        Assert.assertEquals(expected, result);
    }

}
