package org.slc.sli.api.resources.v1.view.impl;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public StudentAllAttendanceOptionalFieldAppender() {
    }

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
            
            if (attendance.containsKey("schoolYearAttendance")) {
                List<Map<String, Object>> schoolYearAttendances = (List<Map<String, Object>>) attendance.get("schoolYearAttendance");
                for (int i = 0; i < schoolYearAttendances.size(); i++) {
                    Map<String, Object> year = schoolYearAttendances.get(i);
                    List<Map<String, Object>> yearEvents = (List<Map<String, Object>>) year.get("attendanceEvent");
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

        //add attendances to appropriate student's entityBody
        for (EntityBody student : entities) {
            String id = (String) student.get("id");
            List<EntityBody> attendancesForStudent = attendancesPerStudent.get(id);

            //add the attendances to the student body
            if (attendancesForStudent != null && !attendancesForStudent.isEmpty()) {
                EntityBody attendancesBody = new EntityBody();
                attendancesBody.put(ResourceNames.ATTENDANCES, attendancesForStudent);
                student.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
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
