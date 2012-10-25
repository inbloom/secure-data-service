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

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StaffToStudentValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return !through && EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.STAFF);
    }

    @Override
    public boolean validate(String entityType, Set<String> studentIds) {
        return validateStaffToStudentContextThroughSharedEducationOrganization(studentIds);
    }

    private boolean validateStaffToStudentContextThroughSharedEducationOrganization(Collection<String> ids) {
        boolean isValid = true;

        //lookup current staff edOrg associations and get the Ed Org Ids
        Set<String> staffsEdOrgIds = getStaffsDirectlyAssociatedEdOrgs();
        //lookup students
        Iterable<Entity> students = getStudentEntitiesFromIds(ids);

        for (Entity entity : students) {
            Set<String> studentsEdOrgs = getStudentsEdOrgs(entity);
            if (!isIntersection(staffsEdOrgIds, studentsEdOrgs)) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private boolean isIntersection(Set setA, Set setB) {
        boolean isIntersection = false;
        for (Object a : setA) {
            if (setB.contains(a)) {
                isIntersection = true;
                break;
            }
        }
        return isIntersection;
    }

    private Set<String> getStudentsEdOrgs(Entity studentEntity) {
        return Collections.emptySet();  //`TODO replace stub
    }

    private Iterable<Entity> getStudentEntitiesFromIds(Collection<String> studentIds) {
        NeutralQuery studentQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, studentIds));
        Iterable<Entity> students = repo.findAll(EntityNames.STUDENT, studentQuery);
        return students;
    }

    private Set<String> getStaffsDirectlyAssociatedEdOrgs() {
        NeutralQuery staffEdOrgAssocQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.STAFF_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                        .getSLIPrincipal().getEntity().getEntityId()));
        staffEdOrgAssocQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_EXISTS, false)));
        staffEdOrgAssocQuery.addOrQuery(new NeutralQuery(new NeutralCriteria(ParameterConstants.END_DATE, NeutralCriteria.CRITERIA_GTE, getFilterDate())));

        Iterable<Entity> staffEdOrgAssociations = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, staffEdOrgAssocQuery);
        Set<String> staffEdOrgs = new HashSet<String>();
        for (Entity entity : staffEdOrgAssociations) {
            staffEdOrgs.add((String) entity.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
        }
        return staffEdOrgs;
    }


    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
