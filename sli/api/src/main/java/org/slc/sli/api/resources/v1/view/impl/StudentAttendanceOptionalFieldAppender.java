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

        //get the attendances per session
        Map<String, List<EntityBody>> attendancePerSession = getAttendances(studentIds, sessions);

        //add attendances to student's entityBody
        for (EntityBody student : entities) {
            String id = (String) student.get("id");
            //get the student section associations
            List<EntityBody> studentSectionAssociations = (List<EntityBody>) student.get("studentSectionAssociation");

            if (studentSectionAssociations == null) continue;

            Set<EntityBody> attendancesForStudent = new HashSet<EntityBody>();
            for (EntityBody studentSectionAssociation : studentSectionAssociations) {
                //get the sectionId
                String sectionId = (String) studentSectionAssociation.get(ParameterConstants.SECTION_ID);
                //get the section
                EntityBody section = optionalFieldAppenderHelper.getEntityFromList(sections, "id", sectionId);

                if (section != null) {
                    //get the attendances for this session
                    List<EntityBody> attendancesForSession = attendancePerSession.get((String) section.get(ParameterConstants.SESSION_ID));

                    if (attendancesForSession != null && !attendancesForSession.isEmpty()) {
                        List<EntityBody> attendancesForStudentForSession = optionalFieldAppenderHelper.getEntitySubList(attendancesForSession,
                                ParameterConstants.STUDENT_ID, id);

                        if (attendancesForStudentForSession != null && !attendancesForStudentForSession.isEmpty()) {
                            attendancesForStudent.addAll(attendancesForStudentForSession);
                        }
                    }
                }
            }

            //add the attendances to the student body
            if (!attendancesForStudent.isEmpty()) {
                EntityBody attendancesBody = new EntityBody();
                attendancesBody.put(ResourceNames.ATTENDANCES, new ArrayList<EntityBody>(attendancesForStudent));
                student.put(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES, attendancesBody);
            }
        }

        return entities;
    }

    /**
     * Returns a map of student attendances per session
     * @param studentIds List of studentIds
     * @param sessions List of sessions
     * @return
     */
    protected Map<String, List<EntityBody>> getAttendances(List<String> studentIds, List<EntityBody> sessions) {
        Map<String, List<EntityBody>> attendancePerSession = new HashMap<String, List<EntityBody>>();

        //init the end date
        Date endDate = new Date(System.currentTimeMillis());
        for (EntityBody session : sessions) {
            try {
                //get the begin date
                Date startDate = formatter.parse((String) session.get("beginDate"));

                //setup the query
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.addCriteria(new NeutralCriteria("eventDate", ">=", formatter.format(startDate)));
                neutralQuery.addCriteria(new NeutralCriteria("eventDate", "<=", formatter.format(endDate)));
                neutralQuery.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.CRITERIA_IN, studentIds));

                //get the attendances
                List<EntityBody> attendances = optionalFieldAppenderHelper.queryEntities(ResourceNames.ATTENDANCES, neutralQuery);

                if (attendances != null && !attendances.isEmpty()) {
                    attendancePerSession.put((String) session.get("id"), attendances);
                }

            } catch (ParseException e) {
                warn("Could not parse date {}", new Object[]{session.get("beginDate")});
            }
        }
        return attendancePerSession;
    }


}
