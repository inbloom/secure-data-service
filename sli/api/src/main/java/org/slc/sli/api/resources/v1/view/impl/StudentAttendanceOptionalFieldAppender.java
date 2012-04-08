package org.slc.sli.api.resources.v1.view.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    @Autowired
    private MongoTemplate template;

    public StudentAttendanceOptionalFieldAppender() {
    }

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities) {

        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");

        //get the section Ids
        Set<String> sectionIds = optionalFieldAppenderHelper.getSectionIds(entities);
        //get the sections
        List<EntityBody> sections = optionalFieldAppenderHelper.queryEntities(ResourceNames.SECTIONS,
                "_id", new ArrayList<String>(sectionIds));
        //get the session Ids
        List<String> sessionIds = optionalFieldAppenderHelper.getIdList(sections, ParameterConstants.SESSION_ID);
        //get the sessions
        List<EntityBody> sessions = optionalFieldAppenderHelper.queryEntities(ResourceNames.SESSIONS,
                "_id", sessionIds);

        //init the start and end date
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis());
        for (EntityBody session : sessions) {
            try {
                //get the begin date
                Date date = formatter.parse((String) session.get("beginDate"));

                if (date.before(startDate)) {
                    startDate = date;
                }

            } catch (ParseException e) {
                warn("Could not parse date {}", new Object[] {session.get("beginDate")});
            }
        }

        //setup the query
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("eventDate", ">=", formatter.format(startDate)));
        neutralQuery.addCriteria(new NeutralCriteria("eventDate", "<=", formatter.format(endDate)));
        neutralQuery.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.CRITERIA_IN, studentIds));

        //get the attendances
        List<EntityBody> attendances = optionalFieldAppenderHelper.queryEntities(ResourceNames.ATTENDANCES, neutralQuery);

        //add attendances to student's entityBody
        for (EntityBody student : entities) {
            String id = (String) student.get("id");

            List<EntityBody> attendancesForStudent = optionalFieldAppenderHelper.getEntitySubList(attendances, ParameterConstants.STUDENT_ID, id);

            if (attendancesForStudent != null && !attendancesForStudent.isEmpty()) {
                EntityBody attendancesBody = new EntityBody();
                attendancesBody.put(ResourceNames.ATTENDANCES, attendancesForStudent);
                student.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
            }
        }

        return entities;
    }

}
