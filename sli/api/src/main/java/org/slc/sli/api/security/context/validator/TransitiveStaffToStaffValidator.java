/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validates the context of a transitive staff member to see the requested set of staff entities.
 * Returns true if the staff member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class TransitiveStaffToStaffValidator extends AbstractContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Override
    public boolean canValidate(String entityType, boolean transitive) {
        return transitive && EntityNames.STAFF.equals(entityType) && isStaff();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(String entityName, Set<String> staffIds) {
        if (!areParametersValid(EntityNames.STAFF, entityName, staffIds)) {
            return false;
        }
        
        List<String> remainder = new ArrayList<String>(staffIds);
        
        // Intersecting schools
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria("staffReference", NeutralCriteria.CRITERIA_IN,
                staffIds));
        basicQuery.setIncludeFields(Arrays.asList("educationOrganizationReference", "staffReference"));
        info("Attempting to validate transitively from staff to staff with ids {}", staffIds);
        
        injectEndDateQuery(basicQuery);
        
        Iterable<Entity> edOrgAssoc = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        
        Map<String, Set<String>> staffEdorgMap = new HashMap<String, Set<String>>();
        populateMapFromMongoResponse(staffEdorgMap, edOrgAssoc);
        Set<String> edOrgLineage = getStaffEdOrgLineage();
        
        // Ok, user doesn't have any current edorgs, don't go any further
        if (edOrgLineage.isEmpty() || staffEdorgMap.isEmpty()) {
            return false;
        }
        
        for (Entry<String, Set<String>> entry : staffEdorgMap.entrySet()) {
            Set<String> tmpSet = new HashSet<String>(entry.getValue());
            tmpSet.retainAll(edOrgLineage);
            if (tmpSet.size() != 0) {
                remainder.remove(entry.getKey());
            }
        }
        
        // Intersecting direct programs
        remainder.removeAll(validateThrough(EntityNames.STAFF_PROGRAM_ASSOCIATION, "programId"));
        
        // Intersecting direct cohorts
        remainder.removeAll(validateThrough(EntityNames.STAFF_COHORT_ASSOCIATION, "cohortId"));
        
        // Intersecting edorg's programs
        basicQuery = new NeutralQuery(new NeutralCriteria("_id", "in", edOrgLineage));
        Iterable<Entity> edorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, basicQuery);
        
        List<String> programs = new ArrayList<String>();
        for (Entity e : edorgs) {
            Object value = e.getBody().get("programReference");
            if (value != null) {
                if (List.class.isAssignableFrom(value.getClass())) {
                    programs.addAll((List<String>) value);
                } else if (String.class.isAssignableFrom(value.getClass())) {
                    programs.add((String) value);
                }
            }
        }
        
        remainder.removeAll(getIds(EntityNames.STAFF_PROGRAM_ASSOCIATION, "programId", programs));
        
        // Intersecting edorg's cohorts
        basicQuery = new NeutralQuery(new NeutralCriteria("educationOrgId", "in", edOrgLineage));
        List<String> cohorts = (List<String>) repo.findAllIds(EntityNames.COHORT, basicQuery);
        
        remainder.removeAll(getIds(EntityNames.STAFF_COHORT_ASSOCIATION, "cohortId", cohorts));
        
        return remainder.isEmpty();
    }
    
    @SuppressWarnings("unchecked")
    private List<String> validateThrough(String assoc, String field) {
        String meId = SecurityUtil.getSLIPrincipal().getEntity().getEntityId();
        
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria("staffId", "=", meId));
        this.injectEndDateQuery(basicQuery);
        Iterable<Entity> spas = repo.findAll(assoc, basicQuery);
        
        List<String> groups = new ArrayList<String>();
        for (Entity spa : spas) {
            Object value = spa.getBody().get(field);
            
            if (value != null) {
                if (List.class.isAssignableFrom(value.getClass())) {
                    groups.addAll((List<String>) value);
                } else if (String.class.isAssignableFrom(value.getClass())) {
                    groups.add((String) value);
                }
            }
        }
        
        return getIds(assoc, field, groups);
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getIds(String assoc, String field, List<String> groups) {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(field, "in", groups));
        injectEndDateQuery(basicQuery);
        Iterable<Entity> spas = repo.findAll(assoc, basicQuery);
        
        List<String> ids = new ArrayList<String>();
        for (Entity spa : spas) {
            Object value = spa.getBody().get("staffId");
            
            if (List.class.isAssignableFrom(value.getClass())) {
                ids.addAll((List<String>) value);
            } else if (String.class.isAssignableFrom(value.getClass())) {
                ids.add((String) value);
            }
        }
        
        return ids;
    }
    
    private void injectEndDateQuery(NeutralQuery basicQuery) {
        NeutralCriteria endDateCriteria = new NeutralCriteria(ParameterConstants.END_DATE,
                NeutralCriteria.CRITERIA_GTE, getFilterDate(true));
        basicQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.END_DATE,
                NeutralCriteria.CRITERIA_EXISTS, false)));
        basicQuery.addOrQuery(new NeutralQuery(endDateCriteria));
    }
    
    private void populateMapFromMongoResponse(Map<String, Set<String>> staffEdorgMap, Iterable<Entity> edOrgAssoc) {
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
