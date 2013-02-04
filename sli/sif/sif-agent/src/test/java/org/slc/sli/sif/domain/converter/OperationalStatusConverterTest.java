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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.student.OperationalStatus;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * OperationalStatusConverter unit tests
 */
public class OperationalStatusConverterTest extends AdkTest {

    private final OperationalStatusConverter converter = new OperationalStatusConverter();
    private final Map<OperationalStatus, String> map = new HashMap<OperationalStatus, String>();

    @Test
    public void testNull() {
        String result = converter.convert(null);
        Assert.assertNull("Operational status should be null", result);
    }

    @Test
    public void testEmpty() {
        OperationalStatus status = OperationalStatus.wrap("");
        String result = converter.convert(status);
        Assert.assertNull("Operational status should be null", result);
    }

    @Test
    public void testMappings() {
        map.clear();
        map.put(OperationalStatus.AGENCY_CHANGED, "Changed Agency");
        map.put(OperationalStatus.AGENCY_CLOSED, "Closed");
        map.put(OperationalStatus.AGENCY_FUTURE, "Future");
        map.put(OperationalStatus.AGENCY_INACTIVE, "Inactive");
        map.put(OperationalStatus.AGENCY_NEW, "New");
        map.put(OperationalStatus.AGENCY_OPEN, "Active");
        map.put(OperationalStatus.CHANGED_BOUNDARY, null);
        map.put(OperationalStatus.SCHOOL_CLOSED, "Closed");
        map.put(OperationalStatus.SCHOOL_FUTURE, "Future");
        map.put(OperationalStatus.SCHOOL_INACTIVE, "Inactive");
        map.put(OperationalStatus.SCHOOL_NEW, "New");
        map.put(OperationalStatus.SCHOOL_OPEN, "Active");

        for (OperationalStatus status : map.keySet()) {
            String expected = map.get(status);
            String result = converter.convert(status);
            Assert.assertEquals(expected, result);
        }

        String result = converter.convert(OperationalStatus.wrap("something else"));
        Assert.assertNull(result);
    }
}
