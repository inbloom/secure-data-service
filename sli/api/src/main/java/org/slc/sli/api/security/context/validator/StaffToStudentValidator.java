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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates the context of a staff member to see the requested set of students. Returns true if the
 * staff member can see ALL of the students, and false otherwise.
 */
@Component
public class StaffToStudentValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.STAFF);
    }

    @Override
    public boolean validate(String entityType, Set<String> studentIds) {
        return validateStaffToStudentContextThroughSharedEducationOrganization(studentIds);
    }

    private boolean validateStaffToStudentContextThroughSharedEducationOrganization(Collection<String> ids) {
        // TODO need programs/cohorts with studentRecordAccess = true
        boolean isValid = true;

        // lookup current staff edOrg associations and get the Ed Org Ids
        Set<String> staffsEdOrgIds = getStaffCurrentAssociatedEdOrgs();

        // lookup students
        Iterable<Entity> students = getStudentEntitiesFromIds(ids);

        if (students != null && students.iterator().hasNext()) {
            for (Entity entity : students) {
                Set<String> studentsEdOrgs = getStudentsEdOrgs(entity);
                if (!isIntersection(staffsEdOrgIds, studentsEdOrgs)) {
                    isValid = false;
                    break;
                }
            }
        } else {
            isValid = false;
        }

        return isValid;
    }

    private boolean isIntersection(Set<String> setA, Set<String> setB) {
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
        Set<String> edOrgs = new HashSet<String>();
        Map<String, List<Map<String, Object>>> denormalized = studentEntity.getDenormalizedData();
        List<Map<String, Object>> schools = denormalized.get("schools");
        if (schools != null) {
            for (Map<String, Object> school : schools) {
                if (school.containsKey("exitWithdrawDate")) {
                    DateTime exitWithdrawDate = getDateTime((String) school.get("exitWithdrawDate"));
                    if (!isLhsBeforeRhs(getNowMinusGracePeriod(), exitWithdrawDate)) {
                        continue;
                    }
                }

                if (school.containsKey("edOrgs")) {
                    @SuppressWarnings("unchecked")
                    List<String> schoolIds = (List<String>) school.get("edOrgs");
                    edOrgs.addAll(schoolIds);
                } else {
                    String schoolId = (String) school.get("_id");
                    edOrgs.add(schoolId);
                }
            }
        }
        return edOrgs;
    }

    private Iterable<Entity> getStudentEntitiesFromIds(Collection<String> studentIds) {
        NeutralQuery studentQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, new ArrayList<String>(studentIds)));
        studentQuery.setEmbeddedFieldString("schools");
        Iterable<Entity> students = getRepo().findAll(EntityNames.STUDENT, studentQuery);
        return students;
    }

}
