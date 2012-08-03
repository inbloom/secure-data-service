/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import openadk.library.student.OperationalStatus;

import org.junit.Test;

import org.slc.sli.sif.ADKTest;

public class OperationalStatusConverterTest extends ADKTest {

    private final OperationalStatusConverter converter = new OperationalStatusConverter();

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
    public void testSchoolClosed(){
        testStatus(OperationalStatus.SCHOOL_CLOSED, "Closed");
    }

    @Test
    public void testSchoolFuture(){
        testStatus(OperationalStatus.SCHOOL_FUTURE, "Future");
    }

    @Test
    public void testSchoolInactive(){
        testStatus(OperationalStatus.SCHOOL_INACTIVE, "Inactive");
    }

    @Test
    public void testSchoolNew(){
        testStatus(OperationalStatus.SCHOOL_NEW, "New");
    }

    @Test
    public void testSchoolOpen(){
        testStatus(OperationalStatus.SCHOOL_OPEN, "Reopened");
    }

    @Test
    public void testChangedBoundary(){
        testStatus(OperationalStatus.CHANGED_BOUNDARY, "Changed Agency");
    }

    @Test
    public void testNotSupported(){
        testStatus(OperationalStatus.AGENCY_CHANGED, "Not Supported");
        testStatus(OperationalStatus.AGENCY_CLOSED, "Not Supported");
        testStatus(OperationalStatus.AGENCY_FUTURE, "Not Supported");
        testStatus(OperationalStatus.AGENCY_INACTIVE, "Not Supported");
        testStatus(OperationalStatus.AGENCY_NEW, "Not Supported");
        testStatus(OperationalStatus.AGENCY_OPEN, "Not Supported");

        testStatus(OperationalStatus.wrap("something else"), "Not Supported");
    }

    private void testStatus(OperationalStatus status, String expected){
        String result = converter.convert(status);
        Assert.assertEquals(expected, result);
    }

}
