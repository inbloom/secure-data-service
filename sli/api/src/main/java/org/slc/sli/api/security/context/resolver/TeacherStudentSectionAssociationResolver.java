package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeacherStudentSectionAssociationResolver implements EntityContextResolver {
    @Autowired
    private TeacherStudentResolver studentResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType)
                && EntityNames.STUDENT_SECTION_ASSOCIATION.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT));
        }
        Iterable<String> ssa = repo.findAllIds(EntityNames.STUDENT_SECTION_ASSOCIATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, studentIds)));
        List<String> ids = new ArrayList<String>();
        for (String id : ssa) {
            ids.add(id);
        }
        securityCache.warm(EntityNames.STUDENT_SECTION_ASSOCIATION, new HashSet<String>(ids));
        return ids;
    }
}
