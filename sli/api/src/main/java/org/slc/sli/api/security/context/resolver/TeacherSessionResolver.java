package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Resolves which teachers a given teacher is allowed to see
 *
 * @author dkornishev
 *
 */
@Component
public class TeacherSessionResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private Repository<Entity> repository;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.SESSION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> teacherSectionIds = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS));

        List<String> studentSectionIds = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS,
                ResourceNames.STUDENT_SECTION_ASSOCIATIONS,
                ResourceNames.STUDENT_SECTION_ASSOCIATIONS));
        
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.addAll(teacherSectionIds);
        sectionIds.addAll(studentSectionIds);
        
        List<String> ids = new ArrayList<String>();
        for (String id : sectionIds) {
            ids.add(id);
        }

        Set<String> sessionIds = new HashSet<String>();

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
        
        Iterable<Entity> entities = repository.findAll(EntityNames.SECTION, neutralQuery);
        
        for (Entity e : entities) {
            String sessionId = (String) e.getBody().get("sessionId");
            if (sessionId != null) {
                sessionIds.add(sessionId);
            }
        }

        return new ArrayList<String>(sessionIds);
    }

}
