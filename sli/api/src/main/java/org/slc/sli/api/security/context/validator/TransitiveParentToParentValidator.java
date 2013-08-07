package org.slc.sli.api.security.context.validator;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

@Component
public class TransitiveParentToParentValidator extends AbstractContextValidator{
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return SecurityUtil.isParent() && EntityNames.PARENT.equals(entityType) && isTransitive;
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.PARENT, entityType, ids)) {
            return Collections.emptySet();
        }

        Set<Entity> students = SecurityUtil.getSLIPrincipal().getOwnedStudentEntities();
        Set<String> requestedIds = new HashSet<String>(ids);

        for ( Entity student : students) {
            List<Entity> spas = student.getEmbeddedData().get(EntityNames.STUDENT_PARENT_ASSOCIATION);
            if (null != spas) {
                for (Entity spa : spas) {
                    requestedIds.remove(spa.getBody().get(ParameterConstants.PARENT_ID));
                }
            }
        }

        ids.removeAll(requestedIds);
        return ids;
    }
}
