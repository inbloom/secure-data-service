package org.slc.sli.api.security.context.validator;

import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates Write context to a global session.
 */
@Component
public class GenericToGlobalSessionWriteValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && EntityNames.SESSION.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.SESSION, entityType, ids)) {
            return false;
        }

        /*
         * Grab all the Sesssions that are being requested AND contain a
         * reference to a edorg in your edorg hierarchy. Counts should be equal
         * if you can see all the sessions you asked for
         */
        Set<String> edOrgLineage = getEdorgLineage(getDirectEdorgs());
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, ids, false));
        query.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.CRITERIA_IN, edOrgLineage));

        return ids.size() == repo.count(entityType, query);
    }

}
