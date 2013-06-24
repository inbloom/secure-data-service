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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Validator for student access to a staff cohort association
 *
 * @author nbrown
 *
 */
@Component
public class StudentToStaffCohortValidator extends BasicValidator {

    private final DateHelper dateHelper;

    @Autowired
    public StudentToStaffCohortValidator(DateHelper dateHelper) {
        super(true, EntityNames.STUDENT, EntityNames.STAFF_COHORT_ASSOCIATION);
        this.dateHelper = dateHelper;
    }

    @Override
    protected boolean doValidate(Set<String> ids, Entity me, String entityType) {
        List<String> myCohorts = getStudentAssociationIds(me);
        Iterator<Entity> results = getRepo().findEach(
                EntityNames.STAFF_COHORT_ASSOCIATION,
                Query.query(Criteria.where("_id").in(ids).and("body.cohortId").in(myCohorts)
                        .andOperator(DateHelper.getExpiredCriteria())));
        Set<String> unvalidated = new HashSet<String>(ids);
        while(results.hasNext()) {
            Entity e = results.next();
            if(dateHelper.isFieldExpired(e.getBody())) {
                return false;
            }
            unvalidated.remove(e.getEntityId());
        }
        return unvalidated.isEmpty();
    }

    protected List<String> getStudentAssociationIds(Entity me) {
        List<Entity> cohortAssociations = me.getEmbeddedData().get("studentCohortAssociation");
        List<String> myCohorts = new ArrayList<String>();
        for (Entity assoc : cohortAssociations) {
            if (!dateHelper.isFieldExpired(assoc.getBody())) {
                myCohorts.add((String) assoc.getBody().get("cohortId"));
            }
        }
        return myCohorts;
    }


}
