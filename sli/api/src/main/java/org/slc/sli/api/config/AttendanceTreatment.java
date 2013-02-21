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

package org.slc.sli.api.config;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.domain.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert between the API and Mongo representations of attendance.
 * @author chung
 */
@Component
public class AttendanceTreatment implements Treatment {
    private static final String SCHOOL_YEAR_ATTENDANCE = "schoolYearAttendance";
    private static final String SCHOOL_YEAR = "schoolYear";
    private static final String ATTENDANCE_EVENT = "attendanceEvent";

    @Override
    @SuppressWarnings("unchecked")
    public EntityBody toStored(EntityBody exposed, EntityDefinition defn) {
        if (defn.getType().equals(EntityNames.ATTENDANCE)) {
            final List<EntityBody> schoolYearAttendances = (List<EntityBody>) exposed.get(SCHOOL_YEAR_ATTENDANCE);

            //TODO: find out if we accept multiple schoolYearAttendances
            EntityBody schoolYearAttendance = schoolYearAttendances.get(0);
            final String schoolYear = (String) schoolYearAttendance.get(SCHOOL_YEAR);
            final List<EntityBody> attendanceEvents = (List<EntityBody>) schoolYearAttendance.get(ATTENDANCE_EVENT);

            exposed.put(ATTENDANCE_EVENT, attendanceEvents);
            exposed.put(SCHOOL_YEAR, schoolYear);
            exposed.remove(SCHOOL_YEAR_ATTENDANCE);
        }

        return exposed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity) {
        if (defn.getType().equals(EntityNames.ATTENDANCE)) {
            final List<EntityBody> attendanceEvents = (List<EntityBody>) stored.get(ATTENDANCE_EVENT);
            final String schoolYear = (String) stored.get(SCHOOL_YEAR);

            List<EntityBody> schoolYearAttendances = new ArrayList<EntityBody>();
            EntityBody schoolYearAttendance = new EntityBody() {{
                put(SCHOOL_YEAR, schoolYear);
                put(ATTENDANCE_EVENT, attendanceEvents);
            }};
            schoolYearAttendances.add(schoolYearAttendance);

            stored.put(SCHOOL_YEAR_ATTENDANCE, schoolYearAttendances);
            stored.remove(SCHOOL_YEAR);
            stored.remove(ATTENDANCE_EVENT);
        }

        return stored;
    }
}
