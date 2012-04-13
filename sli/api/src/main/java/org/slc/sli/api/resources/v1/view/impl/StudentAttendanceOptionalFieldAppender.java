package org.slc.sli.api.resources.v1.view.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Date;
import java.util.StringTokenizer;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
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

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    public StudentAttendanceOptionalFieldAppender() {
    }

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {
        //get the year suffix from the params
        int yearSuffix = getYearSuffix(parameters);
        //get the student Ids
        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");

        //get the section Ids
        Set<String> sectionIds = optionalFieldAppenderHelper.getSectionIds(entities);
        //get the sections
        List<EntityBody> sections = optionalFieldAppenderHelper.queryEntities(ResourceNames.SECTIONS,
                "_id", new ArrayList<String>(sectionIds));
        //get the selectedSession Ids
        List<String> sessionIds = optionalFieldAppenderHelper.getIdList(sections, ParameterConstants.SESSION_ID);
        //get the sessions
        List<EntityBody> sessions = optionalFieldAppenderHelper.queryEntities(ResourceNames.SESSIONS,
                "_id", sessionIds);

        //get the attendances per selectedSession
        Map<String, List<EntityBody>> attendancePerSession = getAttendances(studentIds, sessions, yearSuffix);

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
                    //get the attendances for this selectedSession
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
     * Returns a map of student attendances per selectedSession
     * @param studentIds List of studentIds
     * @param sessions List of sessions
     * @return
     */
    protected Map<String, List<EntityBody>> getAttendances(List<String> studentIds, List<EntityBody> sessions, int yearSuffix) {
        Map<String, List<EntityBody>> attendancePerSession = new HashMap<String, List<EntityBody>>();

        //init the end date
        //Date endDate = new Date(System.currentTimeMillis());
        for (EntityBody session : sessions) {
            //get the begin date
            Date startDate = null;
            try {
                startDate = getBeginDate(studentIds, session, yearSuffix);
            } catch (ParseException e) {
                warn("Could not parse session date");
            }

            if (startDate != null) {
                //setup the query
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.setLimit(0);
                neutralQuery.addCriteria(new NeutralCriteria("eventDate", ">=", formatter.format(startDate)));
                //neutralQuery.addCriteria(new NeutralCriteria("eventDate", "<=", formatter.format(endDate)));
                neutralQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, studentIds));

                //get the attendances
                List<EntityBody> attendances = optionalFieldAppenderHelper.queryEntities(ResourceNames.ATTENDANCES, neutralQuery);

                if (attendances != null && !attendances.isEmpty()) {
                    attendancePerSession.put((String) session.get("id"), attendances);
                }
            }
        }
        return attendancePerSession;
    }

    /**
     * Returns the earliest date out of the sessions with the same
     * school year as the given session
     * @param studentIds The selected session
     * @param selectedSession The selected section
     * @param yearSuffix The number of years to go back
     * @return
     */
    protected Date getBeginDate(List<String> studentIds, EntityBody selectedSession, int yearSuffix) throws ParseException {
        Date startDate = null;

        //if no year is defined default to the current session
        if (yearSuffix == 0) {
            return formatter.parse((String) selectedSession.get("beginDate"));
        }

        //get the school years
        List<String> schoolYears = getSchoolYears((String) selectedSession.get("schoolYear"), yearSuffix);

        //get all the student section associations
        List<EntityBody> allStudentSectionAssociations = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_SECTION_ASSOCIATIONS,
                ParameterConstants.STUDENT_ID, studentIds);

        //get all the sectionIds
        List<String> sectionIds = optionalFieldAppenderHelper.getIdList(allStudentSectionAssociations, ParameterConstants.SECTION_ID);
        //get all the sections
        List<EntityBody> allSections = optionalFieldAppenderHelper.queryEntities(ResourceNames.SECTIONS, "_id", sectionIds);
        //get all the sessionIds
        List<String> allSessionIds = optionalFieldAppenderHelper.getIdList(allSections, ParameterConstants.SESSION_ID);

        //create query to get the sessions with correct school years
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setLimit(0);
        neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, allSessionIds));
        neutralQuery.addCriteria(new NeutralCriteria("schoolYear", NeutralCriteria.CRITERIA_IN, schoolYears));
        //execute the query
        List<EntityBody> allSessions = optionalFieldAppenderHelper.queryEntities(ResourceNames.SESSIONS, neutralQuery);

        //go through and find the earliest date
        startDate = new Date(System.currentTimeMillis());
        for (EntityBody session : allSessions) {
            Date date = formatter.parse((String) session.get("beginDate"));

            if (date.before(startDate)) {
                startDate = date;
            }
        }

        return startDate;
    }

    /**
     * Returns a list of school years depending on the number of
     * years to go back
     * @param currentSchoolYear Current school year
     * @param years Years to go back
     * @return
     */
    protected List<String> getSchoolYears(String currentSchoolYear, int years) {
        List<String> schoolYears = new ArrayList<String>();

        if (currentSchoolYear == null) return schoolYears;
        if (currentSchoolYear.isEmpty()) return  schoolYears;

        schoolYears.add(currentSchoolYear);

        for (int i = 1; i < years; i++) {
            StringTokenizer st = new StringTokenizer(currentSchoolYear, "-");
            String prefix = null, suffix = null;

            int index = 0;
            String token = null;
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                switch (index) {
                    case 0: prefix = token; break;
                    case 1: suffix = token; break;
                }
                ++index;
            }

            if (prefix != null && suffix != null) {
                try {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(Integer.parseInt(prefix) - 1);
                    buffer.append("-");
                    buffer.append(Integer.parseInt(suffix) - 1);

                    currentSchoolYear = buffer.toString();
                    schoolYears.add(currentSchoolYear);
                } catch (NumberFormatException e) {
                    return schoolYears;
                }
            }
        }

        return schoolYears;
    }

    /**
     * Parses the params and converts it to year suffix
     * @param params
     * @return
     */
    protected int getYearSuffix(String params) {
        int yearSuffix = 0;

        if (params != null && !params.isEmpty()) {
            try {
                //parse the params
                yearSuffix = Integer.parseInt(params);
                yearSuffix = Math.abs(yearSuffix);

                //cap the years at 4
                yearSuffix = (yearSuffix > 4) ? 4 : yearSuffix;
            } catch (NumberFormatException e) {
                warn("Could not parse param to year {}", params);
            }
        }

        return yearSuffix;
    }



}
