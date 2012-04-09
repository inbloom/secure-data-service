package org.slc.sli.manager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.view.TimedLogic2;

/**
 * PopulationManager facilitates creation of logical aggregations of EdFi entities/associations such
 * as a
 * student summary comprised of student profile, enrollment, program, and assessment information in
 * order to
 * deliver the Population Summary interaction.
 * 
 * @author Robert Bloh rbloh@wgen.net
 * 
 */
public class PopulationManager implements Manager {
    
    private static Logger log = LoggerFactory.getLogger(PopulationManager.class);
    
    @Autowired
    private EntityManager entityManager;
    
    public PopulationManager() {
        
    }

    /**
     * Get assessments taken by a group of students
     * @param token Toekn used to authenticate
     * @param studentSummaries Student information
     * @return unique set of assessment entities
     */
    public List<GenericEntity> getAssessments(String token, List<GenericEntity> studentSummaries) {
        Set<GenericEntity> assessments = new TreeSet<GenericEntity>(new Comparator<GenericEntity>() {
            @Override
            public int compare(GenericEntity att1, GenericEntity att2) {
                return (att2.getString("id")).compareTo(att1.getString("id"));
            }
        });
        for (GenericEntity studentSummary : studentSummaries) {
            List<Map<String, Object>> studentAssessments = (List<Map<String, Object>>) studentSummary.get(Constants.ATTR_STUDENT_ASSESSMENTS);
            
            for (Map<String, Object> studentAssessment : studentAssessments) {
                try {
                    GenericEntity assessment = new GenericEntity((Map) studentAssessment.get("assessments"));
                    assessments.add(assessment);
                } catch (ClassCastException cce) {
                    log.warn(cce.getMessage());
                }
            }
        }
        return new ArrayList<GenericEntity>(assessments);
    }
    
    /**
     * Get the list of student summaries identified by the student id list and authorized for the
     * security token
     * 
     * @param token
     *            - the principle authentication token
     * @param studentIds
     *            - the student id list
     * @param sessionId
     *            - The id of the current session so you can get historical context.
     * @return studentList
     *         - the student summary entity list
     */
    public List<GenericEntity> getStudentSummaries(String token, List<String> studentIds, ViewConfig viewConfig,
            String sessionId, String sectionId) {
        
        long startTime = System.nanoTime();
        // Initialize student summaries

        //List<GenericEntity> studentSummaries = entityManager.getStudents(token, studentIds);
        List<GenericEntity> studentSummaries = entityManager.getStudents(token, sectionId, studentIds);
        log.warn("@@@@@@@@@@@@@@@@@@ Benchmark for student section view: {}", (System.nanoTime() - startTime) * 1.0e-9);
        
        // Get student programs
        List<GenericEntity> studentPrograms = entityManager.getPrograms(token, studentIds);
        Map<String, Object> studentProgramMap = new HashMap<String, Object>();
        for (GenericEntity studentProgram : studentPrograms) {
            List<String> programs = (List<String>) studentProgram.get(Constants.ATTR_PROGRAMS);
            studentProgramMap.put(studentProgram.getString(Constants.ATTR_STUDENT_ID), programs);
        }

        //Map<String, Object> studentAttendanceMap = createStudentAttendanceMap(token, studentIds, sessionId);
        
        // Add programs, attendance, and student assessment results to summaries
        for (GenericEntity studentSummary : studentSummaries) {
            if (studentSummary == null)
                continue;
            String id = studentSummary.getString(Constants.ATTR_ID);
            studentSummary.put(Constants.ATTR_PROGRAMS, studentProgramMap.get(id));
            //studentSummary.put(Constants.ATTR_STUDENT_ATTENDANCES, studentAttendanceMap.get(id));
            
            // clean out some unneeded gunk
            studentSummary.remove("links");
        }
        
        return studentSummaries;
    }
    
    
    /**
     * 
     * @return
     */
    @EntityMapping("listOfStudents")
    public GenericEntity getListOfStudents(String token, Object sectionId, Config.Data config) {
       
        // TODO: These hardcoded values are very temporary. Don't worry!
        List<String> studentIds = new ArrayList<String>();
        ViewConfig viewConfig = null;
        String sessionId = "819bdf64-dca3-411f-9a18-668cdf464c6c";
        sectionId = "da5b4d1a-63a3-46d6-a4f1-396b3308af83";
        
        List<GenericEntity> studentSummaries = getStudentSummaries(token, studentIds, viewConfig,
                sessionId, (String) sectionId);
        
        // apply assmt filters
        applyAssessmentFilters(studentSummaries, config);
        
        GenericEntity g = new GenericEntity();
        g.put("students", studentSummaries);
        return g;
    }

    
    /**
     * Find the required assessment results according to the data configuration. Filters out the rest.
     */
    private void applyAssessmentFilters(List<GenericEntity> studentSummaries, Config.Data config) {
        
        // Loop through student summaries
        for (GenericEntity summary : studentSummaries) {
            
            // Grab the student's assmt results. Grab assmt filters from config.
            List<Map> assmtResults = (List<Map>) (summary.remove(Constants.ATTR_STUDENT_ASSESSMENTS));

            Map<String, String> assmtFilters = (Map<String, String>) (config.getParams().get("assessmentFilter"));
            if (assmtFilters == null) {
                return;
            }
            
            Map<String, Object> newAssmtResults = new LinkedHashMap<String, Object>();
            
            // Loop through assmt filters
            for (String assmtName : assmtFilters.keySet()) {
            
                String timedLogic = assmtFilters.get(assmtName);
                
                // Apply filter. Add result to student summary.
                Map assmt = applyAssessmentFilter(assmtResults, assmtName, timedLogic);
                
                //Map<String, Map> assmt2 = new LinkedHashMap<String, Map>();
                //assmt2.put(timedLogic, assmt);
                newAssmtResults.put(assmtName, assmt);
            }
            
            summary.put(Constants.ATTR_STUDENT_ASSESSMENTS, newAssmtResults);
        }
    }
    
    private Map applyAssessmentFilter(List<Map> assmtResults, String assmtName, String timedLogic) {
        
        // filter by assmtName
        List<Map> filteredAssmtResults = new ArrayList<Map>();
        for (Map assmtResult : assmtResults) {
            String family = (String) ((Map) (assmtResult.get("assessments"))).get("assessmentFamilyHierarchyName");
            if (family.contains(assmtName)) {
                filteredAssmtResults.add(assmtResult);
            }
        }
        
        // call timed logic
        Map g = TimedLogic2.getMostRecentAssessment(filteredAssmtResults);
        return g;
    }
    
    
    private List<GenericEntity> getStudentAttendance(String token, String studentId, String startDate, String endDate) {
        return entityManager.getAttendance(token, studentId, startDate, endDate);
    }

    
    /**
     * Get a list of assessment results for one student, filtered by assessment name
     * 
     * @param username
     * @param studentId
     * @param config
     * @return
     */
    private List<GenericEntity> getStudentAssessments(String username, String studentId, ViewConfig config) {
        
        // get all assessments for student
        List<GenericEntity> assmts = entityManager.getStudentAssessments(username, studentId);
        
        return assmts;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Get student entity
     * 
     * @param token
     * @param studentId
     * @return
     */
    public GenericEntity getStudent(String token, String studentId) {
        return entityManager.getStudent(token, studentId);
    }
    
    /**
     * Get enriched student entity
     * 
     * @param token
     * @param studentId
     * @param config
     * @return
     */
    @EntityMapping("student")
    public GenericEntity getStudent(String token, Object studentId, Config.Data config) {
        String key = (String) studentId;
        return entityManager.getStudentForCSIPanel(token, key);
    }
    
    @EntityMapping("studentAttendance")
    public GenericEntity getAttendance(String token, Object studentIdObj, Config.Data config) {
        String studentId = (String) studentIdObj;
        // TODO: start using periods
        String period = config.getParams() == null ? null : (String) config.getParams().get("daysBack");
        int daysBack = (period == null) ? 360 : Integer.parseInt(period);
        MutableDateTime daysBackTime = new DateTime().toMutableDateTime();
        daysBackTime.addDays(-1 * daysBack);
        
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter dtfMonth = DateTimeFormat.forPattern("yyyy-MM");
        List<GenericEntity> attendanceList = 
                this.getStudentAttendance(token, studentId, null, null);
        Collections.sort(attendanceList, new Comparator<GenericEntity>() {

            @Override
            public int compare(GenericEntity att1, GenericEntity att2) {
                return ((String) att2.get("eventDate")).compareTo((String) att1.get("eventDate"));
            }
            
        });
        GenericEntity attendance = new GenericEntity();
        GenericEntity currentEntry;
        String currentMonth = null, month;
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);
        int tardyCount = 0, eAbsenceCount = 0, uAbsenceCount = 0, totalCount = 0;
        for (GenericEntity entry : attendanceList) {
            month = dtf.parseDateTime((String) entry.get("eventDate")).toString(dtfMonth);
            if (currentMonth == null) {
                currentMonth = month;
            } else if (!currentMonth.equals(month)) {
                currentEntry = new GenericEntity();
                currentEntry.put("eventDate", month);
                currentEntry.put("totalCount", totalCount);
                currentEntry.put("excusedAbsenceCount", eAbsenceCount);
                currentEntry.put("unexcusedAbsenceCount", uAbsenceCount);
                currentEntry.put("tardyCount", tardyCount);
                currentEntry.put("tardyRate", nf.format(100. * tardyCount / totalCount));
                currentEntry.put("attendanceRate", nf.format(100. * (totalCount - (uAbsenceCount + eAbsenceCount)) / totalCount));
                attendance.appendToList("attendance", currentEntry);
                currentMonth = month;
                uAbsenceCount = 0;
                eAbsenceCount = 0;
                tardyCount = 0;
                totalCount = 0;
            } 
            String value = (String) entry.get("attendanceEventCategory");
            if (value.contains("Tardy")) {
                tardyCount++;
            } else if  (value.contains("Excused Absence")) {
                eAbsenceCount++;
            } else if  (value.contains("Unexcused Absence")) {
                uAbsenceCount++;
            }
            totalCount++;
        }
        return attendance;
    } 
    
    public List<String> getSessionDates(String token, String sessionId) {
        // Get the session first.
        GenericEntity currentSession = entityManager.getSession(token, sessionId);
        List<String> dates = new ArrayList<String>();
        if (currentSession != null) {
            String beginDate = currentSession.getString("beginDate");
            String endDate = currentSession.getString("endDate");
            List<GenericEntity> potentialSessions = entityManager.getSessionsByYear(token,
                    currentSession.getString("schoolYear"));
            for (GenericEntity session : potentialSessions) {
                if (session.getString("beginDate").compareTo(beginDate) < 0) {
                    beginDate = session.getString("beginDate");
                }
                if (session.getString("endDate").compareTo(endDate) > 0) {
                    endDate = session.getString("endDate");
                }
            }
            
            dates.add(beginDate);
            dates.add(endDate);
        } else {
            dates.add("");
            dates.add("");
        }
        return dates;
    }
}
