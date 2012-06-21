package org.slc.sli.manager;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;

/**
 * Gathers and provides information needed for the student progress view
 * @author jstokes
 */
@Manager.EntityMappingManager
public interface StudentProgressManager extends Manager {

    @EntityMapping("transcriptHistory")
    GenericEntity getTranscript(String token, Object studentIdObj, Config.Data config);

    @SuppressWarnings("unchecked")
    Map<String, List<GenericEntity>> getStudentHistoricalAssessments(String token, List<String> studentIds,
                                                                     String selectedCourse, String selectedSection);

    SortedSet<String> getSchoolYears(String token, Map<String,
            List<GenericEntity>> historicalData);

    Map<String, Map<String, GenericEntity>> getCurrentProgressForStudents(String token, List<String> studentIds,
                                                                          String selectedSection);

    SortedSet<GenericEntity> retrieveSortedGradebookEntryList(Map<String, Map<String, GenericEntity>> gradebookEntryData);
}
