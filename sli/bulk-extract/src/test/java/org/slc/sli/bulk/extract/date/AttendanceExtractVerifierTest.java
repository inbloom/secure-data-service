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
package org.slc.sli.bulk.extract.date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * @author tshewchuk
 */
public class AttendanceExtractVerifierTest {

    private AttendanceExtractVerifier attendanceExtractVerifier = new AttendanceExtractVerifier();

    @Test
    public void testshouldExtractEntityBeforeAndAfterTreatment() {
        Map<String, Object> entityBody = new HashMap<String, Object>();
        entityBody.put(ParameterConstants.SCHOOL_YEAR, "2009-2010");
        Entity attendance = Mockito.mock(Entity.class);
        Mockito.when(attendance.getType()).thenReturn(EntityNames.ATTENDANCE);
        Mockito.when(attendance.getBody()).thenReturn(entityBody);

        // First call contains untreated attendance.
        Assert.assertTrue(attendanceExtractVerifier.shouldExtract(attendance, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));

        // Mimic attendance treatment transformation for subsequent calls.
        entityBody.clear();
        List<Map<String, Object>> schoolYearAttendances = new ArrayList<Map<String, Object>>();
        Map<String, Object> schoolYearAttendance = new HashMap<String, Object>();
        schoolYearAttendance.put(ParameterConstants.SCHOOL_YEAR, "2009-2010");
        schoolYearAttendances.add(schoolYearAttendance);
        entityBody.put("schoolYearAttendance", schoolYearAttendances);

        Assert.assertTrue(attendanceExtractVerifier.shouldExtract(attendance, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(attendanceExtractVerifier.shouldExtract(attendance, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(attendanceExtractVerifier.shouldExtract(attendance, DateTime.parse("2009-05-24", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(attendanceExtractVerifier.shouldExtract(attendance, DateTime.parse("2008-05-24", DateHelper.getDateTimeFormat())));
    }

}
