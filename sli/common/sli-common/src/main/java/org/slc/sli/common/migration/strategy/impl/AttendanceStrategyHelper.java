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
package org.slc.sli.common.migration.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to help convert attendance from a mongo to API format.
 * @author ablum
 *
 */
public class AttendanceStrategyHelper {
    private static final String SCHOOL_YEAR_ATTENDANCE = "schoolYearAttendance";
    private static final String SCHOOL_YEAR = "schoolYear";
    private static final String ATTENDANCE_EVENT = "attendanceEvent";

    /**
     * Wrap attendanceEvent and schoolYear fields into one new field: schoolYearAttendance.
     * @param entityBody entityBody
     * @return A new wrapped body
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> wrap(Map<String, Object> entityBody) {
        Map<String, Object> body = new HashMap<String, Object>(entityBody);
        final List<Map<String, Object>> attendanceEvents = (List<Map<String, Object>>) body.get(ATTENDANCE_EVENT);
        final String schoolYear = (String) body.get(SCHOOL_YEAR);

        List<Map<String, Object>> schoolYearAttendances = new ArrayList<Map<String, Object>>();
        Map<String, Object> schoolYearAttendance = new HashMap<String, Object>();
        schoolYearAttendance.put(SCHOOL_YEAR, schoolYear);
        if (attendanceEvents != null) {
            schoolYearAttendance.put(ATTENDANCE_EVENT, attendanceEvents);
        }
        schoolYearAttendances.add(schoolYearAttendance);

        body.put(SCHOOL_YEAR_ATTENDANCE, schoolYearAttendances);
        body.remove(SCHOOL_YEAR);
        body.remove(ATTENDANCE_EVENT);

        return body;
    }
}
