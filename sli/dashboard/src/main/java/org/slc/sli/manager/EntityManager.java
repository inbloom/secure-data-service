package org.slc.sli.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.client.SDKConstants;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.ContactSorter;
import org.slc.sli.entity.util.GenericEntityEnhancer;
import org.slc.sli.entity.util.ParentsSorter;
import org.slc.sli.util.Constants;
import org.slc.sli.util.DashboardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * EntityManager which engages with the API client to build "logical" entity graphs to be leveraged
 * by the MVC framework.
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
        GenericEntity section = getApiClient().getSectionHomeForStudent(token, studentId);

        if (section != null) {
            student.put(Constants.ATTR_SECTION_ID, section.get(Constants.ATTR_UNIQUE_SECTION_CODE));
            GenericEntity teacher = getApiClient().getTeacherForSection(token, section.getString(Constants.ATTR_ID));

            if (teacher != null) {
                Map teacherName = (Map) teacher.get(Constants.ATTR_NAME);
                if (teacherName != null) {
                    student.put(Constants.ATTR_TEACHER_NAME, teacherName);
                }
            }
        }

        List<GenericEntity> studentEnrollment = getApiClient().getEnrollmentForStudent(token, student.getId());
        student.put(Constants.ATTR_STUDENT_ENROLLMENT, studentEnrollment);
        
        List<GenericEntity> parents = getApiClient().getParentsForStudent(token, studentId, null);
        
        //sort parent Contact if there are more than 2.
        if (parents != null && parents.size() > 1)
            ParentsSorter.sort(parents);
        for (GenericEntity parentsContact : parents)
            ContactSorter.sort(parentsContact);
        student.put(Constants.ATTR_PARENTS, parents);
        
        return student;
    }

    /**
     * Retrieves a list of attendance objects for a single student.
     * @param token The current authentication token.
     * @param studentId The studentID that you want to get your attendance objects for.
     * @return a list of attendance objects
     */
    public List<GenericEntity> getAttendance(final String token, final String studentId, final String start, final String end) {
        Map params = new HashMap();
        if (start != null && start.length() > 0) {
            params.put(SDKConstants.PARAM_EVENT_DATE + ">", "" + start);
            params.put(SDKConstants.PARAM_EVENT_DATE + "<", "" + end);
        }
        return getApiClient().getAttendanceForStudent(token, studentId, params);
    }

    /**
     * Returns a list of courses for a given student and params
     * @param token Security token
     * @param studentId The student Id
     * @param params param map
     * @return
     */
    public List<GenericEntity> getCourses(final String token, final String studentId, Map<String, String> params) {
        return getApiClient().getCoursesForStudent(token, studentId, params);
    }

    public List<GenericEntity> getStudentsWithGradebookEntries(final String token, final String sectionId) {
        return getApiClient().getStudentsForSectionWithGradebookEntries(token, sectionId);
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
        return getApiClient().getStudentsForSection(token, sectionId, studentIds);
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

    public GenericEntity getSession(String token, String sessionId) {
        return getApiClient().getSession(token, sessionId);
    }

    public List<GenericEntity> getSessionsByYear(String token, String schoolYear) {
        return getApiClient().getSessionsForYear(token, schoolYear);  //To change body of created methods use File | Settings | File Templates.
    }

    public GenericEntity getAcademicRecord(String token, String studentId, Map<String, String> params) {
        List<GenericEntity> entities = getApiClient().getAcademicRecordsForStudent(token, studentId, params);

        if (entities == null || entities.size() <= 0) {
            return null;
        } 

        return entities.get(0);
    }
}
