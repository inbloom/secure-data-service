package org.slc.sli.view;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * A utility class for views in SLI dashboard. Wrapper around historical data for a list of students
 *
 * @author jstokes
 *
 */
public class HistoricalDataResolver {
    private Map<String, List<GenericEntity>> historicalData;
    private SortedSet<String> schoolYears;
    private String subjectArea;

    private static final String GRADE_KEY = "finalLetterGradeEarned";
    private static final String COURSE_TITLE_KEY = "courseTitle";
    private static final String COURSE_ID = "courseId";


    /**
     * Constructor
     * @param historicalData List of historical information - each contains
     *                       subject area, course title, course grade, session
     * @param schoolYears List of school years, sorted by most recent
     * @param subjectArea The subject area for which this historical data is for
     */
    public HistoricalDataResolver(Map<String, List<GenericEntity>> historicalData, SortedSet<String> schoolYears,
                                  String subjectArea) {
        this.schoolYears = schoolYears;
        this.historicalData = historicalData;
        this.subjectArea = subjectArea;
    }

    /**
     * Get the subject area for a given set of historical data
     * @return the subject area for the given resolver
     */
    public String getSubjectArea() {
        return subjectArea;
    }

    /**
     * Get the set of school years the historical data applies to
     * @return set of school years
     */
    public SortedSet<String> getSchoolYears() {
        return schoolYears;
    }

    /**
     * For a given field in a table, return the school year the data should be for
     * @param field The field in the table
     * @return
     */
    private String getSchoolYear(Field field) {
        return field.getTimeSlot();
    }

    /**
     * Gets the course name representation based on the field location and the student
     * @param field The field location in the table
     * @param student A map of student information
     * @return string representation of the course name
     */
    public String getCourse(Field field, Map student) {
        String studentId = student.get("id").toString();
        String schoolYear = getSchoolYear(field);

        if (schoolYear == null || studentId == null) return "-";
        
        return getFromHistoricalData(studentId, schoolYear, COURSE_TITLE_KEY);
    }

    /**
     * Gets the grade representation based on the field location and the student
     * @param field The field location in the table
     * @param student A map of student information
     * @return string representation of the grade
     */
    public String getGrade(Field field, Map student) {
        String studentId = student.get("id").toString();
        String schoolYear = getSchoolYear(field);
        if (schoolYear == null || studentId == null) return "-";

        return getFromHistoricalData(studentId, schoolYear, GRADE_KEY);
    }

    /**
     * Helper method for getGrade and getCourse - returns from the historical information for a given
     * key (course, grade, etc) based on a student id and school year
     * @param studentId The id of the student we are looking for information on
     * @param schoolYear The school year we want information from
     * @param key The type information we want to grab
     * @return string representation of the information we are looking for
     */
    private String getFromHistoricalData(String studentId, String schoolYear, String key) {
        List<GenericEntity> historicalList = historicalData.get(studentId);
        List<String> items = new ArrayList<String>();

        if (historicalList == null) return "-";
        
        for (GenericEntity data : historicalList) {
            String dataSession = data.getString("gradeLevelWhenTaken");
            if (dataSession != null && dataSession.equals(schoolYear)) {
                String item = data.get(key).toString();
                items.add(item);
            }
        }

        if (items.size() == 0) return "-";
        //else if (items.size() > 1) return "...";
        else return items.get(0);
    }

}
