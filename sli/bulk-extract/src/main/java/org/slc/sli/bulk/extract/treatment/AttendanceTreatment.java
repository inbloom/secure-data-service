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

import org.slc.sli.domain.Entity;
/**
 * Treat attendance entity.
 * @author ablum
 *
 */
public class AttendanceTreatment implements Treatment {
    private static final String SCHOOL_YEAR = "schoolYear";
    private static final String ATTENDANCE_EVENT = "attendanceEvent";
    private static final String SCHOOL_YEAR_ATTENDANCE = "schoolYearAttendance";

    @Override
    public Entity apply(Entity entity) {
        List<Map<String,Object>> attendances = new ArrayList<Map<String,Object>>();
        Map<String, Object> schoolYearAttendance = new HashMap<String, Object>();
        if (entity.getBody().containsKey(SCHOOL_YEAR)) {
            schoolYearAttendance.put(SCHOOL_YEAR, entity.getBody().get(SCHOOL_YEAR));
            entity.getBody().remove(SCHOOL_YEAR);
        }

        if (entity.getBody().containsKey(ATTENDANCE_EVENT)) {
            schoolYearAttendance.put(ATTENDANCE_EVENT, entity.getBody().get(ATTENDANCE_EVENT));
            entity.getBody().remove(ATTENDANCE_EVENT);
        }
        attendances.add(schoolYearAttendance);
        entity.getBody().put(SCHOOL_YEAR_ATTENDANCE, attendances);
        return entity;
    }

}
