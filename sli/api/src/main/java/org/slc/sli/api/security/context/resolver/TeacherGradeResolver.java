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
public class TeacherGradeResolver implements EntityContextResolver {
    @Autowired
    private TeacherStudentSectionAssociationResolver studentSectionResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.GRADE.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> sectionIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT_SECTION_ASSOCIATION)) {
            sectionIds = studentSectionResolver.findAccessible(principal);
        } else {
            sectionIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT_SECTION_ASSOCIATION));
        }
        Iterable<String> gradeIds = repo.findAllIds(EntityNames.GRADE, new NeutralQuery(new NeutralCriteria(
                ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID, NeutralCriteria.CRITERIA_IN, sectionIds)));
        List<String> ids = new ArrayList<String>();
        for (String id : gradeIds) {
            ids.add(id);
        }
        securityCache.warm(EntityNames.GRADE, new HashSet<String>(ids));
        return ids;
    }
}
