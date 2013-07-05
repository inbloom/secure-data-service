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
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * validating non transtive programs for students
 *
 * @author ycao
 */
@Component
public class StudentToProgramCohortValidator extends BasicValidator {

    @Autowired
    DateHelper dateHelper;

    public StudentToProgramCohortValidator() {
        super(false, Arrays.asList(EntityNames.STUDENT, EntityNames.PARENT), Arrays.asList(EntityNames.PROGRAM, EntityNames.COHORT));
    }

    @Override
    protected boolean doValidate(Set<String> ids, String entityType) {
        String subdocType = null;
        String subdocId = null;
        if (EntityNames.COHORT.equals(entityType)) {
            subdocType = EntityNames.STUDENT_COHORT_ASSOCIATION;
            subdocId = ParameterConstants.COHORT_ID;
        } else if (EntityNames.PROGRAM.equals(entityType)) {
            subdocType = EntityNames.STUDENT_PROGRAM_ASSOCIATION;
            subdocId = ParameterConstants.PROGRAM_ID;
        }

        Set<String> myCurrentIds = new HashSet<String>();

        for (Entity myself : SecurityUtil.getSLIPrincipal().getOwnedStudentEntities()) {

            List<Entity> studentAssociations = myself.getEmbeddedData().get(subdocType);

            if (studentAssociations == null) {
                continue;
            }

            for (Entity myAssociation : studentAssociations) {
                if (myAssociation.getBody() != null && !dateHelper.isFieldExpired(myAssociation.getBody(), ParameterConstants.END_DATE)) {
                    myCurrentIds.add((String) myAssociation.getBody().get(subdocId));
                }
            }
        }

        return myCurrentIds.containsAll(ids);
    }

}
