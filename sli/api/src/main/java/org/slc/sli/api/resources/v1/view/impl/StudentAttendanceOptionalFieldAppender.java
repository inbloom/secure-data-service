package org.slc.sli.api.resources.v1.view.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;

/**
 * Provides data about students and attendance to construct the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class StudentAttendanceOptionalFieldAppender implements OptionalFieldAppender {

    private final Logger logger = LoggerFactory.getLogger(StudentAttendanceOptionalFieldAppender.class);

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    public StudentAttendanceOptionalFieldAppender() {
    }

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities) {
        List<String> studentIds = new ArrayList<String>();
        Map<String, List<EntityBody>> studentAttendances = new HashMap<String, List<EntityBody>>();

        //construct list of student IDs
        for (EntityBody entity : entities) {
            String id = (String) entity.get("id");
            studentIds.add(id);
            studentAttendances.put(id, new ArrayList<EntityBody>());
        }

        //get the attendances
        List<EntityBody> attendanceEntities = optionalFieldAppenderHelper.queryEntities(ResourceNames.ATTENDANCES,
                ParameterConstants.STUDENT_ID, studentIds);

        //sort out attendances by studentId
        for (EntityBody attendanceEntity : attendanceEntities) {
            String id = (String) attendanceEntity.get("studentId");

            if (studentAttendances.containsKey(id)) {
                studentAttendances.get(id).add(attendanceEntity);
            } else {
                logger.warn("Attendances were returned for studentIDs that were not in query.");
            }
        }

        //add attendances to student's entityBody
        for (EntityBody entity : entities) {
            String id = (String) entity.get("id");
            EntityBody attendancesBody = new EntityBody();

            attendancesBody.put(ResourceNames.ATTENDANCES, studentAttendances.get(id));
            entity.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
        }

        return entities;
    }

}
