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

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * User: dkornishev
 */
@Component
public class TransitiveStudentToStudentValidator extends BasicValidator {

    @Autowired
    private DateHelper dateHelper;

    public TransitiveStudentToStudentValidator() {
        super(true, Arrays.asList(EntityNames.STUDENT, EntityNames.PARENT), EntityNames.STUDENT);
    }

    @Override
    protected Set<String> doValidate(Set<String> ids, String entityType) {
        if (!areParametersValid(EntityNames.STUDENT, entityType, ids)) {
            return Collections.emptySet();
        }
        
        Set<String> myCopyOfIds = new HashSet<String>(ids);
        myCopyOfIds.removeAll(SecurityUtil.getSLIPrincipal().getOwnedStudentIds());
        if (myCopyOfIds.size() == 0) {
            return ids;
        }

        Iterable<Entity> ownedStudents = SecurityUtil.getSLIPrincipal().getOwnedStudentEntities();

        for (Entity authenticatedStudent : ownedStudents) {
            // check for current students in my sections, programs, and cohorts. Section first.
            Set<String> currentSections = new HashSet<String>();
            List<Map<String, Object>> putativeSections = authenticatedStudent.getDenormalizedData().get("section");
            if (putativeSections != null) {
                for (Map<String, Object> section : putativeSections) {
                    if (!dateHelper.isFieldExpired(section)) {
                        currentSections.add((String) section.get("_id"));
                    }
                }
                NeutralQuery sectionQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, currentSections, false));
                sectionQuery.setEmbeddedFields(Arrays.asList("studentSectionAssociation"));
                for (Entity section : getRepo().findAll("section", sectionQuery)) {
                    List<Entity> ssas = section.getEmbeddedData().get("studentSectionAssociation");
                    if (ssas != null) {
                        for (Entity ssa : ssas) {
                            if (!dateHelper.isFieldExpired(ssa.getBody())) {
                                myCopyOfIds.remove(ssa.getBody().get("studentId"));
                            }
                        }
                    }
                }
            }
            if (myCopyOfIds.size() == 0) {
                return ids;
            }

            // program and cohorts
            NeutralQuery studentQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, myCopyOfIds, false));
            studentQuery.setEmbeddedFields(Arrays.asList("studentProgramAssociation", "studentCohortAssociation"));
            studentQuery.setIncludeFields(new ArrayList<String>()); // we don't need anything in the body of the superdoc.
            for (Entity student : getRepo().findAll("student", studentQuery)) {
                removeValidIds(myCopyOfIds, authenticatedStudent, student, "studentProgramAssociation", "programId");
                removeValidIds(myCopyOfIds, authenticatedStudent, student, "studentCohortAssociation", "cohortId");
            }

            if (myCopyOfIds.size() == 0) {
                return ids;
            }
        }
        Set<String> validIds  = new HashSet<String>(ids);
        validIds.removeAll(myCopyOfIds);
        return validIds;
    }

    private void removeValidIds(Set<String> ids, Entity authenticatedStudent, Entity student, String subdocType, String refField) {
        Set<String> allowedEntities = new HashSet<String>();
        List<Entity> usersAssociations = authenticatedStudent.getEmbeddedData().get(subdocType);
        if (usersAssociations != null) {
            for (Entity association : usersAssociations) {
                if (!dateHelper.isFieldExpired(association.getBody())) {
                    allowedEntities.add((String) association.getBody().get(refField));
                }
            }
        }
        List<Entity> associations = student.getEmbeddedData().get(subdocType);
        if (associations != null) {
            for (Entity association : associations) {
                String reference = (String) association.getBody().get(refField);
                if (allowedEntities.contains(reference)
                        && !dateHelper.isFieldExpired(association.getBody())) {
                    ids.remove(student.getEntityId());
                }
            }
        }
    }

}
