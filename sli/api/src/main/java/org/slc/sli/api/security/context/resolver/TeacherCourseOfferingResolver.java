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
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TeacherCourseOfferingResolver implements EntityContextResolver {
    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    private TeacherSectionResolver sectionResolver;
    
    @Autowired
    TeacherSessionResolver sessionResolver;
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.COURSE_OFFERING.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> sectionIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.SECTION)) {
            sectionIds = sectionResolver.findAccessible(principal);
        } else {
            sectionIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.SECTION));
        }
        
        List<String> sessionIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.SESSION)) {
            sessionIds = sessionResolver.findAccessible(principal);
        } else {
            sessionIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.SESSION));
        }
        List<String> finalIds = new ArrayList<String>();
        // Go through the sections getting out the sessionIds
        Iterable<Entity> returned = repo.findAll(EntityNames.SECTION, new NeutralQuery(new NeutralCriteria("_id",
                NeutralCriteria.OPERATOR_EQUAL, sectionIds)));
        for (Entity section : returned) {
            if (section.getBody().containsKey(ParameterConstants.COURSE_OFFERING_ID)) {
                finalIds.add((String) section.getBody().get(ParameterConstants.COURSE_OFFERING_ID));
            }
        }
        
        returned = repo.findAll(EntityNames.SESSION, new NeutralQuery(new NeutralCriteria("_id",
                NeutralCriteria.OPERATOR_EQUAL, sessionIds)));
        for (Entity section : returned) {
            if (section.getBody().containsKey(ParameterConstants.COURSE_OFFERING_ID)) {
                finalIds.add((String) section.getBody().get(ParameterConstants.COURSE_OFFERING_ID));
            }
        }


        securityCachingStrategy.warm(EntityNames.SESSION, new HashSet<String>(finalIds));
        return finalIds;
    }
    
}
