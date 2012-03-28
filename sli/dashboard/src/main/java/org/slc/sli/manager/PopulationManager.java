package org.slc.sli.manager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
            String sessionId) {
        
        // Initialize student summaries
        List<GenericEntity> studentSummaries = entityManager.getStudents(token, studentIds);
        
        // Get student programs
        List<GenericEntity> studentPrograms = entityManager.getPrograms(token, studentIds);
        Map<String, Object> studentProgramMap = new HashMap<String, Object>();
        for (GenericEntity studentProgram : studentPrograms) {
            List<String> programs = (List<String>) studentProgram.get(Constants.ATTR_PROGRAMS);
            studentProgramMap.put(studentProgram.getString(Constants.ATTR_STUDENT_ID), programs);
        }
        
        // Get student assessments
        long startTime = System.nanoTime();
        Map<String, Object> studentAssessmentMap = new HashMap<String, Object>();
        for (String studentId : studentIds) {
            List<GenericEntity> studentAssessments = getStudentAssessments(token, studentId, viewConfig);
            studentAssessmentMap.put(studentId, studentAssessments);
        }
        double endTime = (System.nanoTime() - startTime) * 1.0e-9;
        log.warn("@@@@@@@@@@@@@@@@@@ Benchmark for assessment: {}\t Avg per student: {}", endTime, endTime
                / studentIds.size());
        
        Map<String, Object> studentAttendanceMap = createStudentAttendanceMap(token, studentIds, sessionId);
        
        // Add programs, attendance, and student assessment results to summaries
        for (GenericEntity studentSummary : studentSummaries) {
            if (studentSummary == null)
                continue;
            String id = studentSummary.getString(Constants.ATTR_ID);
            studentSummary.put(Constants.ATTR_PROGRAMS, studentProgramMap.get(id));
            studentSummary.put(Constants.ATTR_STUDENT_ASSESSMENTS, studentAssessmentMap.get(id));
            studentSummary.put(Constants.ATTR_STUDENT_ATTENDANCES, studentAttendanceMap.get(id));
        }
        
        return studentSummaries;
    }
    
    public Map<String, Object> createStudentAttendanceMap(String token, List<String> studentIds, String sessionId) {
        
        // Get attendance
        Map<String, Object> studentAttendanceMap = new HashMap<String, Object>();
        long startTime = System.nanoTime();
        
        List<String> dates = getSessionDates(token, sessionId);
        for (String studentId : studentIds) {
            long studentTime = System.nanoTime();
            List<GenericEntity> studentAttendance = getStudentAttendance(token, studentId, null, null);
            log.warn("@@@@@@@@@@@@@@@@@@ Benchmark for single: {}", (System.nanoTime() - studentTime) * 1.0e-9);

            if (studentAttendance != null && !studentAttendance.isEmpty())
                studentAttendanceMap.put(studentId, studentAttendance);
        }
        double endTime = (System.nanoTime() - startTime) * 1.0e-9;
        log.warn("@@@@@@@@@@@@@@@@@@ Benchmark for attendance: {}\t Avg per student: {}", endTime, endTime
                / studentIds.size());
        return studentAttendanceMap;
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
    
    /**
     * Get assessments from the api, given student assessment data
     * 
     * @param username
     * @param studentAssessments
     * @return
     */
    public List<GenericEntity> getAssessments(String username, List<GenericEntity> studentSummaries) {
        
        // get the list of assessment ids from the student assessments
        List<String> assmtIds = extractAssessmentIds(studentSummaries);
        
        // get the assessment objects from the api
        List<GenericEntity> assmts = entityManager.getAssessments(username, assmtIds);
        return assmts;
    }
    
    /**
     * Helper method to grab the assessment ids from the student assessment results
     * 
     * @return
     */
    private List<String> extractAssessmentIds(List<GenericEntity> studentSummaries) {
        
        List<String> assmtIds = new ArrayList<String>();
        
        // loop through student summaries, grab student assessment lists
        for (GenericEntity studentSummary : studentSummaries) {
            
            List<GenericEntity> studentAssmts = (List<GenericEntity>) studentSummary
                    .get(Constants.ATTR_STUDENT_ASSESSMENTS);
            for (GenericEntity studentAssmt : studentAssmts) {
                
                // add assessment id to the list
                String assmtId = studentAssmt.getString(Constants.ATTR_ASSESSMENT_ID);
                if (!(assmtIds.contains(assmtId))) {
                    assmtIds.add(assmtId);
                }
            }
        }
        
        return assmtIds;
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
