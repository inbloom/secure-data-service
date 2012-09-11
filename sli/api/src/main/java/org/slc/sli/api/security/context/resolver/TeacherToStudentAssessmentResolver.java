package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Resolves which teachers a given teacher is allowed to see
 *
 * @author dkornishev
 *
 */
@Component
public class TeacherToStudentAssessmentResolver implements EntityContextResolver {

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private TeacherStudentResolver studentResolver;

    @Autowired
    private SessionSecurityCache securityCachingStrategy;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) &&
            EntityNames.STUDENT_ASSESSMENT_ASSOCIATION.equals(toEntityType);
    }

    @Override
    public List<String> findAccessible(Entity principal) {

        if(securityCachingStrategy.contains(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION)) {
            return new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION));
        }

        List<String> studentIds = new ArrayList<String>();
        if (securityCachingStrategy.contains(EntityNames.STUDENT)) {
            studentIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT));
        } else {
            studentIds = studentResolver.findAccessible(principal);
        }
        List<String> finalIds = helper.findEntitiesContainingReference(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION,
                ParameterConstants.STUDENT_ID, studentIds);
        securityCachingStrategy.warm(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION, new HashSet<String>(finalIds));
        return finalIds;
    }

}
