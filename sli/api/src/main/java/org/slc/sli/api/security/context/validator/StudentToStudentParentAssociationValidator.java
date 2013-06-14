package org.slc.sli.api.security.context.validator;

import java.util.List;
import java.util.Set;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

/**
 * Validates if an SLIPrincipal who is a Student can see the given student parent associations.
 * Returns true if the student can see ALL of the associations (i.e. they are studentParentAssociations
 * embedded in the given student), and false otherwise.
 * 
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
