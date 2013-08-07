package org.slc.sli.api.security.context.validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;

@Component
public class ParentToStudentParentAssociationValidator extends AbstractContextValidator {
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return !isTransitive && SecurityUtil.isParent() && entityType.equals(EntityNames.STUDENT_PARENT_ASSOCIATION);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.STUDENT_PARENT_ASSOCIATION, entityType, ids)) {
            return Collections.emptySet();
        }
        
        Set<String> remainingIds = removePermissibleIds(ids, true);
        ids.removeAll(remainingIds);
        return ids;
    }
    
    protected Set<String> removePermissibleIds(Set<String> ids, boolean filterBySelf) {
        Set<String> requestedIds = new HashSet<String>(ids);
        Set<Entity> ownStudents = SecurityUtil.getSLIPrincipal().getOwnedStudentEntities();
        for (Entity student : ownStudents) {
            List<Entity> elist = student.getEmbeddedData().get("studentParentAssociation");
            if (elist != null ) {
                for (Entity e : elist) {
                    if (!filterBySelf || e.getBody().get(ParameterConstants.PARENT_ID).equals(SecurityUtil.principalId())) {
                        requestedIds.remove(e.getEntityId());
                    }
                }
            }
        }

        return requestedIds;
    }
}
