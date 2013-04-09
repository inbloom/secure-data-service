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
package org.slc.sli.api.migration.strategy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.common.migration.strategy.impl.AttendanceStrategyHelper;

/**
 * Represents a strategy that converts between different attendance representations
 * for Mongo and API
 */
@Scope("prototype")
@Component
public class AttendanceStrategy implements MigrationStrategy<EntityBody> {

    public static final String OPERATION = "operation";

    private static final String POST = "post";
    private static final String PUT = "put";
    private static final String GET = "get";
    private static final String SCHOOL_YEAR_ATTENDANCE = "schoolYearAttendance";
    private static final String SCHOOL_YEAR = "schoolYear";
    private static final String ATTENDANCE_EVENT = "attendanceEvent";

    private String operation;

    @Override
    public EntityBody migrate(EntityBody entity) throws MigrationException {
        // This strategy should always expect a list
        throw new MigrationException(new IllegalAccessException("This method is not yet implemented"));
    }

    @Override
    public void setParameters(Map<String, Object> parameters) throws MigrationException {
        this.operation = parameters.get(OPERATION).toString();
    }

    @Override
    public List<EntityBody> migrate(List<EntityBody> entityList) throws MigrationException {
        List<EntityBody> returnList = new ArrayList<EntityBody>();
        for (EntityBody entityBody : entityList) {
            if (operation.equals(POST) || operation.equals(PUT)) {
                returnList.addAll(unwrap(entityBody));
            } else if (operation.equals(GET)) {
                returnList.addAll(wrap(entityBody));
            } else {
                throw new MigrationException(new IllegalAccessException("Operation not supported."));
            }
        }
        return returnList;
    }

    @SuppressWarnings("unchecked")
    private List<EntityBody> unwrap(EntityBody entityBody) {
        List<EntityBody> splitBodies = new ArrayList<EntityBody>();
        final List<Map<String, Object>> schoolYearAttendances =
                (List<Map<String, Object>>) entityBody.get(SCHOOL_YEAR_ATTENDANCE);
        if (schoolYearAttendances != null) {
            Map<String, List<Map<String, Object>>> attendanceEventsBySchoolYear =
                    groupBySchoolYears(schoolYearAttendances);
            for (String schoolYear : attendanceEventsBySchoolYear.keySet()) {
                EntityBody copy = new EntityBody(entityBody);
                final List<Map<String, Object>> attendanceEvents = attendanceEventsBySchoolYear.get(schoolYear);
                if (attendanceEvents != null) {
                    copy.put(ATTENDANCE_EVENT, attendanceEvents);
                }
                copy.put(SCHOOL_YEAR, schoolYear);
                copy.remove(SCHOOL_YEAR_ATTENDANCE);
                splitBodies.add(copy);
            }
        } else {
            splitBodies.add(entityBody);
        }

        if (splitBodies.size() > 1 && operation.equals(PUT)) {
            throw new IllegalStateException("Error occurred while processing entity body. Multiple school years?");
        }

        return splitBodies;
    }

    @SuppressWarnings("unchecked")
    private List<EntityBody> wrap(EntityBody entityBody) {
        EntityBody body = new EntityBody(AttendanceStrategyHelper.wrap(entityBody));

        List<EntityBody> entityBodies = new ArrayList<EntityBody>();
        entityBodies.add(body);
        return entityBodies;
    }

    // If some of the schoolYearAttendances are in the same schoolYear,
    // group them
    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, Object>>> groupBySchoolYears(List<Map<String, Object>> schoolYearAttendances) {
        Map<String, List<Map<String, Object>>> attendanceEventsBySchoolYear =
                new HashMap<String, List<Map<String, Object>>>();
        for (Map<String, Object> schoolYearAttendance : schoolYearAttendances) {
            final String schoolYear = (String) schoolYearAttendance.get(SCHOOL_YEAR);
            final List<Map<String, Object>> attendanceEvents =
                    (List<Map<String, Object>>) schoolYearAttendance.get(ATTENDANCE_EVENT);
            if (attendanceEventsBySchoolYear.containsKey(schoolYear)) {
                if (attendanceEvents != null) {
                    attendanceEventsBySchoolYear.get(schoolYear).addAll(attendanceEvents);
                }
            } else {
                attendanceEventsBySchoolYear.put(schoolYear, attendanceEvents);
            }
        }

        return attendanceEventsBySchoolYear;
    }
}
