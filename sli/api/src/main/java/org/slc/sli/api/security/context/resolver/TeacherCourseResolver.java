package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityQuery;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resolves which teachers a given teacher is allowed to see
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherCourseResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private EntityRepository repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COURSE.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> ids = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS,
                ResourceNames.STUDENT_SECTION_ASSOCIATIONS,
                ResourceNames.STUDENT_SECTION_ASSOCIATIONS));

        StringBuilder query = new StringBuilder();
        String separator = "";
        
        for (String s : ids) {
            query.append(separator);
            separator = ",";            
            query.append(s);
        }
        
        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder();
        queryBuilder.addField("_id", query.toString());
        
        Iterable<Entity> entities = repository.findAll(EntityNames.SECTION, queryBuilder.build());

        List<String> courseIds = new ArrayList<String>();
        
        for (Entity e : entities) {
            courseIds.add((String) e.getBody().get("courseId"));
        }

        return courseIds;
    }

}
