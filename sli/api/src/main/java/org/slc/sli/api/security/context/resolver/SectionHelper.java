package org.slc.sli.api.security.context.resolver;

import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Contains helper methods to traverse to the sections of a given user
 *
 */
@Component
public class SectionHelper {

    @Autowired
    private TeacherStudentResolver teacherStudentResolver;
    /**
     * Traverse the edorg hierarchy and find all the SEAs the user is associated with, directly or indirectly.
     *
     * @param teacher
     * @return a list of sections for the teacher
     */
    public List<String> getTeachersSections(Entity teacher) {
        return teacherStudentResolver.getTeachersSectionIds(teacher);
    }


}
