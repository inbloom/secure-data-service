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


package org.slc.sli.api.resources.v1.view.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Provides data about students and attendance to construct the custom
 * views returned by the api
 * @author wscott
 *
 */
@Component
public class StudentAllAttendanceOptionalFieldAppender implements OptionalFieldAppender {

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    @SuppressWarnings("unchecked")
    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {
        // get the student Ids
        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");

        // setup the query
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setLimit(0);
        neutralQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, studentIds));

        // get the attendances
        List<EntityBody> attendances = optionalFieldAppenderHelper.queryEntities(ResourceNames.ATTENDANCES, neutralQuery);

        // build a map of studentId -> list of attendances
        Map<String, List<EntityBody>> attendancesPerStudent = new HashMap<String, List<EntityBody>>();
        for (EntityBody attendance : attendances) {
            String studentId = (String) attendance.get("studentId");
            List<EntityBody> events = new ArrayList<EntityBody>();

            if (attendance.containsKey("attendanceEvent")) {
                List<Map<String, Object>> yearEvents = (List<Map<String, Object>>) attendance.get("attendanceEvent");
                if (yearEvents != null) {
                    for (int j = 0; j < yearEvents.size(); j++) {
                        events.add(new EntityBody(yearEvents.get(j)));
                    }
                }
            }

            if (attendancesPerStudent.containsKey(studentId)) {
                attendancesPerStudent.get(studentId).addAll(events);
            } else {
                attendancesPerStudent.put(studentId, events);
            }

        }

        // add attendances to appropriate student's entityBody
        for (EntityBody student : entities) {
            String id = (String) student.get("id");
            List<EntityBody> attendancesForStudent = attendancesPerStudent.get(id);

            // add the attendances to the student body
            if (attendancesForStudent != null && !attendancesForStudent.isEmpty()) {
                EntityBody attendancesBody = new EntityBody();
                attendancesBody.put(ResourceNames.ATTENDANCES, attendancesForStudent);
                student.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
            }
        }

        return entities;
    }
}
