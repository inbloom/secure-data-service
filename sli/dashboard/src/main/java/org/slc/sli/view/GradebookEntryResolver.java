package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;

/**
 * A utility class for views in SLI dashboard. Wrapper around gradebook entry data for a list of students
 * @author jstokes
 */
public class GradebookEntryResolver {
    private Logger log = LoggerFactory.getLogger(GradebookEntryResolver.class);

    private Map<String, Map<String, GenericEntity>> gradebookData;
    private SortedSet<GenericEntity> gradebookIds;

    private static final String GRADE_KEY = "numericGradeEarned";
    private static final String AVERAGE_KEY = "Average";

    /**
     * Constructor for GradebookEntryResolver
     * @param gradebookData the gradebook data in raw form
     */
    public GradebookEntryResolver(Map<String, Map<String, GenericEntity>> gradebookData) {
        this.gradebookData = gradebookData;
    }

    /**
     * Gets the average for a given student.
     * @param student The student to look for
     * @return string representation of the student's average
     */
    public String getAverage(Map student) {
        return getAverage(student.get("id").toString());
    }

    /**
     * Gets a grade for a given student in a given gradebookEntry
     * @param student The student to look for
     * @param gradebookEntryId The gradebookEntryId at which the test was taken
     * @return grade (string)
     */
    public String getGrade(String gradebookEntryId, Map student) {
        return getGrade(student.get("id").toString(), gradebookEntryId);
    }

    private String getAverage(String studentId) {
        try {
            Map<String, GenericEntity> studentRecord = gradebookData.get(studentId);
            GenericEntity average = studentRecord.get(AVERAGE_KEY);
            return average.get(GRADE_KEY).toString();
        } catch (NullPointerException npe) {
            return "-";
        }
    }

    /**
     * Gets a grade for a given student in a given gradebookEntry
     * @param studentId The id of the student
     * @param gradebookEntryId The gradebook entry we are looking for
     * @return grade (string)
     */
    private String getGrade(String studentId, String gradebookEntryId) {
        return getValue(studentId, gradebookEntryId, GRADE_KEY);
    }

    /**
     * Helper method for retrieving a given value for a student and gradebookEntry
     * @param studentId The id of the student
     * @param gradebookEntryId The id of the gradebook entry
     * @param key The key we are searching for in the data
     * @return the value as a string
     */
    private String getValue(String studentId, String gradebookEntryId, String key) {
        Map<String, GenericEntity> studentRecord = gradebookData.get(studentId);
        String value;
        try {
            GenericEntity gradebookInfo = studentRecord.get(gradebookEntryId);
            value = gradebookInfo.get(key).toString();
        } catch (NullPointerException npe) {
            log.debug("gradebookData did not contain information being searched for: ");
            log.debug("\t studentId = {}, gradebookEntryId = {}, key = {}",
                    new Object[]{studentId, gradebookEntryId, key});
            value = "-";
        }
        return value;
    }

    public SortedSet<GenericEntity> getGradebookIds() {
        return gradebookIds;
    }

    public void setGradebookIds(SortedSet<GenericEntity> gradebookIds) {
        this.gradebookIds = gradebookIds;
    }
}
