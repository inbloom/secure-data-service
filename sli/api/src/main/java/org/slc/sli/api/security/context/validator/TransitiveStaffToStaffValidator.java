/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class TransitiveStaffToStaffValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean transitive) {
        return transitive && EntityNames.STAFF.equals(entityType) && isStaff();
    }

    @Override
    public boolean validate(String entityName, Set<String> staffIds) {
        //Query staff's schools
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria("staffReference", NeutralCriteria.CRITERIA_IN, staffIds));
        basicQuery.setIncludeFields(Arrays.asList("educationOrganizationReference", "staffReference"));
        info("Attempting to validate transitively from staff to staff with ids {}", staffIds);

        NeutralCriteria endDateCriteria = new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_GTE, getFilterDate());
        basicQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_EXISTS, false)));
        basicQuery.addOrQuery(new NeutralQuery(endDateCriteria));

        Iterable<Entity> edOrgAssoc = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);

        Map<String, Set<String>> staffEdorgMap = new HashMap<String, Set<String>>();
        populateMapFromMongoResponse(staffEdorgMap, edOrgAssoc);
        Set<String> edOrgLineage = getStaffEdOrgLineage();

        for (Set<String> edorgs : staffEdorgMap.values() ) {
            //Make sure there's a valid intersection between the schools and edOrgLIneage
            Set<String> tmpSet = new HashSet<String>(edorgs);
            tmpSet.retainAll(edOrgLineage);
            if (tmpSet.size() == 0) {
                return false;
            }
        }
        if (staffEdorgMap.size() == 0 || staffEdorgMap.size() != staffIds.size()) {
            return false;
        }
        return true;

    }

    private void populateMapFromMongoResponse(
            Map<String, Set<String>> staffEdorgMap, Iterable<Entity> edOrgAssoc) {
        for (Entity assoc : edOrgAssoc) {
            String staffId = (String) assoc.getBody().get("staffReference");
            String edorgId = (String) assoc.getBody().get("educationOrganizationReference");
            Set<String> edorgList = staffEdorgMap.get(staffId);
            if (edorgList == null) {
                edorgList = new HashSet<String>();
                staffEdorgMap.put(staffId, edorgList);
            }
            edorgList.add(edorgId);
        }
    }

}
