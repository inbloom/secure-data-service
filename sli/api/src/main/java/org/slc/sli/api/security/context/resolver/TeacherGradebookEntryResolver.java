package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TeacherGradebookEntryResolver implements EntityContextResolver {
    @Autowired
    private TeacherSectionResolver sectionResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.GRADEBOOK_ENTRY.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> sectionIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.SECTION)) {
            sectionIds = sectionResolver.findAccessible(principal);
        } else {
            sectionIds = new ArrayList<String>(securityCache.retrieve(EntityNames.SECTION));
        }
        Iterable<String> gradebookIds = repo.findAllIds(EntityNames.GRADEBOOK_ENTRY, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.SECTION_ID, NeutralCriteria.CRITERIA_IN, sectionIds)));
        List<String> ids = new ArrayList<String>();
        for (String id : gradebookIds) {
            ids.add(id);
        }
        securityCache.warm(EntityNames.GRADEBOOK_ENTRY, new HashSet<String>(ids));
        return ids;
    }
    
}
