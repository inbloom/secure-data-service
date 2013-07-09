package org.slc.sli.api.security.context.validator;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ParentToStudentParentAssociationValidator extends AbstractContextValidator {
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return SecurityUtil.isParent() && entityType.equals(EntityNames.STUDENT_PARENT_ASSOCIATION);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_PARENT_ASSOCIATION, entityType, ids)) {
            return false;
        }

        Set<String> requestedIds = new HashSet<String>(ids);
        Set<Entity> ownStudents = SecurityUtil.getSLIPrincipal().getOwnedStudentEntities();
        for (Entity student : ownStudents) {
            List<Entity> elist = student.getEmbeddedData().get("studentParentAssociation");
            if (elist != null ) {
                for (Entity e : elist) {
                    if (e.getBody().get(ParameterConstants.PARENT_ID).equals(SecurityUtil.principalId())) {
                        requestedIds.remove(e.getEntityId());
                    }
                }
            }
        }

        return requestedIds.isEmpty();
    }
}
