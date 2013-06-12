package org.slc.sli.api.security.context.validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;

/**
 * User: rzingle
 */
@Component

public class StudentToStudentParentAssociationValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStudent() && EntityNames.STUDENT_PARENT_ASSOCIATION.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_PARENT_ASSOCIATION, entityType, ids)) {
            return false;
        }
        
        Entity entity = SecurityUtil.getSLIPrincipal().getEntity();
        List<Entity> elist = entity.getEmbeddedData().get("studentParentAssociation");
        if (elist != null ) {
            for (Entity e : elist) {
                ids.remove(e.getEntityId());
            }
        }

        return ids.isEmpty();
    }
    
}
