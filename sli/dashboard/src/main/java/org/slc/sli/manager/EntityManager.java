package org.slc.sli.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.ContactSorter;
import org.slc.sli.entity.util.GenericEntityEnhancer;
import org.slc.sli.util.Constants;
import org.slc.sli.util.DashboardException;

/**
 * EntityManager which engages with the API client to build "logical" entity graphs to be leveraged
 * by the MVC framework.
 * Adapted from Sravan Vankina's API artifacts in org.slc.sli.client, as well as David Wu's
 * GenericEntity kickoff.
 *
 * @author Sravan Vankina
 * @author David Wu
 * @author Robert Bloh
 *
 */
@Component
public class EntityManager extends ApiClientManager {

    private static Logger log = LoggerFactory.getLogger(EntityManager.class);

    // Mock Data Files
    private static final String MOCK_DATA_DIRECTORY = "mock_data/";
    private static final String MOCK_ENROLLMENT_FILE = "school.json";
    private static final String MOCK_STUDENTS_FILE = "student.json";
    private static final String MOCK_PROGRAMS_FILE = "student_program_association.json";
    private static final String MOCK_ASSESSMENT_METADATA_FILE = "assessment_meta_data.json";
    private static final String MOCK_ASSESSMENTS_FILE = "assessment.json";
    private static final String MOCK_ATTENDANCE_FILE = "attendance.json";

    // API Session Id
    private static final String API_SESSION_KEY = "sessionId";

    public EntityManager() {

    }

    /**
     * Get the list of school entities identified by the school id list and authorized for the
     * security token
     *
     * @param token
     *            - the principle authentication token
     * @param schoolIds
     *            - the school id list
     * @return schoolList
     *         - the school entity list
     */
    public List<GenericEntity> getSchools(final String token, List<String> schoolIds) {
        return getApiClient().getSchools(token, schoolIds);
    }

    /**
     * Returns a single student entity with optional fields embedded.
     * @param token authentication token
     * @param studentId The student we are looking for
     * @param optionalFields A list of "optional views" that we are appending to the student.  These could be related
     *                       to attendance, assessments, etc..
     * @return A single student entity with selected optional views embedded.
     */
    public GenericEntity getStudentWithOptionalFields(final String token, String studentId, List<String> optionalFields) {
        return getApiClient().getStudentWithOptionalFields(token, studentId, optionalFields);
    }

    /**
     * Get the list of student entities identified by the student id list and authorized for the
     * security token
     *
     * @param token
     *            - the principle authentication token
     * @param studentIds
     *            - the student id list
     * @return studentList
     *         - the student entity list
     */
    public List<GenericEntity> getStudents(final String token, Collection<String> studentIds) {
        return getApiClient().getStudents(token, studentIds);
    }

    /**
     * Get the student entity identified by the student id and authorized for the security token
     *
     * @param token
     *            - the principle authentication token
     * @param studentId
     *            - the student id
     * @return student
     *         - the student entity
     */
    public GenericEntity getStudent(final String token, String studentId) {
        return getApiClient().getStudent(token, studentId);
    }



    /**
     * Get the student entity along with additional info needed for CSI panel
     *
     * @param token
     *            - the principle authentication token
     * @param studentId
     *            - the student id
     * @return student
     *         - the student entity
     */
    public GenericEntity getStudentForCSIPanel(final String token, String studentId) {
        GenericEntity student = getStudent(token, studentId);
        if (student == null) {
            throw new DashboardException("Unable to retrieve data for the requested ID");
        }
        student = ContactSorter.sort(student);
        student = GenericEntityEnhancer.enhanceStudent(student);
        GenericEntity section = getApiClient().getHomeRoomForStudent(studentId, token);

        if (section != null) {
            student.put(Constants.ATTR_SECTION_ID, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
            GenericEntity teacher = getApiClient().getTeacherForSection(section.getString(Constants.ATTR_ID), token);

            if (teacher != null) {
                Map teacherName = (Map) teacher.get(Constants.ATTR_NAME);
                if (teacherName != null) {
                    student.put(Constants.ATTR_TEACHER_NAME, teacherName);
                }
            }
        }

        List<GenericEntity> studentEnrollment = getApiClient().getStudentEnrollment(token, student);
        student.put(Constants.ATTR_STUDENT_ENROLLMENT, studentEnrollment);

        /*GenericEntity program = getProgram(token, studentId);
        if (program != null) {
            student.put(Constants.ATTR_PROGRAMS, program.get(Constants.ATTR_PROGRAMS));
        } else {
            student.put(Constants.ATTR_PROGRAMS, new ArrayList());
        }*/
        return student;
    }

    /**
     * Get the list of assessment entities
     *
     * @return assessment list
     */
    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds) {
        return getApiClient().getAssessments(token, assessmentIds);
    }

    /**
     * Get the list of student assessment entities identified by the student id list and authorized for the
     * security token
     *
     * @param token
     *            - the principle authentication token
     * @param studentIds
     *            - the student id list
     * @return student assessment list
     */
    public List<GenericEntity> getStudentAssessments(final String token, String studentId) {
        // TODO: the logic for filtering by student id isn't working right now, so just passing in null
        return getApiClient().getStudentAssessments(token, studentId);
    }


    /**
     * Get custom data
     * @param token
     * @param key
     * @return
     */
    public List<GenericEntity> getCustomData(String token, String key) {
        return getApiClient().getCustomData(token, key);
    }

    /**
     * Retrieves a list of attendance objects for a single student.
     * @param token The current authentication token.
     * @param studentId The studentID that you want to get your attendance objects for.
     * @return a list of attendance objects
     */
    public List<GenericEntity> getAttendance(final String token, final String studentId, final String start, final String end) {
        return getApiClient().getStudentAttendance(token, studentId, start, end);
    }

    /**
     * Returns a list of courses for a given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params param map
     * @return
     */
    public List<GenericEntity> getCourses(final String token, final String studentId, Map<String, String> params) {
        return getApiClient().getCourses(token, studentId, params);
    }

    /**
     * Returns a list of studentCourseAssociations for a given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params param map
     * @return
     */
    public List<GenericEntity> getStudentTranscriptAssociations(final String token, final String studentId, Map<String, String> params) {
        return getApiClient().getStudentTranscriptAssociations(token, studentId, params);
    }

    /**
     * Returns a list of sections for the given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params param map
     * @return
     */
    public List<GenericEntity> getSections(final String token, final String studentId, Map<String, String> params) {
        return getApiClient().getSections(token, studentId, params);
    }

    /**
     * Returns a list of student grade book entries for a given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params param map
     * @return
     */
    public List<GenericEntity> getStudentSectionGradebookEntries(final String token, final String studentId, Map<String, String> params) {
        return getApiClient().getStudentSectionGradebookEntries(token, studentId, params);
    }

    public List<GenericEntity> getStudentsWithGradebookEntries(final String token, final String sectionId) {
        return getApiClient().getStudentsWithGradebookEntries(token, sectionId);
    }

    /**
     * Returns an entity for the given type, id and params
     * @param token Security token
     * @param type Type of the entity
     * @param id The id of the entity
     * @param params param map
     * @return
     */
    public GenericEntity getEntity(final String token, final String type, final String id, Map<String, String> params) {
        return getApiClient().getEntity(token, type, id, params);
    }


    /**
     * Return a list of students for a section with the optional fields
     * @param token Security token
     * @param sectionId The sectionId
     * @param studentIds The studentIds (this is only here to get MockClient working)
     * @return
     */
    public List<GenericEntity> getStudents(String token, String sectionId, List<String> studentIds) {
        return getApiClient().getStudents(token, sectionId, studentIds);
    }

    /**
     * Returns a list of students, which match the search parameters
     * @param token
     * @param firstName
     * @param lastName
     * @return
     */
    
    public List<GenericEntity> getStudentsFromSearch(String token, String firstName, String lastName) {
        return getApiClient().getStudentsWithSearch(token, firstName, lastName);
    }
    

    /**
     * Saves an entity list to the specified file using its JSON representation
     *
     * @param filePath
     *            - the file path to persist the entity list JSON string representation
     * @param entityList
     *            - the generic entity list
     */
    public void toFile(String filePath, List<GenericEntity> entityList) {

        BufferedWriter writer = null;

        try {

            // Write JSON file
            writer = new BufferedWriter(new FileWriter(filePath));
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String json = gson.toJson(entityList, new ArrayList<Map>().getClass());
            writer.write(json);

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Retrieves an entity list from the specified file
     * and instantiates from its JSON representation
     *
     * @param filePath
     *            - the file path to persist the view component XML string representation
     * @return entityList
     *         - the generic entity list
     */
    public List<GenericEntity> fromFile(String filePath) {
        List<GenericEntity> entityList = new ArrayList<GenericEntity>();

        BufferedReader reader = null;

        try {

            // Read JSON file
            reader = new BufferedReader(new FileReader(filePath));
            StringBuffer jsonBuffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            // Parse JSON
            Gson gson = new Gson();
            List<Map> maps = gson.fromJson(jsonBuffer.toString(), new ArrayList<Map>().getClass());

            for (Map<String, Object> map : maps) {
                entityList.add(new GenericEntity(map));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return entityList;
    }

    /**
     * Gets the file path of a specified web resource.
     *
     * @return filePath
     *         possible object is {@link String }
     *
     */
    public String getResourceFilePath(String resourceName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        return url.getFile();
    }

    public GenericEntity getSession(String token, String sessionId) {
        return getApiClient().getSession(token, sessionId);
    }

    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        return getApiClient().getSessionsByYear(token, schoolYear);  //To change body of created methods use File | Settings | File Templates.
    }

    public GenericEntity getAcademicRecord(String token, Map<String, String> params) {
        return getApiClient().getAcademicRecord(token, params);
    }
}
