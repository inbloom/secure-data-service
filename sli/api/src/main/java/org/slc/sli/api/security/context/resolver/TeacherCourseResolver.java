package org.slc.sli.api.security.context.resolver;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Resolves which teachers a given teacher is allowed to see
 * 
 * @author syau
 * 
 */
@Component
public class TeacherCourseResolver implements EntityContextResolver {
    
    @Autowired
    private EntityRepository repository;
    @Autowired
    private AssociativeContextHelper helper;
    
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COURSE.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> sectionIds = helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SECTION_ASSOCIATIONS));
        Set<String> courseIds = new HashSet<String>();
        for (String sectionId : sectionIds) {
            Entity section = this.repository.find(EntityNames.SECTION, sectionId);
            Object courseIdObj = section.getBody().get("courseId");
            if (courseIdObj != null) {
                courseIds.add(courseIdObj.toString());
            }
        }
        return new ArrayList<String>(courseIds);
    }
}
