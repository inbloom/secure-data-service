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

import java.util.*;

import org.slc.sli.api.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 *  A generic validator to handle both student-parent and teacher-parent.
 *  The only difference between the two is the logic to determine
 *  whether the user can access a given student, which is left to 
 *  the student validator.
 */
@Component
public class GenericToParentValidator extends AbstractContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired 
    ContextValidator validatorStore;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return (isStaff() || isTeacher()) && EntityNames.PARENT.equals(entityType);
    }

    @Override
    public Set<String> validate(String entityType, Set<String> parentIds) throws IllegalStateException {
        if (!areParametersValid(EntityNames.PARENT, entityType, parentIds)) {
            return Collections.emptySet();
        }
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria("parentId", NeutralCriteria.CRITERIA_IN, parentIds));
        basicQuery.setIncludeFields(Arrays.asList("studentId", "parentId"));
        Iterable<Entity> assocs = repo.findAll(EntityNames.STUDENT_PARENT_ASSOCIATION, basicQuery);

        Map<String, Set<String>> parentToStudentMap = new HashMap<String, Set<String>>();

        for (Entity assoc : assocs) {
            String studentId = (String) assoc.getBody().get("studentId");
            String parentId = (String) assoc.getBody().get("parentId");
            if (!parentToStudentMap.containsKey(parentId)) {
                parentToStudentMap.put(parentId, new HashSet<String>());
            }
            parentToStudentMap.get(parentId).add(studentId);
        }

        //Make sure that for each parent we can see at least one of their students
        IContextValidator studentValidator = validatorStore.findValidator(EntityNames.STUDENT, true);

        Set<String> validParentsIds = new HashSet<String>();
        for(Map.Entry<String, Set<String>> entry : parentToStudentMap.entrySet()) {
            if (studentValidator.validate(EntityNames.STUDENT, entry.getValue()).size() > 0) {
                validParentsIds.add(entry.getKey());
            }
        }
        return validParentsIds;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.DUAL_CONTEXT;
    }
}
