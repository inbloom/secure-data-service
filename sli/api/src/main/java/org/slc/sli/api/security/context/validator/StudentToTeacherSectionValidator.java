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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Vaidator for teacher section associations for students
 *
 * @author nbrown
 *
 */
@Component
public class StudentToTeacherSectionValidator extends StudentToStaffAssociationAbstractValidator {

    public StudentToTeacherSectionValidator() {
        super(EntityNames.TEACHER_SECTION_ASSOCIATION, "sectionId");
    }

    @Override
    protected Set<String> getStudentAssociationIds(Entity me) {
        return getStudentAssociationsFromDenorm(me, "section");
    }

    @Override
    protected Iterator<Entity> getMatchingAssociations(Set<String> ids, Set<String> sectionsForStudent) {
        Iterator<Entity> sections = getRepo().findEach(
                EntityNames.SECTION,
                Query.query(Criteria.where("_id").in(sectionsForStudent).and(EntityNames.TEACHER_SECTION_ASSOCIATION)
                        .elemMatch(Criteria.where("_id").in(ids).andOperator(DateHelper.getExpiredCriteria()))));
        List<Entity> results = new ArrayList<Entity>();
        while (sections.hasNext()) {
            Entity section = sections.next();
            List<Entity> associations = section.getEmbeddedData().get(EntityNames.TEACHER_SECTION_ASSOCIATION);
            for (Entity entity : associations) {
                if (ids.contains(entity.getEntityId())) {
                    results.add(entity);
                }
            }
        }
        return results.iterator();
    }
}
