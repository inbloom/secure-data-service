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

import java.util.Collections;
import java.util.HashSet;
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
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        Set<String> remainingIds = new HashSet<String>(ids);
        if (!areParametersValid(EntityNames.STUDENT_PARENT_ASSOCIATION, entityType, ids)) {
            return Collections.emptySet();
        }
        
        Entity entity = SecurityUtil.getSLIPrincipal().getEntity();
        List<Entity> elist = entity.getEmbeddedData().get("studentParentAssociation");
        if (elist != null ) {
            for (Entity e : elist) {
                remainingIds.remove(e.getEntityId());
            }
        }
        ids.removeAll(remainingIds);
        return ids;
    }
    
}
