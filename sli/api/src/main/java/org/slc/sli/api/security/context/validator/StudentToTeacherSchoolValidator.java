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

import java.util.Iterator;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Validator for students accessing teacher school associations
 *
 * @author nbrown
 *
 */
@Component
public class StudentToTeacherSchoolValidator extends StudentToStaffAssociation {

    public StudentToTeacherSchoolValidator() {
        super(EntityNames.TEACHER_SCHOOL_ASSOCIATION, "schoolId");
    }

    @Override
    protected Set<String> getStudentAssociationIds(Entity me) {
        return getStudentAssociationsFromDenorm(me, "schools");
    }

    @Override
    protected boolean isExpired(Entity e) {
        // someone forgot to add an enddate to this entity, so we have to look it up in its third
        // abnormal form analog, staffEdOrgAssociations
        // I hope no one expects this to be fast to query...
        String teacherId = (String) e.getBody().get("teacherId");
        String schoolId = (String) e.getBody().get("schoolId");
        Iterator<Entity> analogs = getRepo().findEach(
                EntityNames.STAFF_ED_ORG_ASSOCIATION,
                Query.query(
                        Criteria.where("body.staffReference").is(teacherId).and("body.educationOrganizationReference")
                                .is(schoolId)).limit(1).addCriteria(DateHelper.getExpiredCriteria()));
        return !analogs.hasNext();
    }

}
