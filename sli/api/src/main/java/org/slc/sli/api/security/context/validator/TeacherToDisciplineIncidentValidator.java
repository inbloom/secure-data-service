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

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 */
@Component
public class TeacherToDisciplineIncidentValidator extends AbstractContextValidator {
    
    @Autowired 
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToStudentValidator studentValidator;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTeacher() && EntityNames.DISCIPLINE_INCIDENT.equals(entityType);
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {
        if (!areParametersValid(EntityNames.DISCIPLINE_INCIDENT, entityType, ids)) {
            return false;
        }
        
        Set<String> discIncidentIds = new HashSet<String>(ids);
        
        //First look to see if the current teacher is directly involved with the incident
        String myself = SecurityUtil.getSLIPrincipal().getEntity().getEntityId();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, discIncidentIds));
        query.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL, myself));
        Iterable<Entity> incidents = repo.findAll(EntityNames.DISCIPLINE_INCIDENT, query);
        
        for (Entity ent : incidents) {
            discIncidentIds.remove(ent.getEntityId());
        }
        
        if (discIncidentIds.size() == 0) {
            return true;    //we have direct context to all the incidents
        }
        
        //Otherwise the teacher needs to have context to the students involved with the incident
        query = new NeutralQuery(new NeutralCriteria(ParameterConstants.DISCIPLINE_INCIDENT_ID, NeutralCriteria.CRITERIA_IN, discIncidentIds));
        query.setIncludeFields(Arrays.asList(ParameterConstants.STUDENT_ID, ParameterConstants.DISCIPLINE_INCIDENT_ID));
        Iterable<Entity> assocs = repo.findAll(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, query);
        
        Map<String, Set<String>> discIncToStudents = new HashMap<String, Set<String>>();
        for (Entity assoc : assocs) {
            String studentId = (String) assoc.getBody().get(ParameterConstants.STUDENT_ID);
            String discIncId = (String) assoc.getBody().get(ParameterConstants.DISCIPLINE_INCIDENT_ID);
            Set<String> studentList = discIncToStudents.get(discIncId);
            if (studentList == null) {
                studentList = new HashSet<String>();
                discIncToStudents.put(discIncId, studentList);
            }
            studentList.add(studentId);
        }
        
        //Make sure that for each incident we can see at least one of their students
        int validIncidents = 0;
        for (Set<String> studentList : discIncToStudents.values()) {
            if (studentValidator.getValid(EntityNames.STUDENT, studentList).size() > 0) {
                validIncidents++;
            } else {
                return false;
            }
        }
        return validIncidents == discIncidentIds.size();
    }
    
    
}
