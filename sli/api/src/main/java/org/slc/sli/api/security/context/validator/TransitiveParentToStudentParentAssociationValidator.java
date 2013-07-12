package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

/**
 * transitive student to parent
 * 
 * @author ycao
 * 
 */
@Component
public class TransitiveParentToStudentParentAssociationValidator extends BasicValidator {
    
    public TransitiveParentToStudentParentAssociationValidator() {
        super(true, Arrays.asList(EntityNames.PARENT), Arrays.asList(EntityNames.STUDENT_PARENT_ASSOCIATION));
    }
    
    @Override
    protected boolean doValidate(Set<String> ids, String entityType) {
        if (!areParametersValid(EntityNames.STUDENT_PARENT_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        Set<String> requestedIds = new HashSet<String>(ids);
        Set<Entity> ownStudents = SecurityUtil.getSLIPrincipal().getOwnedStudentEntities();
        for (Entity student : ownStudents) {
            List<Entity> elist = student.getEmbeddedData().get(EntityNames.STUDENT_PARENT_ASSOCIATION);
            if (elist != null) {
                for (Entity e : elist) {
                    requestedIds.remove(e.getEntityId());
                }
            }
        }
        
        return requestedIds.isEmpty();
    }

}
