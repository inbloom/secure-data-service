package org.slc.sli.manager.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.GenericEntityEnhancer;
import org.slc.sli.manager.ApiClientManager;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.PopulationManager;
import org.slc.sli.util.Constants;
import org.slc.sli.util.TimedLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PopulationManager facilitates creation of logical aggregations of EdFi
 * entities/associations such as a student summary comprised of student profile,
 * fment, program, and assessment information in order to deliver the
 * Population Summary interaction.
 *
 * @author Robert Bloh
 *
 */
public class PopulationManagerImpl extends ApiClientManager implements PopulationManager {

    private static final String ATTENDANCE_TARDY = "Tardy";
    private static final String ATTENDANCE_ABSENCE = "Absence";

    private static final String STUDENT_CACHE = "user.student";

    private static Logger log = LoggerFactory.getLogger(PopulationManagerImpl.class);

    @Autowired
    private EntityManager entityManager;

    public PopulationManagerImpl() {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.manager.PopulationManagerI#getAssessments(java.lang.String,
     * java.util.List)
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
     * @see
     * org.slc.sli.manager.PopulationManagerI#getStudentSummaries(java.lang.
     * String, java.util.List, org.slc.sli.config.ViewConfig, java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<GenericEntity> getStudentSummaries(String token, List<String> studentIds, String sessionId,
            String sectionId) {

        long startTime = System.nanoTime();
        // Initialize student summaries

        List<GenericEntity> studentSummaries = entityManager.getStudents(token, sectionId, studentIds);
        log.warn("@@@@@@@@@@@@@@@@@@ Benchmark for student section view: {}", (System.nanoTime() - startTime) * 1.0e-9);

        return studentSummaries;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.manager.PopulationManagerI#getListOfStudents(java.lang.String
     * , java.lang.Object, org.slc.sli.entity.Config.Data)
     */
    @Override
    public GenericEntity getListOfStudents(String token, Object sectionId, Config.Data config) {

        String id = (String) sectionId;

        // get student summary data
        List<GenericEntity> studentSummaries = getStudentSummaries(token, null, null, id);

        // apply assmt filters and flatten assmt data structure for easy
        // fetching
        applyAssessmentFilters(studentSummaries, config);

        // data enhancements
        enhanceListOfStudents(studentSummaries, id);

        GenericEntity result = new GenericEntity();
        Collections.sort(studentSummaries, STUDENT_COMPARATOR);
        result.put(Constants.ATTR_STUDENTS, studentSummaries);

        return result;
    }

    /**
     * Make enhancements that make it easier for front-end javascript to use the
     * data
     *
     * @param studentSummaries
     */
    public void enhanceListOfStudents(List<GenericEntity> studentSummaries, String sectionId) {
        if (studentSummaries != null) {
            for (GenericEntity student : studentSummaries) {
                if (student == null) {
                    continue;
                }

                // clean out some unneeded gunk
                scrubStudentData(student);

                // add full name
                addFullName(student);

                // add the final grade
                addFinalGrades(student, sectionId);

                // add the grade book
                addCurrentSemesterGrades(student, sectionId);

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
     * Tally up individual attendance events. Front-end needs to show aggregated
     * results.
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

            // for each score result, create a new attribute that makes the
            // score easily accessible
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
     * Clean out some student data, so we don't pass too much unnecessary stuff
     * to the front-end
     *
     * @param student
     */
    public void scrubStudentData(GenericEntity student) {

        student.remove(Constants.ATTR_LINKS);
    }

    /**
     * Grabs the subject area from the data based on the section ID.
     *
     * @param stuSectAssocs
     * @param sectionId
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String getSubjectArea(List<Map<String, Object>> stuSectAssocs, String sectionId) {
        String subjectArea = null;
        for (Map<String, Object> assoc : stuSectAssocs) {
            if (sectionId.equalsIgnoreCase((String) assoc.get(Constants.ATTR_SECTION_ID))) {
                Map<String, Object> sections = (Map) assoc.get(Constants.ATTR_SECTIONS);
                subjectArea = getSubjectArea(sections);
                break;
            }
        }

        return subjectArea;
    }

    /**
     * Grabs the Subject Area from a section.
     *
     * @param sections
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String getSubjectArea(Map<String, Object> sections) {
        if (sections == null) {
            return null;
        }

        Map<String, Object> courses = (Map) sections.get(Constants.ATTR_COURSES);
        if (courses == null) {
            return null;
        }

        return (String) courses.get(Constants.ATTR_SUBJECTAREA);
    }

    /**
     * Extracts grades from transcriptAssociationRecord based on sections in the
     * past. For each section where a transcript with final letter grade
     * exist, the grade is added to the list of grades for the semester.
     *
     * @param student
     * @param interSections
     * @param stuTransAssocs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addSemesterFinalGrades(GenericEntity student, List<Map<String, Object>> interSections,
            List<Map<String, Object>> stuTransAssocs) {

        // Iterate through the course Id's and grab transcripts grades, once
        // we have NUMBER_OF_SEMESTERS transcript grades, we're done
        for (Map<String, Object> section : interSections) {
            String courseId = (String) section.get(Constants.ATTR_COURSE_ID);

            // Find the correct course. If that course is found in
            // the transcript, then record that letter grade to the
            // semesterScores.
            for (Map<String, Object> assoc : stuTransAssocs) {
                if (courseId.equalsIgnoreCase((String) assoc.get(Constants.ATTR_COURSE_ID))) {
                    String finalLetterGrade = (String) assoc.get(Constants.ATTR_FINAL_LETTER_GRADE);
                    String courseTitle = (String) ((Map) section.get(Constants.ATTR_COURSES))
                            .get(Constants.ATTR_COURSE_TITLE);
                    if (finalLetterGrade != null) {
                        String semesterString = buildSemesterYearString(section);
                        Map<String, Object> grade = new LinkedHashMap<String, Object>();
                        grade.put(Constants.SECTION_LETTER_GRADE, finalLetterGrade);
                        grade.put(Constants.SECTION_COURSE, courseTitle);
                        List<Map<String, Object>> semesterScores = (List<Map<String, Object>>) student
                                .get(semesterString);
                        if (semesterScores == null) {
                            semesterScores = new ArrayList<Map<String, Object>>();
                        }
                        semesterScores.add(grade);
                        student.put(semesterString, semesterScores);
                        break;
                    }
                }
            }
        }
    }

    /**
     * This method adds the final grades of a student to the student data. It
     * will only grab the latest two grades. Ideally we would filter on subject
     * area, but there is currently no subject area data in the SDS.
     *
     * @param student
     */
    @SuppressWarnings({ "unchecked" })
    private void addFinalGrades(GenericEntity student, String sectionId) {
        try {
            Map<String, Object> transcripts = (Map<String, Object>) student.get(Constants.ATTR_TRANSCRIPT);
            if (transcripts == null) {
                return;
            }

            /*
             * For each student section association, we have to determine if it is in the same
             * subject area as the sectionid passed. If it is then we add it to our List.
             * Once we have a list of sections. We can grab all of the semester grades
             * for those sections whose subject area intersect.
             */
            List<Map<String, Object>> stuSectAssocs = (List<Map<String, Object>>) transcripts
                    .get(Constants.ATTR_STUDENT_SECTION_ASSOC);
            if (stuSectAssocs == null) {
                return;
            }

            String subjectArea = getSubjectArea(stuSectAssocs, sectionId);
            List<Map<String, Object>> interSections = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> assoc : stuSectAssocs) {
                Map<String, Object> sections = (Map<String, Object>) assoc.get(Constants.ATTR_SECTIONS);
                // This case will catch if the subjectArea is null
                if (subjectArea == null || subjectArea.equalsIgnoreCase(getSubjectArea(sections))) {
                    interSections.add(sections);
                }
            }

            addSemesterFinalGrades(student, interSections,
                    (List<Map<String, Object>>) transcripts.get(Constants.ATTR_STUDENT_TRANSCRIPT_ASSOC));

        } catch (ClassCastException ex) {
            log.error("Error occured processing Final Grades", ex);
            Map<String, Object> grade = new LinkedHashMap<String, Object>();
            student.put(Constants.ATTR_SCORE_RESULTS, grade.put(Constants.ATTR_FINAL_LETTER_GRADE, "?"));
        } catch (NullPointerException ex) {
            log.error("Error occured processing Final Grades", ex);
            Map<String, Object> grade = new LinkedHashMap<String, Object>();
            student.put(Constants.ATTR_SCORE_RESULTS, grade.put(Constants.ATTR_FINAL_LETTER_GRADE, "?"));
        }
    }

    /**
     * Returns the term and the year as a string for a given student Section association.
     *
     * @param stuSectAssocs
     * @param sectionId
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String getSemesterYear(List<Map<String, Object>> stuSectAssocs, String sectionId) {
        String semesterString = null;
        for (Map<String, Object> assoc : stuSectAssocs) {
            if (((String) assoc.get(Constants.ATTR_SECTION_ID)).equalsIgnoreCase(sectionId)) {
                Map<String, Object> sections = (Map) assoc.get(Constants.ATTR_SECTIONS);
                semesterString = buildSemesterYearString(sections);
                break;
            }
        }
        return semesterString;
    }

    /**
     * Extracts the semester+Year from the section passed.
     *
     * @param sections
     * @return (e.g. FallSemester2010-2011 )
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String buildSemesterYearString(Map<String, Object> section) {
        String semesterString = null;
        if (section == null) {
            return semesterString;
        }

        Map<String, Object> sessions = (Map) section.get(Constants.ATTR_SESSIONS);
        if (sessions == null) {
            return semesterString;
        }

        String term = (String) sessions.get(Constants.ATTR_TERM);
        String year = (String) sessions.get(Constants.ATTR_SCHOOL_YEAR);
        if (term != null && year != null) {
            semesterString = term.replaceAll(" ", "") + year.replaceAll(" ", "");
        }

        return semesterString;
    }

    /**
     * Adds the current semester grades to the student in a easily retrievable manner.
     *
     * @param student
     * @param sectionId
     */
    @SuppressWarnings("unchecked")
    private void addCurrentSemesterGrades(GenericEntity student, String sectionId) {
        // Sort the grades
        SortedSet<GenericEntity> sortedList = new TreeSet<GenericEntity>(new Comparator<GenericEntity>() {
            @Override
            public int compare(GenericEntity a, GenericEntity b) {
                Object dateA = a.get(Constants.ATTR_DATE_FULFILLED);
                Object dateB = b.get(Constants.ATTR_DATE_FULFILLED);
                if (dateA == null) {
                    return 1;
                } else if (dateB == null) {
                    return -1;
                }
                return ((Date) dateA).compareTo((Date) dateB);
            }
        });

        // Get the term and year
        try {
            Map<String, Object> transcripts = (Map<String, Object>) student.get(Constants.ATTR_TRANSCRIPT);
            if (transcripts == null) {
                return;
            }
            List<Map<String, Object>> stuSectAssocs = (List<Map<String, Object>>) transcripts
                    .get(Constants.ATTR_STUDENT_SECTION_ASSOC);
            if (stuSectAssocs == null) {
                return;
            }
            String semesterString = getSemesterYear(stuSectAssocs, sectionId);
            if (semesterString == null) {
                return;
            }

            // iterate and add to letter grade
            List<Map<String, Object>> gradeEntries = (List<Map<String, Object>>) student
                    .get(Constants.ATTR_STUDENT_GRADEBOOK_ENTRIES);
            if (gradeEntries == null) {
                return;
            }

            for (Map<String, Object> currentGrade : gradeEntries) {
                GenericEntity gradeDate = new GenericEntity();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Object grade = currentGrade.get(Constants.ATTR_LETTER_GRADE_EARNED);
                if (grade == null) {
                    grade = currentGrade.get(Constants.ATTR_NUMERIC_GRADE_EARNED);
                    if (grade == null) {
                        continue;
                    }
                }

                try {
                    Object dateString = currentGrade.get(Constants.ATTR_DATE_FULFILLED);
                    if (dateString != null) {
                        Date date = formatter.parse((String) dateString);
                        gradeDate.put(Constants.ATTR_DATE_FULFILLED, date);
                    }
                } catch (ParseException e) {
                    log.error("Error parsing dates for a Current Section grade.");
                }
                gradeDate.put(Constants.ATTR_GRADE_EARNED, grade);
                sortedList.add(gradeDate);
            }
            int count = 0;
            for (GenericEntity entity : sortedList) {
                student.put(semesterString + "-" + count++, entity);
            }

        } catch (ClassCastException ex) {
            log.error("Error occured processing Gradebook Entries", ex);
        } catch (NullPointerException ex) {
            log.error("Error occured processing Gradebook Entries", ex);
        }

    }

    /**
     * Find the required assessment results according to the data configuration.
     * Filter out the rest.
     */
    @SuppressWarnings("unchecked")
    public void applyAssessmentFilters(List<GenericEntity> studentSummaries, Config.Data config) {

        // Loop through student summaries
        if (studentSummaries != null) {
            for (GenericEntity summary : studentSummaries) {

                // Grab the student's assmt results. Grab assmt filters from
                // config.
                List<Map<String, Object>> assmtResults = (List<Map<String, Object>>) (summary
                        .remove(Constants.ATTR_STUDENT_ASSESSMENTS));

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

                        TimedLogic.TimeSlot timeSlot = TimedLogic.TimeSlot.valueOf(timeSlotStr);

                        // Apply filter. Add result to student summary.
                        Map assmt = applyAssessmentFilter(assmtResults, assmtFamily, timeSlot);
                        newAssmtResults.put(assmtFamily.replace('.', '_'), assmt);
                    }
                }

                summary.put(Constants.ATTR_ASSESSMENTS, newAssmtResults);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> filterAssessmentByFamily(List<?> assmtResults, String assmtFamily) {
        // filter by assmt family name
        List<Map<String, Object>> studentAssessmentFiltered = new ArrayList<Map<String, Object>>();
        Map<String, Object> assmtResult, assessments;
        String family;
        for (Object assmtResultObj : assmtResults) {
            assmtResult = (Map<String, Object>) assmtResultObj;
            assessments = (Map<String, Object>) assmtResult.get(Constants.ATTR_ASSESSMENTS);
            if (assessments != null) {
                family = (String) assessments.get(Constants.ATTR_ASSESSMENT_FAMILY_HIERARCHY_NAME);
                if (family != null && family.contains(assmtFamily)) {
                    studentAssessmentFiltered.add(assmtResult);
                }
            }
        }
        return studentAssessmentFiltered;
    }

    /**
     * Filter a list of assessment results, based on the assessment family and
     * timed logic
     *
     * @param assmtResults
     * @param assmtFamily
     * @param timedLogic
     * @return
     */
    private Map applyAssessmentFilter(List<Map<String, Object>> assmtResults, String assmtFamily,
            TimedLogic.TimeSlot timeSlot) {
        // filter by assmt family name
        List<Map<String, Object>> studentAssessmentFiltered = filterAssessmentByFamily(assmtResults, assmtFamily);

        if (studentAssessmentFiltered.size() == 0) {
            return null;
        }

        Map chosenAssessment = null;

        // TODO: fix objective assessment code and use it
        String objAssmtCode = "";

        // call timeslot logic to pick out the assessment we want
        switch (timeSlot) {

            case MOST_RECENT_RESULT:
                chosenAssessment = TimedLogic.getMostRecentAssessment(studentAssessmentFiltered);
                break;

            case HIGHEST_EVER:
                if (!objAssmtCode.equals("")) {
                    chosenAssessment = TimedLogic.getHighestEverObjAssmt(studentAssessmentFiltered, objAssmtCode);
                } else {
                    chosenAssessment = TimedLogic.getHighestEverAssessment(studentAssessmentFiltered);
                }
                break;

            case MOST_RECENT_WINDOW:

                List<Map<String, Object>> assessmentMetaData = new ArrayList<Map<String, Object>>();

                // TODO: get the assessment meta data
                /*
                 * Set<String> assessmentIds = new HashSet<String>(); for (Map
                 * studentAssessment : studentAssessmentFiltered) { String
                 * assessmentId = (String)
                 * studentAssessment.get(Constants.ATTR_ASSESSMENT_ID); if
                 * (!assessmentIds.contains(assessmentId)) { GenericEntity
                 * assessment = metaDataResolver.getAssmtById(assessmentId);
                 * assessmentMetaData.add(assessment);
                 * assessmentIds.add(assessmentId); } }
                 */

                chosenAssessment = TimedLogic.getMostRecentAssessmentWindow(studentAssessmentFiltered,
                        assessmentMetaData);
                break;

            default:

                // Decide whether to throw runtime exception here. Should timed
                // logic default @@@
                chosenAssessment = TimedLogic.getMostRecentAssessment(studentAssessmentFiltered);
                break;
        }

        return chosenAssessment;
    }

    private List<GenericEntity> getStudentAttendance(String token, String studentId, String startDate, String endDate) {
        List<GenericEntity> list = entityManager.getAttendance(token, studentId, startDate, endDate);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.manager.PopulationManagerI#setEntityManager(org.slc.sli.manager
     * .EntityManager)
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getStudent(java.lang.String,
     * java.lang.String)
     */
    @Override
    public GenericEntity getStudent(String token, String studentId) {
        return entityManager.getStudent(token, studentId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.manager.PopulationManagerI#getStudent(java.lang.String,
     * java.lang.Object, org.slc.sli.entity.Config.Data)
     */
    @Override
    public GenericEntity getStudent(String token, Object studentId, Config.Data config) {
        String key = (String) studentId;
        GenericEntity student = getFromCache(STUDENT_CACHE, token, key);
        if (student == null) {
            student = entityManager.getStudentForCSIPanel(token, key);
            putToCache(STUDENT_CACHE, token, key, student);
        }
        return student;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.manager.PopulationManagerI#getAttendance(java.lang.String,
     * java.lang.Object, org.slc.sli.entity.Config.Data)
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
                return ((String) att2.get(Constants.ATTR_ATTENDANCE_DATE)).compareTo((String) att1
                        .get(Constants.ATTR_ATTENDANCE_DATE));
            }

        });
        GenericEntity attendance = new GenericEntity();
        GenericEntity currentEntry;
        String currentMonth = null, month;
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);
        int tardyCount = 0, eAbsenceCount = 0, uAbsenceCount = 0, totalCount = 0;
        String date;
        for (GenericEntity entry : attendanceList) {
            date = (String) entry.get(Constants.ATTR_ATTENDANCE_DATE);
            month = (date == null) ? null : dtf.parseDateTime(date).toString(dtfMonth);
            if (currentMonth == null) {
                currentMonth = month;
            } else if (!currentMonth.equals(month)) {
                currentEntry = new GenericEntity();
                currentEntry.put(Constants.ATTR_ATTENDANCE_DATE, month);
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
            String value = (String) entry.get(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY);
            if (value != null) {
                if (value.contains(ATTENDANCE_TARDY)) {
                    tardyCount++;
                } else if (value.contains("Excused Absence")) {
                    eAbsenceCount++;
                } else if (value.contains("Unexcused Absence")) {
                    uAbsenceCount++;
                }
            }
            totalCount++;
        }
        return attendance;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.manager.PopulationManagerI#getSessionDates(java.lang.String,
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

    @Override
    public GenericEntity getStudentsBySearch(String token, Object nameQuery, Config.Data config) {
        // Map<String, String> nameQueryMap = (Map<String, String>) nameQuery;
        GenericEntity studentSearch = new GenericEntity();
        // make sure the incoming nameQuery is of the proper format:
        // expected is an array of Strings generated by StudentSearch.get()
        String[] nameList;
        try {
            nameList = (String[]) nameQuery;
        } catch (ClassCastException cce) {
            setStudentSearchEntity(studentSearch, new LinkedList<GenericEntity>(), "", "", "", 0, 1, 50, 1);
            return studentSearch;
        }
        // the query should contain at a minimum either a first or a last name
        // both fields must be present in input, but one (not both) can optionally be empty
        String firstName = null, lastName = null;
        if (nameList.length >= 2) {
            firstName = nameList[0];
            lastName = nameList[1];
        }
        if ((firstName == null || firstName.equals("")) && (lastName == null || lastName.equals(""))) {
            setStudentSearchEntity(studentSearch, new LinkedList<GenericEntity>(), "", "", "", 0, 1, 50, 1);
            return studentSearch;
        }
        // optionally (but typically), it should also contain pagination information
        int pageNum = 1, pageSize = 50;
        if (nameList.length >= 4) {
            try {
                pageNum = Integer.parseInt(nameList[2]);
                pageSize = Integer.parseInt(nameList[3]);
            } catch (NumberFormatException nfe) {
                // pagination information was in an incorrect format, use default values;
                // this will never happen unless StudentSearch.get is changed incorrectly
                pageNum = 1;
                pageSize = 50;
            }
        }

        String searchString = firstName + " " + lastName;
        searchString = searchString.trim();

        // Do two searches, one with input as capitalized by user, the other with capitalized
        // first and last name
        List<GenericEntity> students = entityManager.getStudentsFromSearch(token, firstName, lastName);

        List<GenericEntity> titleCaseStudents = entityManager.getStudentsFromSearch(token,
                WordUtils.capitalize(firstName), WordUtils.capitalize(lastName));

        // Now we remove the duplicate records from the previous two searches
        // TreeSet is used for this so we can define our own comparator
        TreeSet<GenericEntity> studentSet = new TreeSet<GenericEntity>(new Comparator<GenericEntity>() {
            @Override
            public int compare(GenericEntity ge1, GenericEntity ge2) {
                // note that we only care about object uniqueness (to use the Set interface)
                // therefore, we sort by id which is not useful from a sorting perspective
                return ge1.getId().compareTo(ge2.getId());
            }
        });
        studentSet.addAll(students);
        studentSet.addAll(titleCaseStudents);

        List<GenericEntity> enhancedStudents = new LinkedList<GenericEntity>();
        HashMap<String, GenericEntity> retrievedSchools = new HashMap<String, GenericEntity>();
        GenericEntity school;
        Iterator<GenericEntity> studentSetIterator = studentSet.iterator();
        while (studentSetIterator.hasNext()) {
            GenericEntity student = studentSetIterator.next();
            student = entityManager.getStudent(token, student.getId());
            addFullName(student);

            if (student.get("schoolId") != null) {
                if (retrievedSchools.containsKey(student.get("schoolId"))) {
                    school = retrievedSchools.get(student.get("schoolId"));
                    student.put("currentSchoolName", school.get(Constants.ATTR_NAME_OF_INST));
                } else {
                    school = entityManager.getEntity(token, Constants.ATTR_SCHOOLS, student.getString("schoolId"),
                            new HashMap());
                    retrievedSchools.put(school.getString(Constants.ATTR_ID), school);
                    student.put("currentSchoolName", school.get(Constants.ATTR_NAME_OF_INST));
                }
            }
            GenericEntityEnhancer.enhanceStudent(student);
            enhancedStudents.add(student);
        }
        // This is a temporary solution until we decide how to integrate the search with the API
        // pagination calls. Currently, when API is used, the total number of search results is
        // stored in the header which is not accessible. Also, code above performs two searches and
        // combines results - this is a problem if API pagination is used.
        int numResults = enhancedStudents.size();
        // verify sensible page number was requested (negatives not allowed)
        if (pageNum < 1) {
            pageNum = 1;
        }
        // verify sensible page size was specified (negatives not allowed)
        if (pageSize < 1) {
            pageSize = 1;
        }
        // calculate the last available page from the number of results and page size
        int maxPageNum = numResults / pageSize;
        if (numResults % pageSize != 0) {
            maxPageNum++;
        }
        // requested page number cannot exceed last available page
        if (pageNum > maxPageNum) {
            pageNum = maxPageNum;
        }
        // fetch the subset of search results specified by the pagination request
        if (numResults > pageSize) {
            int beginIndex = (pageNum - 1) * pageSize;
            int endIndex = beginIndex + pageSize;
            if (endIndex > numResults) {
                endIndex = numResults;
            }
            enhancedStudents = enhancedStudents.subList(beginIndex, endIndex);
        }

        // fill the search map with results
        setStudentSearchEntity(studentSearch, enhancedStudents, searchString, firstName, lastName, numResults, pageNum,
                pageSize, maxPageNum);
        return studentSearch;
    }

    private void setStudentSearchEntity(GenericEntity studentSearch, List<GenericEntity> students, String searchStr,
            String firstName, String lastName, int numResults, int pageNum, int pageSize, int maxPageNum) {
        studentSearch.put(Constants.ATTR_STUDENTS, students);
        studentSearch.put(Constants.ATTR_SEARCH_STRING, searchStr);
        studentSearch.put(Constants.ATTR_FIRST_NAME, firstName);
        studentSearch.put(Constants.ATTR_LAST_SURNAME, lastName);
        studentSearch.put(Constants.ATTR_NUM_RESULTS, numResults);
        studentSearch.put(Constants.ATTR_SEARCH_PAGE_NUM, pageNum);
        studentSearch.put(Constants.ATTR_SEARCH_PAGE_SIZE, pageSize);
        studentSearch.put(Constants.ATTR_SEARCH_MAX_PAGE_NUM, maxPageNum);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GenericEntity getAssessments(String token, Object id, Config.Data config) {
        GenericEntity entity = new GenericEntity();
        GenericEntity student = entityManager.getStudentWithOptionalFields(token, (String) id,
                Arrays.asList(Constants.ATTR_ASSESSMENTS));
        if (student == null) {
            log.error("Requested data for non-existing ID" + id);
            return entity;
        }
        // Map params = config.getParams();
        // if (null == params) {
        // return null;
        // }
        List<Map<String, Object>> assessements = filterAssessmentByFamily(
                student.getList(Constants.ATTR_STUDENT_ASSESSMENTS),
                (String) config.getParams().get(Constants.ATTR_ASSESSMENT_FAMILY));

        // get all assessments for student
        entity.put(Constants.ATTR_ASSESSMENTS, assessements);
        Set<String> scoreResultNames = new LinkedHashSet<String>();
        List<Map<String, Object>> scoreResults;
        Map<String, Object> assessmentDetails;
        List<List<Map<String, Object>>> perfLevelsDescs;
        String reportingMethod;
        // inline assessments, perf attributes and convert grade to gradelevel
        // TODO: we have similar logic for LOS - should be refactored and reused if possible
        for (Map<String, Object> elem : assessements) {
            scoreResults = (List<Map<String, Object>>) elem.get(Constants.ATTR_SCORE_RESULTS);
            if (scoreResults != null) {
                for (Map<String, Object> oneScore : scoreResults) {
                    reportingMethod = (String) oneScore.get(Constants.ATTR_ASSESSMENT_REPORTING_METHOD);
                    scoreResultNames.add(reportingMethod);
                    elem.put(reportingMethod, oneScore.get(Constants.ATTR_RESULT));
                }
            }
            assessmentDetails = (Map<String, Object>) elem.get(Constants.ATTR_ASSESSMENTS);
            if (assessmentDetails != null) {
                GenericEntityEnhancer.convertGradeLevel(assessmentDetails, Constants.ATTR_GRADE_LEVEL_ASSESSED);
            }

            perfLevelsDescs = (List<List<Map<String, Object>>>) elem.get(Constants.ATTR_PERFORMANCE_LEVEL_DESCRIPTOR);

            if (perfLevelsDescs != null) {
                for (List<Map<String, Object>> perfLevelsDesc : perfLevelsDescs) {
                    if (perfLevelsDesc != null && perfLevelsDesc.size() > 0) {

                        String perfLevel = (String) perfLevelsDesc.get(0).get(Constants.ATTR_CODE_VALUE);
                        elem.put(Constants.ATTR_PERF_LEVEL, perfLevel);
                    }
                }
            }
        }
        entity.put("scoreResultsSet", new ArrayList<String>(scoreResultNames));
        return entity;
    }

    /**
     * Comparator for student names
     */
    public static final Comparator<GenericEntity> STUDENT_COMPARATOR = new Comparator<GenericEntity>() {

        @Override
        public int compare(GenericEntity o1, GenericEntity o2) {
            String name1 = (String) o1.getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME);
            String name2 = (String) o2.getNode(Constants.ATTR_NAME + "." + Constants.ATTR_FULL_NAME);
            if (name1 == null && name2 == null) {
                return 0;
            }
            if (name1 == null) {
                name1 = StringUtils.EMPTY;
            }
            return name1.compareTo(name2);
        }

    };
}