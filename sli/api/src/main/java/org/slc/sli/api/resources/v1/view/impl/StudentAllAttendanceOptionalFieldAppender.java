package org.slc.sli.api.resources.v1.view.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        for (EntityBody studentSchoolPair : attendances) {
            String studentId = (String) studentSchoolPair.get("studentId");
            attendancesPerStudent.put(studentId, new ArrayList<EntityBody>());
        }
        
        //iterate through attendances, adding to the appropriate student's list
        for (EntityBody attendance : attendances) {
            String id = (String) attendance.get("studentId");
            attendancesPerStudent.get(id).add(attendance);
        }

        //add attendances to appropriate student's entityBody
        for (EntityBody student : entities) {
            String id = (String) student.get("id");
            List<EntityBody> attendancesForStudent = attendancesPerStudent.get(id);

            //add the attendances to the student body
            if (!attendancesForStudent.isEmpty()) {
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
            if (!attendancesForStudent.isEmpty()) {
                EntityBody attendancesBody = new EntityBody();
                attendancesBody.put(ResourceNames.ATTENDANCES, attendancesForStudent);
                student.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
            }
        }

        return entities;
    }
    
    /**
     * Pulls attendance events out of the schoolYearAttendance object (list of maps containing school year
     * attendance) and returns a list of entity bodies representing the attendance events.
     * @param schoolYearAttendance stored in transformed attendance collection.
     * @return list of entity bodies representing transformed attendance events.
     */
    private List<EntityBody> getAttendanceEventEntityBodies(List<Map<String, Object>> schoolYearAttendance) {
        List<EntityBody> bodies = new ArrayList<EntityBody>();
        for (int i = 0; i < schoolYearAttendance.size(); i++) {
            Map<String, Object> attendance = schoolYearAttendance.get(i);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> events = (List<Map<String, Object>>) attendance.get("attendanceEvent");
            
            for (Map<String, Object> event : events) {
                bodies.add(new EntityBody(event));
            }
        }
        return bodies;
    }
}
