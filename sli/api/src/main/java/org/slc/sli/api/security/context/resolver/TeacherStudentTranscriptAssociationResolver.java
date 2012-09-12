package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class TeacherStudentTranscriptAssociationResolver implements EntityContextResolver {
    @Autowired
    private TeacherStudentAcademicRecordResolver academicResolver;
    
    @Autowired
    private TeacherStudentResolver studentResolver;
    
    @Autowired
    private SessionSecurityCache securityCache;
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return EntityNames.TEACHER.equals(fromEntityType)
                && EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION.equals(toEntityType);
    }
    
    @Override
    public List<String> findAccessible(Entity principal) {
        Set<String> ids = getAcademicRecordTranscripts(principal);
        ids.addAll(getStudentTranscripts(principal));
        securityCache.warm(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, ids);
        return new ArrayList<String>(ids);
    }
    
    private Set<String> getAcademicRecordTranscripts(Entity principal) {
        List<String> studentIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT_ACADEMIC_RECORD)) {
            studentIds = academicResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT_ACADEMIC_RECORD));
        }
        Iterable<String> staIds = repo.findAllIds(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID, NeutralCriteria.CRITERIA_IN,
                        studentIds)));
        Set<String> ids = new HashSet<String>();
        for (String id : staIds) {
            ids.add(id);
        }
        return ids;
    }
    
    private Set<String> getStudentTranscripts(Entity principal) {
        
        List<String> studentIds = new ArrayList<String>();
        if (!securityCache.contains(EntityNames.STUDENT)) {
            studentIds = academicResolver.findAccessible(principal);
        } else {
            studentIds = new ArrayList<String>(securityCache.retrieve(EntityNames.STUDENT));
        }
        Iterable<String> staIds = repo.findAllIds(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STUDENT_ID, NeutralCriteria.CRITERIA_IN, studentIds)));
        Set<String> ids = new HashSet<String>();
        for (String id : staIds) {
            ids.add(id);
        }
        return ids;
    }
}
