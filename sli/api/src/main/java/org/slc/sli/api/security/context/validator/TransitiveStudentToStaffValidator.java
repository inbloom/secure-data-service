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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Validator for teacher/staff
 *
 * @author nbrown
 *
 */
@Component
public class TransitiveStudentToStaffValidator extends BasicValidator {

    public TransitiveStudentToStaffValidator() {
        super(true, EntityNames.STUDENT, Arrays.asList(EntityNames.STAFF, EntityNames.TEACHER));
    }

    @Override
    protected boolean doValidate(Set<String> ids) {
        Entity me = SecurityUtil.getSLIPrincipal().getEntity();
        Set<String> idsToCheck = new HashSet<String>(ids);
        idsToCheck.removeAll(filterConnectedViaEdOrg(idsToCheck, me));
        idsToCheck.removeAll(filterConnectedViaSection(idsToCheck, me));
        idsToCheck.removeAll(filterConnectedViaProgram(idsToCheck, me));
        idsToCheck.removeAll(filterConnectedViaCohort(idsToCheck, me));
        return idsToCheck.isEmpty();
    }

    protected Set<String> filterConnectedViaProgram(Set<String> ids, Entity me) {
        if (ids.isEmpty()) {
            return ids;
        }
        return filterThroughSubdocs(ids, me, "studentProgramAssociation", "programId", "programId",
                EntityNames.STAFF_PROGRAM_ASSOCIATION, "staffId", false);
    }

    private Set<String> filterThroughSubdocs(Set<String> ids, Entity me, String subDocType, String subDocKey,
            String idKey, String staffAssocType, String staffRef, boolean isDenorm) {
        Set<String> subDocIds = isDenorm ? getDenormIds(me, subDocType, subDocKey) : getSubDocIds(me, subDocType,
                subDocKey);
        if (subDocIds.isEmpty()) {
            return Collections.emptySet();
        }
        Query q = Query.query(Criteria.where("body." + idKey).in(subDocIds).and("body." + staffRef).in(ids)
                .andOperator(DateHelper.getExpiredCriteria()));
        Iterator<Entity> spas = getRepo().findEach(staffAssocType, q);
        Set<String> filtered = new HashSet<String>();
        while (spas.hasNext()) {
            Entity spa = spas.next();
            String staff = (String) spa.getBody().get(staffRef);
            if (staff != null) {
                filtered.add(staff);
            }
        }
        return filtered;
    }

    protected Set<String> filterConnectedViaCohort(Set<String> ids, Entity me) {
        if (ids.isEmpty()) {
            return ids;
        }
        return filterThroughSubdocs(ids, me, "studentCohortAssociation", "cohortId", "cohortId",
                EntityNames.STAFF_COHORT_ASSOCIATION, "staffId", false);
    }

    protected Set<String> filterConnectedViaSection(Set<String> ids, Entity me) {
        if (ids.isEmpty()) {
            return ids;
        }
        //The fact that this goes through a subdoc on section means I can't reuse filterThroughSubdocs without adding another couple dozen parameters to that method...
        Set<String> sectionIds = getDenormIds(me, "section", "_id");
        if (sectionIds.isEmpty()) {
            return Collections.emptySet();
        }
        Query q = Query.query(Criteria.where("_id").in(sectionIds).and("teacherSectionAssociation").elemMatch(Criteria.where("body.teacherId").in(ids).andOperator(DateHelper.getExpiredCriteria())));
        q.fields().include("teacherSectionAssociation.$");
        Iterator<Entity> sections = getRepo().findEach(EntityNames.SECTION, q);
        Set<String> filtered = new HashSet<String>();
        while(sections.hasNext()) {
            Entity section = sections.next();
            List<Entity> tsas = section.getEmbeddedData().get("teacherSectionAssociation");
            for(Entity tsa: tsas) {
                String teacher = (String) tsa.getBody().get("teacherId");
                filtered.add(teacher);
            }
        }
        return filtered;
    }

    protected Set<String> filterConnectedViaEdOrg(Set<String> ids, Entity me) {
        if (ids.isEmpty()) {
            return ids;
        }
        return filterThroughSubdocs(ids, me, "schools", "_id", "educationOrganizationReference",
                EntityNames.STAFF_ED_ORG_ASSOCIATION, "staffReference", true);
    }

    private Set<String> getSubDocIds(Entity me, String subDocType, String idKey) {
        List<Entity> subDocs = me.getEmbeddedData().get(subDocType);
        if (subDocs == null) {
            return Collections.emptySet();
        }
        Set<String> assocIds = new HashSet<String>();
        for (Entity subDoc : subDocs) {
            assocIds.add((String) subDoc.getBody().get(idKey));
        }
        return assocIds;
    }

    // virtually the exact same function except with fricken Maps instead of entities....
    private Set<String> getDenormIds(Entity me, String denormType, String idKey) {
        List<Map<String, Object>> denorms = me.getDenormalizedData().get(denormType);
        if (denorms == null) {
            return Collections.emptySet();
        }
        Set<String> assocIds = new HashSet<String>();
        for (Map<String, Object> denorm : denorms) {
            assocIds.add((String) denorm.get(idKey));
        }
        return assocIds;
    }
}
