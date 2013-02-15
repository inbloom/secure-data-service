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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  A generic validator to handle both student-gradebookEntry and teacher-gradebookEntry.
 *  Since the only difference between the two is the logic to determine
 *  whether the user can access a given section.
 */
@Component
public class GenericToGradebookEntryValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired 
    ContextValidator validatorStore;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.GRADEBOOK_ENTRY.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.GRADEBOOK_ENTRY, entityType, ids)) {
            return false;
        }
        
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids));
        query.setIncludeFields(Arrays.asList(ParameterConstants.SECTION_ID));
        Iterable<Entity> ents = repo.findAll(EntityNames.GRADEBOOK_ENTRY, query);

        Set<String> sectionIds = new HashSet<String>();

        for (Entity gbe : ents) {
            String sectionId = (String) gbe.getBody().get("sectionId");
            sectionIds.add(sectionId);
        }

        return validatorStore.findValidator(EntityNames.SECTION, true).validate(EntityNames.SECTION, sectionIds);
    }

}
