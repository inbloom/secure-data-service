package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.traversal.cache.impl.SessionSecurityCache;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TeacherAttendanceResolver implements EntityContextResolver {
    @Autowired
    private TeacherStudentResolver studentResolver;
    
    @Autowired
    private SessionSecurityCache securityCachingStrategy;
    
    @Autowired
    
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType) && EntityNames.ATTENDANCE.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCachingStrategy.contains(EntityNames.STUDENT)) {
            studentIds = studentResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCachingStrategy.retrieve(EntityNames.STUDENT));
        }
        Iterable<String> attendanceIds = repo.findAllIds(EntityNames.ATTENDANCE, new NeutralQuery(new NeutralCriteria(
                "studentId", NeutralCriteria.CRITERIA_IN, studentIds)));
        List<String> finalIds = new ArrayList<String>();
        for (String id : attendanceIds) {
            finalIds.add(id);
        }
        securityCachingStrategy.warm(EntityNames.ATTENDANCE, new HashSet<String>(finalIds));
        return finalIds;
    }
    
}
