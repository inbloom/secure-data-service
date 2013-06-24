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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

public abstract class StudentToStaffAssociation extends BasicValidator {

    private final String collection;

    private final String associationField;

    public StudentToStaffAssociation(String type, String associationField) {
        super(true, EntityNames.STUDENT, type);
        this.collection = type;
        this.associationField = associationField;
    }

    @Override
    protected boolean doValidate(Set<String> ids, Entity me, String entityType) {
        List<String> studentAssociations = getStudentAssociationIds(me);
        Iterator<Entity> results = getRepo().findEach(
                this.collection,
                Query.query(Criteria.where("_id").in(ids).and("body." + associationField).in(studentAssociations)
                        .andOperator(DateHelper.getExpiredCriteria())));
        Set<String> unvalidated = new HashSet<String>(ids);
        while (results.hasNext()) {
            Entity e = results.next();
            if (getDateHelper().isFieldExpired(e.getBody())) {
                return false;
            }
            unvalidated.remove(e.getEntityId());
        }
        return unvalidated.isEmpty();
    }

    protected abstract List<String> getStudentAssociationIds(Entity me);

}