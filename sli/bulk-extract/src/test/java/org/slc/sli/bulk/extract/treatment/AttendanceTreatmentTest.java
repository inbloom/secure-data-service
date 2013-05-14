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
package org.slc.sli.bulk.extract.treatment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.slc.sli.bulk.extract.BulkExtractEntity;
import org.slc.sli.domain.Entity;

/**
 * Treat attendance entity.
 * @author ablum
 *
 */
public class AttendanceTreatmentTest {
    private AttendanceTreatment treat = new AttendanceTreatment();

    /**
     * Test the apply method.
     */
    @Test
    public void testApply() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolYear", "schoolYear");
        List<Map<String,Object>> attendanceEvent = new ArrayList<Map<String,Object>>();
        body.put("attendanceEvent", attendanceEvent);

        Entity entity = new BulkExtractEntity(body, "student");
        Entity treated = treat.apply(entity);

        Assert.assertNotNull(treated.getBody().get("schoolYearAttendance"));
        List<Map<String, Object>> attendances = new ArrayList<Map<String, Object>>();
        Map<String, Object> schoolYearAttendance = new HashMap<String, Object>();
        attendances = (List<Map<String, Object>>) treated.getBody().get("schoolYearAttendance");
        schoolYearAttendance = attendances.get(0);
        Assert.assertNotNull(schoolYearAttendance.get("schoolYear"));
        Assert.assertNotNull(schoolYearAttendance.get("attendanceEvent"));

        Assert.assertEquals("schoolYear", schoolYearAttendance.get("schoolYear"));
        Assert.assertNotNull(schoolYearAttendance.get("attendanceEvent"));

    }
    
    @Test
    public void testDuplicateApplication() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolYearAttendance", new Object());

        Entity entity = new BulkExtractEntity(body, "student");
        Entity treated = treat.apply(entity);
        Assert.assertEquals(entity, treated);
    }
}
