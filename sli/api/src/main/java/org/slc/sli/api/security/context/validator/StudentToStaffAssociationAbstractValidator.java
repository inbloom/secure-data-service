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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Abstract validator to student to staff xxx associations
 *
 * @author nbrown
 */
public abstract class StudentToStaffAssociationAbstractValidator extends BasicValidator {

    private static final String ASSOCIATION_ID_FIELD = "_id";
    private final String collection;
    private final String associationField;

    public StudentToStaffAssociationAbstractValidator(String type, String associationField) {
        super(Arrays.asList(EntityNames.STUDENT, EntityNames.PARENT), type);
        this.collection = type;
        this.associationField = associationField;
    }

    @Override
    protected Set<String> doValidate(Set<String> ids, String entityType) {
        Set<String> validated = new HashSet<String>();
        for (Entity me : SecurityUtil.getSLIPrincipal().getOwnedStudentEntities()) {
            Set<String> studentAssociations = getStudentAssociationIds(me);
            Iterator<Entity> results = getMatchingAssociations(ids, studentAssociations);
            while (results.hasNext()) {
                Entity e = results.next();
                if (isExpired(e)) {
                    validated.clear();
                    break;
                }
                validated.add(e.getEntityId());
            }
        }
        return validated;

    }

    protected boolean isExpired(Entity e) {
        return getDateHelper().isFieldExpired(e.getBody());
    }

    protected Iterator<Entity> getMatchingAssociations(Set<String> ids, Set<String> studentAssociations) {
        Iterator<Entity> results = getRepo().findEach(
                this.getCollection(),
                Query.query(Criteria.where(ASSOCIATION_ID_FIELD).in(ids).and("body." + associationField)
                        .in(new ArrayList<String>(studentAssociations)).andOperator(DateHelper.getExpiredCriteria())));
        return results;
    }

    protected Set<String> getStudentAssociationsFromSubDoc(Entity me, String subDocType, String associationKey) {
        List<Entity> associations = me.getEmbeddedData().get(subDocType);

        if (null == associations) {
            return new HashSet<String>();
        }

        Set<String> myCohorts = new HashSet<String>();
        for (Entity assoc : associations) {
            if (!isExpired(assoc)) {
                myCohorts.add((String) assoc.getBody().get(associationKey));
            }
        }
        return myCohorts;
    }

    protected Set<String> getStudentAssociationsFromDenorm(Entity me, String denormType) {
        List<Map<String, Object>> associations = me.getDenormalizedData().get(denormType);

        if (null == associations) {
            return new HashSet<String>();
        }

        Set<String> myCohorts = new HashSet<String>();
        for (Map<String, Object> assoc : associations) {
            if (!getDateHelper().isFieldExpired(assoc)) {
                myCohorts.add((String) assoc.get("_id"));
            }
        }
        return myCohorts;
    }

    protected abstract Set<String> getStudentAssociationIds(Entity me);

    protected String getCollection() {
        return collection;
    }

}