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

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.dal.convert.SuperDocIdUtility;

/**
 * validate studentCohort/Program associations for parents
 * 
 * @author ycao
 * 
 */
@Component
public class ParentToStudentAssociationValidator extends BasicValidator {
    
    @Autowired
    private TransitiveStudentToStudentValidator studentValidator;
    
    public ParentToStudentAssociationValidator() {
        super(Arrays.asList(EntityNames.PARENT), Arrays.asList(EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT_PROGRAM_ASSOCIATION));
    }
    
    @Override
    protected Set<String> doValidate(Set<String> ids, String entityType) {
        Set<String> studentIds = new HashSet<String>();
        Map<String, Set<String>> studentToAssoc = new HashMap<String, Set<String>>();
        for (String id : ids) {
            String key = SuperDocIdUtility.getParentId(id);
            studentIds.add(key);
            if(!studentToAssoc.containsKey(key)) {
                studentToAssoc.put(key, new HashSet<String>());
            }
            studentToAssoc.get(key).add(id);
        }

        Set<String> validStudentIds = studentValidator.validate(EntityNames.STUDENT, studentIds);
        return getValidIds(validStudentIds, studentToAssoc);
    }
    
}
