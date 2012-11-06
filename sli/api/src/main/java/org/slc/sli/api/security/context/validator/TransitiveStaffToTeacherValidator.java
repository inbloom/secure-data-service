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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class TransitiveStaffToTeacherValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return through && EntityNames.TEACHER.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.STAFF);
    }

    @Override
    public boolean validate(String entityName, Set<String> teacherIds) {

        //Query teacher's schools
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria("teacherId", NeutralCriteria.CRITERIA_IN, teacherIds));
        basicQuery.setIncludeFields(Arrays.asList("teacherId", "schoolId"));
        Iterable<Entity> schoolAssoc = repo.findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, basicQuery);
        Map<String, Set<String>> teacherSchoolMap = new HashMap<String, Set<String>>();
        populateMapFromMongoResponse(teacherSchoolMap, schoolAssoc);
        Set<String> edOrgLineage = getStaffEdOrgLineage();
        for (Set<String> schools : teacherSchoolMap.values() ) {

            //Make sure there's a valid intersection between the schools and edOrgLIneage
            Set<String> tmpSet = new HashSet<String>(schools);
            tmpSet.retainAll(edOrgLineage);
            if (tmpSet.size() == 0) {
                return false;
            }
        }
        if (teacherSchoolMap.size() == 0 || teacherSchoolMap.size() != teacherSchoolMap.size()) {
            return false;
        }
        return true;

    }

    private void populateMapFromMongoResponse(
            Map<String, Set<String>> teacherSchoolMap, Iterable<Entity> schoolAssoc) {
        for (Entity assoc : schoolAssoc) {
            String teacherId = (String) assoc.getBody().get("teacherId");
            String schoolId = (String) assoc.getBody().get("schoolId");
            Set<String> edorgList = teacherSchoolMap.get(teacherId);
            if (edorgList == null) {
                edorgList = new HashSet<String>();
                teacherSchoolMap.put(teacherId, edorgList);
            }
            edorgList.add(schoolId);
        }
    }

}
