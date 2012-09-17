package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
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
public class TeacherSectionResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    private TeacherStudentResolver studentResolver;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        // return false;
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.SECTION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        
        // We really want the students and the sections that those students are in.
        List<String> studentIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT));
        }

        List<String> teacherSectionIds = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS));
        List<String> studentSectionIds = new ArrayList<String>();

        Iterable<Entity> studentSections = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, new NeutralQuery(
                new NeutralCriteria("studentId", NeutralCriteria.CRITERIA_IN, studentIds)));
        for (Entity studentSection : studentSections) {
            if (studentSection.getBody().containsKey(ParameterConstants.SECTION_ID)) {
                studentSectionIds.add((String) studentSection.getBody().get(ParameterConstants.SECTION_ID));
            }
        }

        Set<String> sectionIds = new HashSet<String>();
        sectionIds.addAll(teacherSectionIds);
        sectionIds.addAll(studentSectionIds);
        securityCache.warm(EntityNames.SECTION, sectionIds);
        return new ArrayList<String>(sectionIds);
    }
}
