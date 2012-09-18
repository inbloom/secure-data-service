package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;



/**
 * Resolves which teachers a given teacher is allowed to see
 *
 * @author dkornishev
 *
 */
@Component
public class TeacherCourseResolver implements EntityContextResolver {

    @Autowired
    private TeacherCourseOfferingResolver coResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COURSE.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> courseOfferingIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.COURSE_OFFERING)) {
            courseOfferingIds = coResolver.findAccessible(principal);
        } else {
            courseOfferingIds = new ArrayList<String>(securityCache.retrieve(EntityNames.COURSE_OFFERING));
        }
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", "in", courseOfferingIds));

        Iterable<Entity> entities = repository.findAll(EntityNames.COURSE_OFFERING, neutralQuery);
        Set<String> courseIds = new HashSet<String>();

        for (Entity e : entities) {
            String courseId = (String) e.getBody().get("courseId");
            if (courseId != null) {
                courseIds.add(courseId);
            }
        }
        securityCache.warm(EntityNames.COURSE, courseIds);
        return new ArrayList<String>(courseIds);
    }

}
