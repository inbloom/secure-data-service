package org.slc.sli.manager;

import org.slc.sli.entity.Course;
import org.slc.sli.entity.School;
import org.slc.sli.entity.Section;
import org.slc.sli.util.SecurityUtil;

/**
 * Retrieves and applies necessary business logic to school data
 * 
 * @author dwu
 *
 */
public class SchoolManager extends Manager {

    public School[] getSchools() {
        String token = SecurityUtil.getToken();
        String teacherId = apiClient.getTeacherId(token);
        Section[] sections = apiClient.getSectionsForTeacher(teacherId, token);
        Course[] courses = apiClient.getCoursesForSections(sections, token);
        School[] schools = apiClient.getSchoolsForCourses(courses, token);
        
        return schools;
    }
    
}


