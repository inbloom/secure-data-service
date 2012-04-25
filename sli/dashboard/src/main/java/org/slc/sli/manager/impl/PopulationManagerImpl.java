package org.slc.sli.manager.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.PopulationManager;
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
public class PopulationManagerImpl implements PopulationManager {

    private static final String ATTENDANCE_TARDY = "Tardy";
    private static final String ATTENDANCE_ABSENCE = "Absence";

    private static Logger log = LoggerFactory.getLogger(PopulationManagerImpl.class);

    @Autowired
    private EntityManager entityManager;

    public PopulationManagerImpl() {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getAssessments(java.lang.String, java.util.List)
     */
    @Override
    public List<GenericEntity> getAssessments(String token, List<GenericEntity> studentSummaries) {
        Set<GenericEntity> assessments = new TreeSet<GenericEntity>(new Comparator<GenericEntity>() {
            @Override
            public int compare(GenericEntity att1, GenericEntity att2) {
                return (att2.getString("id")).compareTo(att1.getString("id"));
            }
        });
        for (GenericEntity studentSummary : studentSummaries) {
            List<Map<String, Object>> studentAssessments = (List<Map<String, Object>>) studentSummary
                    .get(Constants.ATTR_STUDENT_ASSESSMENTS);

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

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getStudentSummaries(java.lang.String,
     * java.util.List, org.slc.sli.config.ViewConfig, java.lang.String, java.lang.String)
     */
    @Override
    public List<GenericEntity> getStudentSummaries(String token, List<String> studentIds, ViewConfig viewConfig,
            String sessionId, String sectionId) {

        long startTime = System.nanoTime();
        // Initialize student summaries

        List<GenericEntity> studentSummaries = entityManager.getStudents(token, sectionId, studentIds);
        log.warn("@@@@@@@@@@@@@@@@@@ Benchmark for student section view: {}", (System.nanoTime() - startTime) * 1.0e-9);

        return studentSummaries;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getListOfStudents(java.lang.String,
     * java.lang.Object, org.slc.sli.entity.Config.Data)
     */
    @Override
    public GenericEntity getListOfStudents(String token, Object sectionId, Config.Data config) {

        // get student summary data
        List<GenericEntity> studentSummaries = getStudentSummaries(token, null, null, null, (String) sectionId);

        // apply assmt filters and flatten assmt data structure for easy fetching
        applyAssessmentFilters(studentSummaries, config);

        // data enhancements
        enhanceListOfStudents(studentSummaries, (String) sectionId);

        // get student grades (kindergarten, first grade, etc..) - TODO: get this data from the integrated API call
        updateWithStudentGrades(token, studentSummaries);

        GenericEntity result = new GenericEntity();
        result.put(Constants.ATTR_STUDENTS, studentSummaries);

        return result;
    }

    /**
     * Helper function to get a list of grades for a set of students.
     * NOTE: refactor the bundled API call to provide student grade, so this method won't be
     * necessary!
     *
     * @param token
     * @param studentSummaries
     * @return
     */
    public void updateWithStudentGrades(String token, List<GenericEntity> studentSummaries) {
        Map<String, GenericEntity> mapByUid = new HashMap<String, GenericEntity>();
        if (studentSummaries != null) {
            // get student uids
            for (GenericEntity student : studentSummaries) {
                mapByUid.put(student.getString(Constants.ATTR_ID), student);
            }
            List<GenericEntity> students = entityManager.getStudents(token, mapByUid.keySet());
            // put together set of grades
            for (GenericEntity s : students) {
                mapByUid.get(s.getString(Constants.ATTR_ID)).put(Constants.ATTR_GRADE_LEVEL, s.getString(Constants.ATTR_GRADE_LEVEL));
            }
        }
    }

    /**
     * Make enhancements that make it easier for front-end javascript to use the data
     *
     * @param studentSummaries
     */
    public void enhanceListOfStudents(List<GenericEntity> studentSummaries, String sectionId) {
        if (studentSummaries != null) {
            int count = 0;
            for (GenericEntity student : studentSummaries) {
                if (student == null) {
                    continue;
                }

                // clean out some unneeded gunk
                scrubStudentData(student);

                // add full name
                addFullName(student);

                // TODO - Switch once real API data is implemented
                addFinalGrade(student);
//                addGrade(student, count++);

                // transform assessment score format
                transformAssessmentFormat(student);

                // tally up attendance data
                tallyAttendanceData(student);

            }
            
            
        }
        
    }

    /**
     * Create an attribute for the full student name (first name + last name)
     *
     * @param student
     */
    public void addFullName(GenericEntity student) {

        Map name = (Map) student.get(Constants.ATTR_NAME);
        if (name != null) {
            String fullName = (String) name.get(Constants.ATTR_FIRST_NAME) + " "
                    + (String) name.get(Constants.ATTR_LAST_SURNAME);
            name.put(Constants.ATTR_FULL_NAME, fullName);
        }
    }

    /**
     * Tally up individual attendance events. Front-end needs to show aggregated results.
     *
     * @param student
     */
    public void tallyAttendanceData(GenericEntity student) {

        Map<String, Object> attendanceBody = (Map<String, Object>) student.get(Constants.ATTR_STUDENT_ATTENDANCES);
        if (attendanceBody != null) {

            List<Map<String, Object>> attendances = (List<Map<String, Object>>) attendanceBody
                    .get(Constants.ATTR_STUDENT_ATTENDANCES);
            int absenceCount = 0;
            int tardyCount = 0;
            if (attendances != null && attendances.size() > 0) {
                for (Map attendance : attendances) {

                    String eventCategory = (String) attendance.get(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY);

                    if (eventCategory.contains(ATTENDANCE_ABSENCE)) {
                        absenceCount++;

                    } else if (eventCategory.contains(ATTENDANCE_TARDY)) {
                        tardyCount++;
                    }
                }

                int attendanceRate = Math
                        .round((((float) (attendances.size() - absenceCount)) / attendances.size()) * 100);
                int tardyRate = Math.round((((float) tardyCount) / attendances.size()) * 100);

                attendanceBody.remove(Constants.ATTR_STUDENT_ATTENDANCES);
                attendanceBody.put(Constants.ATTR_ABSENCE_COUNT, absenceCount);
                attendanceBody.put(Constants.ATTR_TARDY_COUNT, tardyCount);
                attendanceBody.put(Constants.ATTR_ATTENDANCE_RATE, attendanceRate);
                attendanceBody.put(Constants.ATTR_TARDY_RATE, tardyRate);
            }
        }
    }

    /**
     * Modify the data structure for assessments, for front-end convenience
     *
     * @param student
     */
    public void transformAssessmentFormat(GenericEntity student) {

        Map studentAssmtAssocs = (Map) student.get(Constants.ATTR_ASSESSMENTS);
        if (studentAssmtAssocs == null) {
            return;
        }

        Collection<Map> assmtResults = studentAssmtAssocs.values();
        for (Map assmtResult : assmtResults) {

            if (assmtResult == null) {
                continue;
            }

            // for each score result, create a new attribute that makes the score easily accessible
            // without looping through this list
            List<Map> scoreResults = (List<Map>) assmtResult.get(Constants.ATTR_SCORE_RESULTS);
            if (scoreResults != null) {

                for (Map scoreResult : scoreResults) {

                    String type = (String) scoreResult.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD);
                    String result = (String) scoreResult.get(Constants.ATTR_RESULT);
                    assmtResult.put(type, result);
                }
            }

            // create a new attribute "perfLevel"
            List<List<Map>> perfLevelsDescs = (List<List<Map>>) assmtResult
                    .get(Constants.ATTR_PERFORMANCE_LEVEL_DESCRIPTOR);

            if (perfLevelsDescs != null) {
                for (List<Map> perfLevelsDesc : perfLevelsDescs) {
                    if (perfLevelsDesc != null && perfLevelsDesc.size() > 0) {

                        String perfLevel = (String) perfLevelsDesc.get(0).get(Constants.ATTR_CODE_VALUE);
                        assmtResult.put(Constants.ATTR_PERF_LEVEL, perfLevel);

                    }
                }
            }
        }
    }

    /**
     * Clean out some student data, so we don't pass too much unnecessary stuff to the front-end
     *
     * @param student
     */
    public void scrubStudentData(GenericEntity student) {

        student.remove(Constants.ATTR_LINKS);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void addFinalGrade(GenericEntity student){
    	try {
        	Map<String, Object> transcripts = (Map<String, Object>) student.get(Constants.ATTR_TRANSCRIPT);  
        	List<Map<String, Object>> stuSectAssocs = (List<Map<String, Object>>) transcripts.get(Constants.ATTR_STUDENT_SECTION_ASSOC);
        	List<Map<String, Object>> stuTransAssocs = (List<Map<String, Object>>) transcripts.get(Constants.ATTR_STUDENT_TRANSCRIPT_ASSOC);       	

            // Course IDs ordered so that newest course is first
        	Map <Date, Map<String, Object>> previousCourseIds = new TreeMap<Date, Map<String, Object>>(
    				new Comparator<Date> () { 
    					public int compare(Date a, Date b) {
    						if( a.compareTo(b) > 0) {
    							return -1;
    						} else if (a.compareTo(b) == 0) {
    							return 0;
    						} else {
    							return 1;
    						}
    					}
    		        }
    		);
    		
    		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        	
    		//Collect all courseIds based on subject Area if possible
    		for(Map<String,Object> assoc : stuSectAssocs){
    			Map<String, Object> sections = (Map<String, Object>) assoc.get(Constants.ATTR_SECTIONS);
    		    Date date = formatter.parse( (String) ( (Map) sections.get(Constants.ATTR_SESSIONS)).get("endDate"));
    			previousCourseIds.put(date, sections);
    	    }

    		
    		//Iterate through the course Id's and grab transcripts grades, once we have 2 transcript grades, we're done
    		int count = 1;
    		int semestersAllowed = 2;
        	for(Date key: previousCourseIds.keySet()) {
        		
        		String courseId = (String) (previousCourseIds.get(key).get(Constants.ATTR_COURSE_ID));
            	for(Map<String,Object> assoc : stuTransAssocs){
                    if(courseId.equalsIgnoreCase((String)assoc.get(Constants.ATTR_COURSE_ID))) {
                        String finalLetterGrade = (String) assoc.get(Constants.ATTR_FINAL_LETTER_GRADE);
                        Map<String, Object> sections = (Map) previousCourseIds.get(key);
                        String term = (String) ((Map)sections.get(Constants.ATTR_SESSIONS)).get("term");
                        String year = (String) ((Map)sections.get(Constants.ATTR_SESSIONS)).get("schoolYear");
                        String courseTitle = (String) ((Map)sections.get(Constants.ATTR_COURSES)).get("courseTitle");
                        if(finalLetterGrade != null) {
                            Map <String, Object> grade = new LinkedHashMap<String,Object>();
                        	grade.put("grade", finalLetterGrade);
                        	grade.put("header", year + " " + term + " " + courseTitle);
                            student.put("score" + count, grade );
                            ++count;
                            break;
                        }
                    }
                }
        	    
        		if(count > semestersAllowed) {
        			break;
        		}
        	}
    		
    	} catch (ClassCastException ex) {
    		ex.printStackTrace();
    		Map <String, Object> grade = new LinkedHashMap<String,Object>();
            student.put(Constants.ATTR_SCORE_RESULTS, grade.put(Constants.ATTR_FINAL_LETTER_GRADE, "?"));
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    		Map <String, Object> grade = new LinkedHashMap<String,Object>();
            student.put(Constants.ATTR_SCORE_RESULTS, grade.put(Constants.ATTR_FINAL_LETTER_GRADE, "?"));
    	} catch (ParseException ex) {
    		ex.printStackTrace();
    		Map <String, Object> grade = new LinkedHashMap<String,Object>();
            student.put(Constants.ATTR_SCORE_RESULTS, grade.put(Constants.ATTR_FINAL_LETTER_GRADE, "?"));
    	}
    }

    /**
     * Find the required assessment results according to the data configuration. Filter out the
     * rest.
     */
    public void applyAssessmentFilters(List<GenericEntity> studentSummaries, Config.Data config) {

        // Loop through student summaries
        if (studentSummaries != null) {
            for (GenericEntity summary : studentSummaries) {

                // Grab the student's assmt results. Grab assmt filters from config.
                List<Map> assmtResults = (List<Map>) (summary.remove(Constants.ATTR_STUDENT_ASSESSMENTS));

                Map<String, Object> param = config.getParams();
                if (param == null) {
                    return;
                }
                Map<String, String> assmtFilters = (Map<String, String>) (config.getParams()
                        .get(Constants.CONFIG_ASSESSMENT_FILTER));
                if (assmtFilters == null) {
                    return;
                }

                Map<String, Object> newAssmtResults = new LinkedHashMap<String, Object>();

                // Loop through assmt filters
                for (String assmtFamily : assmtFilters.keySet()) {

                    String timeSlotStr = assmtFilters.get(assmtFamily);
                    if (timeSlotStr != null) {

                        TimedLogic2.TimeSlot timeSlot = TimedLogic2.TimeSlot.valueOf(timeSlotStr);

                        // Apply filter. Add result to student summary.
                        Map assmt = applyAssessmentFilter(assmtResults, assmtFamily, timeSlot);
                        newAssmtResults.put(assmtFamily, assmt);
                    }
                }

                summary.put(Constants.ATTR_ASSESSMENTS, newAssmtResults);
            }
        }
    }

    /**
     * Filter a list of assessment results, based on the assessment family and timed logic
     *
     * @param assmtResults
     * @param assmtFamily
     * @param timedLogic
     * @return
     */
    private Map applyAssessmentFilter(List<Map> assmtResults, String assmtFamily, TimedLogic2.TimeSlot timeSlot) {

        // filter by assmt family name
        List<Map> studentAssessmentFiltered = new ArrayList<Map>();
        for (Map assmtResult : assmtResults) {
            String family = (String) ((Map) (assmtResult.get(Constants.ATTR_ASSESSMENTS)))
                    .get(Constants.ATTR_ASSESSMENT_FAMILY_HIERARCHY_NAME);
            if (family.contains(assmtFamily)) {
                studentAssessmentFiltered.add(assmtResult);
            }
        }

        if (studentAssessmentFiltered.size() == 0) {
            return null;
        }

        Map chosenAssessment = null;

        // TODO: fix objective assessment code and use it
        String objAssmtCode = "";

        // call timeslot logic to pick out the assessment we want
        switch (timeSlot) {

            case MOST_RECENT_RESULT:
                chosenAssessment = TimedLogic2.getMostRecentAssessment(studentAssessmentFiltered);
                break;

            case HIGHEST_EVER:
                if (!objAssmtCode.equals("")) {
                    chosenAssessment = TimedLogic2.getHighestEverObjAssmt(studentAssessmentFiltered, objAssmtCode);
                } else {
                    chosenAssessment = TimedLogic2.getHighestEverAssessment(studentAssessmentFiltered);
                }
                break;

            case MOST_RECENT_WINDOW:

                List<Map> assessmentMetaData = new ArrayList<Map>();

                // TODO: get the assessment meta data
                /*
                 * Set<String> assessmentIds = new HashSet<String>();
                 * for (Map studentAssessment : studentAssessmentFiltered) {
                 * String assessmentId = (String)
                 * studentAssessment.get(Constants.ATTR_ASSESSMENT_ID);
                 * if (!assessmentIds.contains(assessmentId)) {
                 * GenericEntity assessment = metaDataResolver.getAssmtById(assessmentId);
                 * assessmentMetaData.add(assessment);
                 * assessmentIds.add(assessmentId);
                 * }
                 * }
                 */

                chosenAssessment = TimedLogic2.getMostRecentAssessmentWindow(studentAssessmentFiltered,
                        assessmentMetaData);
                break;

            default:

                // Decide whether to throw runtime exception here. Should timed logic default @@@
                chosenAssessment = TimedLogic2.getMostRecentAssessment(studentAssessmentFiltered);
                break;
        }

        return chosenAssessment;
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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.manager.PopulationManagerI#setEntityManager(org.slc.sli.manager.EntityManager)
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getStudent(java.lang.String, java.lang.String)
     */
    @Override
    public GenericEntity getStudent(String token, String studentId) {
        return entityManager.getStudent(token, studentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getStudent(java.lang.String, java.lang.Object,
     * org.slc.sli.entity.Config.Data)
     */
    @Override
    public GenericEntity getStudent(String token, Object studentId, Config.Data config) {
        String key = (String) studentId;
        return entityManager.getStudentForCSIPanel(token, key);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getAttendance(java.lang.String, java.lang.Object,
     * org.slc.sli.entity.Config.Data)
     */
    @Override
    public GenericEntity getAttendance(String token, Object studentIdObj, Config.Data config) {
        String studentId = (String) studentIdObj;
        // TODO: start using periods
        String period = config.getParams() == null ? null : (String) config.getParams().get("daysBack");
        int daysBack = (period == null) ? 360 : Integer.parseInt(period);
        MutableDateTime daysBackTime = new DateTime().toMutableDateTime();
        daysBackTime.addDays(-1 * daysBack);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter dtfMonth = DateTimeFormat.forPattern("yyyy-MM");
        List<GenericEntity> attendanceList = this.getStudentAttendance(token, studentId, null, null);
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
                currentEntry.put("attendanceRate",
                        nf.format(100. * (totalCount - (uAbsenceCount + eAbsenceCount)) / totalCount));
                attendance.appendToList("attendance", currentEntry);
                currentMonth = month;
                uAbsenceCount = 0;
                eAbsenceCount = 0;
                tardyCount = 0;
                totalCount = 0;
            }
            String value = (String) entry.get("attendanceEventCategory");
            if (value.contains(ATTENDANCE_TARDY)) {
                tardyCount++;
            } else if (value.contains("Excused Absence")) {
                eAbsenceCount++;
            } else if (value.contains("Unexcused Absence")) {
                uAbsenceCount++;
            }
            totalCount++;
        }
        return attendance;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getSessionDates(java.lang.String,
     * java.lang.String)
     */
    @Override
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
