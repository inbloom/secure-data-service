package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;

/**
 * transitive student to parent
 * 
 * @author ycao
 * 
 */
@Component
public class TransitiveParentToStudentParentAssociationValidator extends ParentToStudentParentAssociationValidator {
    
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && SecurityUtil.isParent() && entityType.equals(EntityNames.STUDENT_PARENT_ASSOCIATION);
    }
    
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_PARENT_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        Set<String> remainingIds = removePermissibleIds(ids, false);
        return remainingIds.isEmpty();
    }
}
