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

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * validate cohorts transitively for a student
 *
 * @author ycao
 */
@Component
public class StudentToCohortValidator extends BasicValidator {

    public StudentToCohortValidator() {
        super(true, Arrays.asList(EntityNames.STUDENT, EntityNames.PARENT), EntityNames.COHORT);
    }

    @Override
    protected boolean doValidate(Set<String> ids, String entityType) {


        Set<String> myCohorts = new HashSet<String>();
        for (Entity owned : SecurityUtil.getSLIPrincipal().getOwnedStudentEntities()) {

            if (owned.getEmbeddedData() != null) {
                List<Entity> studentCohortAssociations = owned.getEmbeddedData().get(EntityNames.STUDENT_COHORT_ASSOCIATION);

                if (studentCohortAssociations == null) {
                    return false;
                }

                for (Entity myCohortAssociation : studentCohortAssociations) {
                    if (myCohortAssociation.getBody() != null) {
                        myCohorts.add((String) myCohortAssociation.getBody().get(ParameterConstants.COHORT_ID));
                    }
                }
            }

        }

        return myCohorts.containsAll(ids);
    }

}
