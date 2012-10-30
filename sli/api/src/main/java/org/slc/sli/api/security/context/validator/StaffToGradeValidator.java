/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Validates the context of a staff member to see the requested set of grades. Returns true if the
 * staff member can see ALL of the grades, and false otherwise.
 *
 * @author shalka
 */
@Component
public class StaffToGradeValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private StaffToStudentValidator validator;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return !through && isStaff() && EntityNames.GRADE.equals(entityType);
    }

    @Override
    public boolean validate(String entityType, Set<String> ids) {
        List<String> studentSectionAssociationIds = getIdsContainedInFieldOnEntities(entityType, new ArrayList<String>(
                ids), ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);
        if (studentSectionAssociationIds.isEmpty()) {
            return false;
        }

        List<String> studentIds = getIdsContainedInFieldOnEntities(EntityNames.STUDENT_SECTION_ASSOCIATION,
                studentSectionAssociationIds, ParameterConstants.STUDENT_ID);
        if (studentIds.isEmpty()) {
            return false;
        }

        return validator.validate(EntityNames.STUDENT, new HashSet<String>(studentIds));
    }

    /**
     * Performs a query for entities of type 'type' with _id contained in the List of 'ids'.
     * Iterates through result and peels off String value contained in body.<<field>>. Returns
     * unique set of values that were stored in body.<<field>>.
     *
     * @param type
     *            Entity type to query for.
     * @param ids
     *            List of _ids of entities to query.
     * @param field
     *            Field (contained in body) to peel off of entities.
     * @return List of Strings representing unique Set of values stored in entities' body.<<field>>.
     */
    protected List<String> getIdsContainedInFieldOnEntities(String type, List<String> ids, String field) {
        Set<String> matching = new HashSet<String>();

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.OPERATOR_EQUAL, ids));
        Iterable<Entity> entities = repo.findAll(type, query);
        if (entities != null) {
            for (Entity entity : entities) {
                Map<String, Object> body = entity.getBody();
                if (body.containsKey(field)) {
                    matching.add((String) body.get(field));
                }
            }
        }

        return new ArrayList<String>(matching);
    }

    /**
     * Sets the paging repository delegate (for unit testing).
     *
     * @param repo
     *            Paging Delete Repository to use.
     */
    protected void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }

    /**
     * Sets the staff to student validator (for unit testing).
     *
     * @param staffToStudentValidator
     *            Staff To Student Validator.
     */
    protected void setStaffToStudentValidator(StaffToStudentValidator staffToStudentValidator) {
        this.validator = staffToStudentValidator;
    }
}
