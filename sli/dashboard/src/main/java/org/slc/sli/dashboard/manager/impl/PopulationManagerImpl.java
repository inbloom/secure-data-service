/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dashboard.manager.impl;

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.Config.Data;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.GenericEntityComparator;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.manager.ApiClientManager;
import org.slc.sli.dashboard.manager.EntityManager;
import org.slc.sli.dashboard.manager.PopulationManager;
import org.slc.sli.dashboard.util.CacheableUserData;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.ExecutionTimeLogger.LogExecutionTime;
import org.slc.sli.dashboard.util.TimedLogic;

/**
 * Facilitates creation of logical aggregations of EdFi entities/associations
 * such as a student summary comprised of student profile, program, and
 * assessment information in order to deliver the Population Summary
 * interaction.
 * 
 * @author Robert Bloh
 * 
 */
public class PopulationManagerImpl extends ApiClientManager implements PopulationManager {

    private static final String ATTENDANCE_TARDY = "Tardy";
    private static final String ATTENDANCE_ABSENCE = "Absence";

    private static final int DEFAULT_YEARS_BACK = 3;
    private static final int NO_LIMIT = -1;

    private static final String ES_LIMIT = "limit";
    private static final String ES_SEARCH_LIMIT = "250";

    private static final Logger LOG = LoggerFactory.getLogger(PopulationManagerImpl.class);

    private static final String NICKNAME = "nickName";

    @Autowired
    private EntityManager entityManager;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getAssessments(java.
     * lang.String, java.util.List)
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
                    LOG.warn(cce.getMessage());
                }
            }
        }
        return new ArrayList<GenericEntity>(assessments);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getStudentSummaries(
     * java.lang. String, java.util.List, org.slc.sli.config.ViewConfig,
     * java.lang.String, java.lang.String)
     */
    @Override
    public List<GenericEntity> getStudentSummaries(String token, List<String> studentIds, String sessionId,
            String sectionId) {

        long startTime = System.nanoTime();
        // Initialize student summaries

        List<GenericEntity> studentSummaries = entityManager.getStudents(token, sectionId);
        LOG.warn("@@@@@@@@@@@@@@@@@@ Benchmark for student section view: {}", (System.nanoTime() - startTime) * 1.0e-9);

        return studentSummaries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getListOfStudents(java
     * .lang.String , java.lang.Object,
     * org.slc.sli.dashboard.entity.Config.Data)
     */
    @Override
    @CacheableUserData
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
                addCurrentSessionGrades(student, sectionId);

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
     * Create an attribute for the full student name (first name + middle name + last name) and
     * nickname
     * 
     * @param student
     */
    public void addFullNameForSearch(GenericEntity student) {

        StringBuilder fullName = new StringBuilder();
        Map name = (Map) student.get(Constants.ATTR_NAME);
        if (name != null) {
            fullName.append((String) name.get(Constants.ATTR_FIRST_NAME) + " ");
            String middleName = (String) name.get(Constants.ATTR_MIDDLE_NAME);
            fullName.append((middleName != null && !middleName.isEmpty() ? middleName + " " : ""));
            fullName.append((String) name.get(Constants.ATTR_LAST_SURNAME));
        }
        List otherNameList = (List) student.get(Constants.ATTR_OTHER_NAME);
        if (otherNameList != null && !otherNameList.isEmpty()) {
            Map otherName = (Map) otherNameList.get(0);
            if (otherName != null && !otherName.isEmpty()) {
                String otherNameType = (String) otherName.get(Constants.ATTR_OTHER_NAME_TYPE);
                if (Constants.ATTR_NICKNAME.equals(otherNameType)) {
                    StringBuilder nickName = new StringBuilder();
                    String middleName = (String) otherName.get(Constants.ATTR_MIDDLE_NAME);
                    String lastname = (String) otherName.get(Constants.ATTR_LAST_SURNAME);
                    nickName.append((String) otherName.get(Constants.ATTR_FIRST_NAME) + " ");
                    nickName.append((middleName != null && !middleName.isEmpty() ? middleName + " " : ""));
                    nickName.append(lastname != null ? lastname : "");
                    name.put(NICKNAME, nickName.toString());
                }
            }
        }
        name.put(Constants.ATTR_FULL_NAME, fullName.toString());
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
            // score easily accessible without looping through this list
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
     * 
     * Finds the chronologically (strictly) earlier date on the list as compared
     * to the anchorDate. Note that this method assumes the dates list has been
     * chronologically sorted, the anchorDate is contained on it, and it has no
     * "null" entries. The input parameters themselves can be null, in which
     * case null is returned.
     * 
     * @param dates
     * @param anchorDate
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Date findPrevDate(List<Date> dates, Date anchorDate) {
        if (anchorDate == null || dates == null) {
            return null;
        }
        Date prevDate = null;
        ListIterator<Date> li = dates.listIterator(dates.indexOf(anchorDate));
        while (li.hasPrevious()) {
            Date d = li.previous();
            if (d.before(anchorDate)) {
                prevDate = d;
                break;
            }
        }
        return prevDate;
    }

    /**
     * Extracts grades from transcriptAssociationRecord based on sections in the
     * past. For each section where a transcript with final letter grade exist,
     * the grade is added to the list of grades for the semester.
     * 
     * @param student
     * @param interSections
     * @param stuTransAssocs
     * @param curSessionEndDate
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addSemesterFinalGrades(GenericEntity student, List<Map<String, Object>> interSections,
            List<Map<String, Object>> stuTransAssocs, Date curSessionEndDate) {

        // populate the end dates for all the relevant sections
        List<Date> sectionDates = new LinkedList<Date>();
        sectionDates.add(curSessionEndDate);
        for (Map<String, Object> section : interSections) {
            List<Date> dates = getSessionDates(section);
            Date sectionEndDate = (dates == null) ? null : dates.get(1);
            if (sectionEndDate != null) {
                sectionDates.add(sectionEndDate);
            }
        }
        Collections.sort(sectionDates);
        // find the related sections chronologically preceding the current one
        // on the list
        Date prevDate = findPrevDate(sectionDates, curSessionEndDate);
        Date prevPrevDate = findPrevDate(sectionDates, prevDate);

        // Iterate through the course Id's and grab transcripts grades, once
        // we have NUMBER_OF_SEMESTERS transcript grades, we're done
        for (Map<String, Object> section : interSections) {

            if (section != null) {

                Map<String, Object> course = ((Map<String, Object>) section.get(Constants.ATTR_COURSES));
                if (course != null) {
                    String courseId = (String) course.get(Constants.ATTR_ID);

                    // we need to keep track of special cases, e.g. previous
                    // semester and two
                    // semesters ago
                    // data
                    List<Date> dates = getSessionDates(section);
                    Date sectionEndDate = (dates == null) ? null : dates.get(1);
                    String tag;
                    if (sectionEndDate != null) {
                        if (prevDate != null && prevDate.equals(sectionEndDate)) {
                            // this is the previous semester's section
                            tag = "previousSemester";
                        } else if (prevPrevDate != null && prevPrevDate.equals(sectionEndDate)) {
                            // this is two semesters ago
                            tag = "twoSemestersAgo";
                        } else {
                            // this is neither of the cases of interest
                            continue;
                        }
                    } else {
                        // no section end date means we cannot determine where
                        // this data belongs
                        continue;
                    }

                    // Find the correct course. If that course is found in
                    // the transcript, then record that letter grade to the
                    // semesterScores.
                    for (Map<String, Object> assoc : stuTransAssocs) {
                        if (courseId.equalsIgnoreCase((String) assoc.get(Constants.ATTR_COURSE_ID))) {
                            String finalLetterGrade = (String) assoc.get(Constants.ATTR_FINAL_LETTER_GRADE);
                            String courseTitle = (String) course.get(Constants.ATTR_COURSE_TITLE);
                            if (finalLetterGrade != null) {
                                Map<String, Object> grade = new LinkedHashMap<String, Object>();
                                grade.put(Constants.SECTION_LETTER_GRADE, finalLetterGrade);
                                grade.put(Constants.SECTION_COURSE, courseTitle);
                                List<Map<String, Object>> semesterScores = (List<Map<String, Object>>) student.get(tag);
                                if (semesterScores == null) {
                                    semesterScores = new ArrayList<Map<String, Object>>();
                                }
                                semesterScores.add(grade);
                                student.put(tag, semesterScores);
                                break;
                            }
                        }
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
             * For each student section association, we have to determine if it
             * is in the same subject area as the sectionid passed. If it is
             * then we add it to our List. Once we have a list of sections. We
             * can grab all of the semester grades for those sections whose
             * subject area intersect.
             */
            List<Map<String, Object>> stuSectAssocs = (List<Map<String, Object>>) transcripts
                    .get(Constants.ATTR_STUDENT_SECTION_ASSOC);
            if (stuSectAssocs == null) {
                return;
            }

            String subjectArea = getSubjectArea(stuSectAssocs, sectionId);

            List<Date> dates = getSessionDates(stuSectAssocs, sectionId);
            Date curSessionEndDate = (dates == null) ? null : dates.get(1);
            if (curSessionEndDate == null) {
                // if we have no current session end date, we cannot determine
                // what the previous
                // semesters were
                return;
            }

            List<Map<String, Object>> interSections = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> assoc : stuSectAssocs) {
                Map<String, Object> sections = (Map<String, Object>) assoc.get(Constants.ATTR_SECTIONS);
                // This case will catch if the subjectArea is null
                // Add the section only if it's not null
                if (sections != null && (subjectArea == null || subjectArea.equalsIgnoreCase(getSubjectArea(sections)))) {
                    interSections.add(sections);
                }
            }

            addSemesterFinalGrades(student, interSections,
                    (List<Map<String, Object>>) transcripts.get(Constants.ATTR_COURSE_TRANSCRIPTS), curSessionEndDate);

        } catch (ClassCastException ex) {
            LOG.error("Error occured processing Final Grades", ex);
            Map<String, Object> grade = new LinkedHashMap<String, Object>();
            student.put(Constants.ATTR_SCORE_RESULTS, grade.put(Constants.ATTR_FINAL_LETTER_GRADE, "?"));
        }
    }

    /**
     * Returns the term and the year as a string for a given student Section
     * association.
     * 
     * @param stuSectAssocs
     * @param sectionId
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String getSessionYear(List<Map<String, Object>> stuSectAssocs, String sectionId) {
        String sessionString = null;
        for (Map<String, Object> assoc : stuSectAssocs) {
            if (((String) assoc.get(Constants.ATTR_SECTION_ID)).equalsIgnoreCase(sectionId)) {
                Map<String, Object> sections = (Map) assoc.get(Constants.ATTR_SECTIONS);
                sessionString = buildSemesterYearString(sections);
                break;
            }
        }
        return sessionString;
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
     * Extracts the session start and end dates from the specified section.
     * 
     * @param section
     * @return session start and end dates or null if information is not
     *         available
     * @author iivanisevic
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Date> getSessionDates(Map<String, Object> section) {
        List<Date> dates;
        // get the session for this section
        Map<String, Object> session = (Map) section.get(Constants.ATTR_SESSIONS);
        if (session == null) {
            // no session for this section, bad news
            return null;
        }
        dates = new LinkedList<Date>();
        dates.add(parseDate((String) session.get(Constants.ATTR_SESSION_BEGIN_DATE)));
        dates.add(parseDate((String) session.get(Constants.ATTR_SESSION_END_DATE)));
        return dates;
    }

    /**
     * Extracts the session start and end dates from the specified section and
     * studentSectionAssociation.
     * 
     * @param stuSectAssocs
     * @param sectionId
     * @return session start and end dates or null if information is not
     *         available
     * @author iivanisevic
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Date> getSessionDates(List<Map<String, Object>> stuSectAssocs, String sectionId) {
        List<Date> dates = null;
        for (Map<String, Object> assoc : stuSectAssocs) {
            // find the association containing this sectionId
            String tempSectionId = (String) assoc.get(Constants.ATTR_SECTION_ID);
            if (tempSectionId != null && tempSectionId.equalsIgnoreCase(sectionId)) {
                // we found the section we were looking for
                Map<String, Object> section = (Map) assoc.get(Constants.ATTR_SECTIONS);
                if (section != null) {
                    dates = getSessionDates(section);
                    break;
                }
            }
        }
        return dates;
    }

    private Date parseDate(String date) {
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (date != null) {
                return formatter.parse(date);
            }
        } catch (ParseException e) {
            LOG.error("Error parsing dates. Date string was: " + date);
        }
        return null;
    }

    /**
     * Adds the current session grades to the student in a easily retrievable
     * manner.
     * 
     * @param student
     * @param sectionId
     */
    @SuppressWarnings("unchecked")
    private void addCurrentSessionGrades(GenericEntity student, String sectionId) {
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

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
            // figure out what the start and end dates are for the current
            // session
            List<Date> dates = getSessionDates(stuSectAssocs, sectionId);
            Date sessionStart = (dates == null) ? null : dates.get(0);
            Date sessionEnd = (dates == null) ? null : dates.get(1);

            // next we ensure that gradebook entries belong to the current
            // session

            // iterate and add to letter grade
            List<Map<String, Object>> gradeEntries = (List<Map<String, Object>>) student
                    .get(Constants.ATTR_STUDENT_GRADEBOOK_ENTRIES);
            if (gradeEntries == null) {
                return;
            }

            for (Map<String, Object> currentGrade : gradeEntries) {
                GenericEntity gradeDate = new GenericEntity();
                // get the grade itself
                Object grade = currentGrade.get(Constants.ATTR_LETTER_GRADE_EARNED);
                if (grade == null) {
                    grade = currentGrade.get(Constants.ATTR_NUMERIC_GRADE_EARNED);
                    if (grade == null) {
                        continue;
                    }
                }
                // when was it earned?
                try {
                    Object dateString = currentGrade.get(Constants.ATTR_DATE_FULFILLED);
                    // only keep it if it belongs to this session, or there is
                    // no date
                    // associated with it or the current session
                    if (dateString != null) {
                        Date date = formatter.parse((String) dateString);
                        if (((sessionStart == null || sessionStart.compareTo(date) <= 0) && (sessionEnd == null || sessionEnd
                                .compareTo(date) >= 0))) {
                            gradeDate.put(Constants.ATTR_DATE_FULFILLED, date);
                        } else {
                            // this grade does not belong in the current session
                            continue;
                        }
                    }
                } catch (ParseException e) {
                    LOG.error("Error parsing dates for a Current Section grade.");
                }
                gradeDate.put(Constants.ATTR_GRADE_EARNED, grade);
                sortedList.add(gradeDate);
            }
            int count = 0;
            for (GenericEntity entity : sortedList) {
                student.put("currentSession-" + count++, entity);
            }

        } catch (ClassCastException ex) {
            LOG.error("Error occured processing Gradebook Entries", ex);
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
                // config
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
                        // since we flip data to be the property name, we cannot
                        // allow dots in it
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

    private List<GenericEntity> getStudentAttendanceForSchool(String token, String studentId, String schoolId,
            String startDate, String endDate) {
        List<GenericEntity> list = entityManager.getAttendanceForSchool(token, studentId, schoolId, startDate, endDate);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#setEntityManager(org
     * .slc.sli.dashboard.manager .EntityManager)
     */
    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getStudent(java.lang
     * .String, java.lang.String)
     */
    @Override
    public GenericEntity getStudent(String token, String studentId) {
        return entityManager.getStudent(token, studentId);
    }

    @Override
    public GenericEntity getTeacher(String token, Object teacherId, Config.Data config) {
        return getApiClient().getTeacherWithSections(token, (String) teacherId);
    }

    @Override
    public GenericEntity getTeachersForSchool(String token, Object schoolId, Config.Data config) {
        List<GenericEntity> teachers = getApiClient().getTeachersForSchool(token, (String) schoolId);

        if (teachers != null) {
            for (GenericEntity teacher : teachers) {
                addFullName(teacher);
            }
        }

        GenericEntity result = new GenericEntity();

        result.put(Constants.ATTR_TEACHERS, teachers);

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getStudent(java.lang
     * .String, java.lang.Object, org.slc.sli.dashboard.entity.Config.Data)
     */
    @Override
    @LogExecutionTime
    public GenericEntity getStudent(String token, Object studentId, Config.Data config) {
        return entityManager.getStudentForCSIPanel(token, (String) studentId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getAttendance(java.lang
     * .String, java.lang.Object, org.slc.sli.dashboard.entity.Config.Data)
     */
    @Override
    @LogExecutionTime
    public GenericEntity getAttendance(String token, Object studentIdObj, Config.Data config) {
        // get yearsBack from param
        String yearsBack = config.getParams() == null ? null : (String) config.getParams().get("yearsBack");
        int intYearsBack = DEFAULT_YEARS_BACK;
        if (yearsBack != null) {
            try {
                intYearsBack = Integer.parseInt(yearsBack);
            } catch (NumberFormatException e) {
                LOG.error("params: value of yearsBack was not integer. [" + intYearsBack + "]. Using default value ["
                        + DEFAULT_YEARS_BACK + "]");
                intYearsBack = DEFAULT_YEARS_BACK;
            }
        }

        GenericEntity attendance = new GenericEntity();
        String studentId = (String) studentIdObj;
        // get Enrollment History
        List<GenericEntity> enrollments = getApiClient().getEnrollmentForStudent(token, studentId);

        // creating lookup index for enrollment, key is term (yyyy-yyyy)
        int currentSchoolYear = 0;
        Map<String, LinkedHashMap<String, Object>> enrollmentsIndex = new HashMap<String, LinkedHashMap<String, Object>>();
        for (LinkedHashMap<String, Object> enrollment : enrollments) {
            String entryDateYear = "";
            String exitWithdrawDateYear = "";
            String entryDate = (String) enrollment.get(Constants.ATTR_ENROLLMENT_ENTRY_DATE);
            String exitWithdrawDate = (String) enrollment.get(Constants.ATTR_ENROLLMENT_EXIT_WITHDRAW_DATE);

            // find a year for entryDate
            if (entryDate != null && entryDate.length() > 3) {
                entryDateYear = entryDate.substring(0, 4);
            }

            // find a year for exitWithdarwDate
            if (exitWithdrawDate != null && exitWithdrawDate.length() > 3) {
                exitWithdrawDateYear = exitWithdrawDate.substring(0, 4);
            } else {
                // exitWithdrawDate is null because it is in current term.
                // add one year to entryDateYear.
                currentSchoolYear = Integer.parseInt(entryDateYear);
                exitWithdrawDateYear = Integer.toString(currentSchoolYear + 1);
            }

            // creating index lookup key
            String key = entryDateYear + "-" + exitWithdrawDateYear;

            enrollmentsIndex.put(key, enrollment);
        }

        // Numberformat for %Present - no fraction
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);

        // get attendance for the student
        List<GenericEntity> attendanceList = this.getStudentAttendance(token, studentId, null, null);
        for (Map<String, Object> targetAttendance : attendanceList) {

            // get schoolYearAttendance
            List<Map<String, Object>> schoolYearAttendances = (List<Map<String, Object>>) targetAttendance
                    .get(Constants.ATTR_ATTENDANCE_SCHOOLYEAR_ATTENDANCE);
            if (schoolYearAttendances != null) {

                // sort by schoolYear
                GenericEntityComparator comparator = new GenericEntityComparator(Constants.ATTR_SCHOOL_YEAR,
                        String.class);
                Collections.sort(schoolYearAttendances, Collections.reverseOrder(comparator));

                for (Map<String, Object> schoolYearAttendance : schoolYearAttendances) {
                    int inAttendanceCount = 0;
                    int absenceCount = 0;
                    int excusedAbsenceCount = 0;
                    int unexcusedAbsenceCount = 0;
                    int tardyCount = 0;
                    int earlyDepartureCount = 0;
                    int totalCount = 0;

                    // get schoolYear
                    String schoolYear = (String) schoolYearAttendance.get(Constants.ATTR_SCHOOL_YEAR);

                    // if some reasons we cannot find currentSchoolYear, then
                    // display all histories
                    // if intYearsBack is not set to NO_LIMIT (-1) and found
                    // currentSchoolYear,
                    // then exam whether current loop is within user defined
                    // yearsBack
                    if (intYearsBack != NO_LIMIT && currentSchoolYear != 0) {
                        int targetYear = Integer.parseInt(schoolYear.substring(0, 4));
                        // if yearsBack is 1, it means current schoolYear.
                        // break from the loop if currentSchoolYear-targetYear
                        // is over yearsBack.
                        if ((currentSchoolYear - targetYear) >= intYearsBack) {
                            break;
                        }
                    }

                    // get target school year enrollment
                    LinkedHashMap<String, Object> enrollment = enrollmentsIndex.get(schoolYear);
                    GenericEntity currentTermAttendance = new GenericEntity();

                    // set school term
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_TERM, schoolYear);

                    String nameOfInstitution = "";
                    boolean enrollmentExistsForAttendance = false;

                    // get school name from enrollment
                    if (enrollment != null) {
                        Map<String, Object> school = (Map<String, Object>) enrollment.get(Constants.ATTR_SCHOOL);
                        if (school != null) {
                            nameOfInstitution = (String) school.get(Constants.ATTR_NAME_OF_INST);
                        }
                        String enrollmentSchoolId = (String) enrollment.get(Constants.ATTR_SCHOOL_ID);
                        String attendanceSchoolId = (String) targetAttendance.get(Constants.ATTR_SCHOOL_ID);
                        if (enrollmentSchoolId.equals(attendanceSchoolId) == true) {
                            // only show attendances that are for the school in the
                            // target enrollment
                            enrollmentExistsForAttendance = true;
                        }

                    }

                    // get attendanceEvent
                    List<LinkedHashMap<String, Object>> attendanceEvents = (List<LinkedHashMap<String, Object>>) schoolYearAttendance
                            .get(Constants.ATTR_ATTENDANCE_ATTENDANCE_EVENT);

                    // count each attendance event
                    if (attendanceEvents != null && enrollmentExistsForAttendance) {
                        for (Map<String, Object> attendanceEvent : attendanceEvents) {
                            String event = (String) attendanceEvent.get(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY);
                            if (event != null) {
                                totalCount++;
                                if (event.equals(Constants.ATTR_ATTENDANCE_IN_ATTENDANCE)) {
                                    inAttendanceCount++;
                                } else if (event.equals(Constants.ATTR_ATTENDANCE_ABSENCE)) {
                                    absenceCount++;
                                } else if (event.equals(Constants.ATTR_ATTENDANCE_EXCUSED_ABSENCE)) {
                                    excusedAbsenceCount++;
                                } else if (event.equals(Constants.ATTR_ATTENDANCE_UNEXCUSED_ABSENCE)) {
                                    unexcusedAbsenceCount++;
                                } else if (event.equals(Constants.ATTR_ATTENDANCE_TARDY)) {
                                    tardyCount++;
                                } else if (event.equals(Constants.ATTR_ATTENDANCE_EARLY_DEPARTURE)) {
                                    earlyDepartureCount++;
                                }
                            }
                        }
                    }

                    // set school name
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_SCHOOL, nameOfInstitution);

                    String gradeLevel = "";
                    // set grade level
                    if (enrollment != null) {
                        gradeLevel = (String) enrollment.get(Constants.ATTR_ENROLLMENT_ENTRY_GRADE_LEVEL_CODE);
                    }
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_GRADE_LEVEL, gradeLevel);

                    // set %Present
                    currentTermAttendance
                            .put(Constants.ATTENDANCE_HISTORY_PRESENT,
                                    numberFormat
                                            .format(totalCount == 0 ? 0
                                                    : ((inAttendanceCount + tardyCount + earlyDepartureCount) / (double) totalCount) * 100));
                    // set In Attendance
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_IN_ATTENDANCE, inAttendanceCount);

                    // set Total Absences
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_TOTAL_ABSENCES, absenceCount
                            + excusedAbsenceCount + unexcusedAbsenceCount);

                    // set Absence
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_ABSENCE, absenceCount);

                    // set Excused Absences
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_EXCUSED, excusedAbsenceCount);

                    // set Unexcused Absences
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_UNEXCUSED, unexcusedAbsenceCount);

                    // set Tardy
                    currentTermAttendance.put(Constants.ATTENDANCE_HISTORY_TARDY, tardyCount);

                    // set Early departure
                    currentTermAttendance.put(Constants.ATTENDANCE_EARLY_DEPARTURE, earlyDepartureCount);

                    // Add to attendance list
                    attendance.appendToList("attendance", currentTermAttendance);
                }
            }
        }
        return attendance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.dashboard.manager.PopulationManagerI#getSessionDates(java
     * .lang.String, java.lang.String)
     */
    @Override
    public List<String> getSessionDates(String token, String sessionId) {
        // This method appears to not return the begin/end dates for the input
        // session, as the name
        // implies, but rather the begin/end dates, respectively, for the first
        // and last session of
        // the school year associated with the input session id.

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
    public GenericEntity getStudentsBySearch(String token, Object query, Config.Data config) {

        GenericEntity studentSearch = new GenericEntity(); // object to return

        // refactor to use API Request class instead of an array of String.
        String[] queryArray;
        try {
            queryArray = (String[]) query;
        } catch (ClassCastException cce) {
            setStudentSearchEntity(studentSearch, new LinkedList<GenericEntity>(), "", 0, 1, 50, 1);
            return studentSearch;
        }

        if (queryArray != null && queryArray.length > 0) {
            String name = queryArray[0];
            if (name == null || name.isEmpty()) {
                setStudentSearchEntity(studentSearch, new LinkedList<GenericEntity>(), "", 0, 1, 50, 1);
                return studentSearch;
            }
            // get results from api
            Map<String, String> params = new HashMap<String, String>();

            // hard limit to 250
            params.put(ES_LIMIT, ES_SEARCH_LIMIT);
            List<GenericEntity> students = getApiClient().searchStudents(token, name.replaceAll(" ", "%20"), params);

            // optionally (but typically), it should also contain pagination
            // information
            int pageNum = 1, pageSize = 50;
            if (queryArray.length >= 3) {
                try {
                    pageNum = Integer.parseInt(queryArray[1]);
                    pageSize = Integer.parseInt(queryArray[2]);
                } catch (NumberFormatException nfe) {
                    // pagination information was in an incorrect format, use
                    // default values.
                    pageNum = 1;
                    pageSize = 50;
                }
            }

            // This is a temporary solution until we decide how to integrate the
            // search with the API
            // pagination calls. Currently, when API is used, the total number of
            // search results is
            // stored in the header which is not accessible. Also, code above
            // performs two searches and
            // combines results - this is a problem if API pagination is used.
            int numResults = students.size();
            // verify sensible page number was requested (negatives not allowed)
            if (pageNum < 1) {
                pageNum = 1;
            }
            // verify sensible page size was specified (negatives not allowed)
            if (pageSize < 1) {
                pageSize = 1;
            }
            // calculate the last available page from the number of results and page
            // size
            int maxPageNum = numResults / pageSize;
            if (numResults % pageSize != 0) {
                maxPageNum++;
            }
            // requested page number cannot exceed last available page
            if (pageNum > maxPageNum) {
                pageNum = maxPageNum;
            }
            // fetch the subset of search results specified by the pagination
            // request
            if (numResults > pageSize) {
                int beginIndex = (pageNum - 1) * pageSize;
                int endIndex = beginIndex + pageSize;
                if (endIndex > numResults) {
                    endIndex = numResults;
                }
                students = students.subList(beginIndex, endIndex);
            }

            // post-process
            // get detail information for each student
            List<GenericEntity> enhancedStudents = new LinkedList<GenericEntity>();
            HashMap<String, GenericEntity> retrievedSchools = new HashMap<String, GenericEntity>();

            // get students
            List<String> studentIdsList = new LinkedList<String>();
            for (GenericEntity student : students) {
                studentIdsList.add(student.getId());
            }
            Map<String, String> studentParams = new HashMap<String, String>();
            studentParams.put(Constants.LIMIT, "0");
            studentParams.put(Constants.ATTR_OPTIONAL_FIELDS, Constants.ATTR_GRADE_LEVEL);
            List<GenericEntity> retrievedStudents = entityManager.getApiClient().getStudents(token, studentIdsList,
                    studentParams);

            //iterate retrieved student records
            //add full name (first, middle, and last with other name(nick name) if it is available)
            //add school name
            GenericEntity school;
            for (GenericEntity student : retrievedStudents) {
                addFullNameForSearch(student);

                String schoolId = student.getString(Constants.ATTR_SCHOOL_ID);
                if (schoolId != null && !schoolId.isEmpty()) {
                    if (retrievedSchools.containsKey(schoolId)) {
                        school = retrievedSchools.get(schoolId);
                        student.put("currentSchoolName", school.get(Constants.ATTR_NAME_OF_INST));
                    } else {
                        school = entityManager.getEntity(token, Constants.ATTR_SCHOOLS, schoolId, null);
                        retrievedSchools.put(school.getString(Constants.ATTR_ID), school);
                        student.put("currentSchoolName", school.get(Constants.ATTR_NAME_OF_INST));
                    }
                }

                GenericEntityEnhancer.enhanceStudent(student);
                enhancedStudents.add(student);
            }
            setStudentSearchEntity(studentSearch, enhancedStudents, name, numResults, pageNum, pageSize, maxPageNum);
        } else {
            setStudentSearchEntity(studentSearch, new LinkedList<GenericEntity>(), "", 0, 1, 50, 1);
        }
        return studentSearch;
    }

    private void setStudentSearchEntity(GenericEntity studentSearch, List<GenericEntity> students, String searchStr,
            int numResults, int pageNum, int pageSize, int maxPageNum) {
        studentSearch.put(Constants.ATTR_STUDENTS, students);
        studentSearch.put(Constants.ATTR_SEARCH_STRING, searchStr);
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
            LOG.error("Requested data for non-existing ID" + id);
            return entity;
        }
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

    /**
     * Retrieves info required to create section profile.
     */
    @Override
    public GenericEntity getSectionForProfile(String token, Object sectionId, Config.Data config) {
        return entityManager.getSectionForProfile(token, (String) sectionId, null, null);
    }

    @Override
    public GenericEntity getCoursesAndGrades(String token, Object studentId, Data config) {
        return entityManager.getCurrentCoursesAndGrades(token, (String) studentId);
    }

    /**
     * Retrieves attendance in a sorted order, removes all events where the
     * student is present. Returns a GenericEntity with startDate, endDate, and
     * attendanceList.
     */
    @Override
    public GenericEntity getStudentAttendanceForCalendar(String token, Object studentId, Data config) {

        String schoolId = null;
        GenericEntity ge = new GenericEntity();

        List<GenericEntity> enrollments = getApiClient().getEnrollmentForStudent(token, (String) studentId);
        if (enrollments == null || enrollments.size() < 1) {
            return ge;
        }
        GenericEntity firstEnrollment = enrollments.get(0);
        Map<String, Object> school = (Map<String, Object>) firstEnrollment.get(Constants.ATTR_SCHOOL);
        if (school != null) {
            schoolId = (String) school.get(Constants.ATTR_ID);
        } else {
            return ge;
        }

        List<String> currentYearDates = null;

        try {
            // get begin/end dates for the current school year
            currentYearDates = getCurrentYearDates(token, schoolId);
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
        }

        LinkedList<Map> absentList = new LinkedList<Map>();
        ge.put(Constants.ATTR_ATTENDANCE_LIST, absentList);
        ge.put(Constants.ATTR_START_DATE, currentYearDates.get(0));
        ge.put(Constants.ATTR_END_DATE, currentYearDates.get(1));

        List<GenericEntity> attendanceList = getStudentAttendanceForSchool(token, (String) studentId, schoolId, null,
                null);
        if (attendanceList == null || attendanceList.size() < 1) {
            return ge;
        }

        GenericEntity firstWrapper = attendanceList.get(0);
        List<Map<String, Object>> schoolYearAttendance = (List<Map<String, Object>>) firstWrapper
                .get(Constants.ATTR_ATTENDANCE_SCHOOLYEAR_ATTENDANCE);
        if (schoolYearAttendance == null || schoolYearAttendance.size() < 1) {
            return ge;
        }

        // Comparator, sort by "schoolYear" descending order
        Comparator<Map<String, Object>> schoolYearAttendanceComparator = new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                Object schoolYearObj0 = arg0.get(Constants.ATTR_SCHOOL_YEAR);
                Object schoolYearObj1 = arg1.get(Constants.ATTR_SCHOOL_YEAR);
                if (schoolYearObj0 == null || schoolYearObj1 == null) {
                    return 0;
                }
                String schoolYear0 = schoolYearObj0.toString();
                String schoolYear1 = schoolYearObj1.toString();
                return schoolYear1.compareTo(schoolYear0);
            }
        };
        Collections.sort(schoolYearAttendance, schoolYearAttendanceComparator);

        Map<String, Object> secondWrapper = schoolYearAttendance.get(0);
        List<Map> attList = (List<Map>) secondWrapper.get(Constants.ATTR_ATTENDANCE_ATTENDANCE_EVENT);
        if (attList == null) {
            return ge;
        }

        // filter out 'In Attendance' events, remove whitespace
        // LinkedList<Map> absentList = new LinkedList<Map>();
        for (Map attEvent : attList) {
            String event = (String) attEvent.get(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY);
            if (!event.equals(Constants.ATTR_ATTENDANCE_IN_ATTENDANCE)) {
                String strippedWhiteSpaceEvent = ((String) attEvent.get(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY))
                        .replace(" ", "");
                attEvent.put(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY, strippedWhiteSpaceEvent);
                absentList.addLast(attEvent);
            }
        }

        return ge;
    }

    /**
     * Returns the begin and end dates of the current school year. The current
     * school year is determined by comparing the current date to the begin/end
     * dates for sessions the user can see. If the current date is not in any
     * session, it returns the most recent session.
     * 
     * @param token
     * @return
     * @throws ParseException
     */
    private List<String> getCurrentYearDates(String token, String schoolId) throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<GenericEntity> sessions = getApiClient().getSessions(token, schoolId, null);

        // sort sessions latest to earliest
        Comparator<Map> c = new Comparator<Map>() {

            @Override
            public int compare(Map arg0, Map arg1) {
                Map<String, String> map1, map2;

                map1 = arg0;
                map2 = arg1;

                Date date1, date2;
                try {
                    date1 = sdf.parse(map1.get(Constants.ATTR_BEGIN_DATE));
                    date2 = sdf.parse(map2.get(Constants.ATTR_BEGIN_DATE));
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    return 0;
                }
            }
        };

        Collections.sort(sessions, c);

        // find the current year
        String schoolYear = null;
        for (GenericEntity session : sessions) {
            Date currentDate = new Date();
            Date sessionStart = sdf.parse(session.getString(Constants.ATTR_BEGIN_DATE));
            Date sessionEnd = sdf.parse(session.getString(Constants.ATTR_END_DATE));
            if (sessionStart != null && sessionStart.before(currentDate) && sessionEnd != null
                    && sessionEnd.after(currentDate)) {
                schoolYear = session.getString(Constants.ATTR_SCHOOL_YEAR);
                break;
            } else if (sessionEnd.before(currentDate)) {
                schoolYear = session.getString(Constants.ATTR_SCHOOL_YEAR);
                break;
            }
        }

        Date startDate = null;
        Date endDate = null;

        // find all sessions for current school year, find earliest start date,
        // latest end date
        for (GenericEntity session : sessions) {
            if (session.getString(Constants.ATTR_SCHOOL_YEAR).equals(schoolYear)) {

                Date sessionStart = sdf.parse(session.getString(Constants.ATTR_BEGIN_DATE));
                Date sessionEnd = sdf.parse(session.getString(Constants.ATTR_END_DATE));
                if (startDate == null || sessionStart.before(startDate)) {
                    startDate = sessionStart;
                }
                if (endDate == null || sessionEnd.after(endDate)) {
                    endDate = sessionEnd;
                }
            }
        }

        List<String> ret = new ArrayList<String>();

        ret.add(sdf.format(startDate == null ? DateTime.now().toDate() : startDate));
        ret.add(sdf.format(endDate == null ? DateTime.now().toDate() : endDate));
        return ret;
    }

    @Override
    public GenericEntity getEdorgProfile(String token, Object edorgId, Config.Data config) {
        return entityManager.getEdorgProfile(token, (String) edorgId);
    }

    @Override
    public GenericEntity getStateEdorgProfile(String token, Object edorgId, Config.Data config) {
        return entityManager.getStateEdorgProfile(token, (String) edorgId);
    }
}
