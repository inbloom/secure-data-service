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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TeacherToStudentValidator implements IContextValidator {
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Override
    public boolean canValidate(String entityType) {
        return EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER);
    }
    
    @Override
    public boolean validate(Set<String> ids) {
        Set<String> teacherSections = new HashSet<String>();
        boolean match = false;
        
        NeutralCriteria endDateCriteria = getEndDateCriteria();

        NeutralQuery basicQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.TEACHER_ID, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                        .getSLIPrincipal().getEntity().getEntityId()));
        // basicQuery.addCriteria(endDateCriteria);
        Iterable<Entity> tsas = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, basicQuery);
        for (Entity tsa : tsas) {
            teacherSections.add((String) tsa.getBody().get(ParameterConstants.SECTION_ID));
        }
        for(String id : ids) {
            Set<String> studentSections = new HashSet<String>();
            basicQuery = new NeutralQuery(
new NeutralCriteria(ParameterConstants.STUDENT_ID,
                    NeutralCriteria.OPERATOR_EQUAL, id));
            // basicQuery.addCriteria(endDateCriteria);
            Iterable<Entity> ssas = repo.findAll(EntityNames.STUDENT_SECTION_ASSOCIATION, basicQuery);
            for (Entity ssa : ssas) {
                studentSections.add((String) ssa.getBody().get(ParameterConstants.SECTION_ID));
            }
            Set<String> tempSet = new HashSet<String>(teacherSections);
            tempSet.retainAll(studentSections);
            if (tempSet.size() == 0) {
                return false;
            }
        }
        return true;
    }

    public NeutralCriteria getEndDateCriteria() {
        return new NeutralCriteria(ParameterConstants.END_DATE,
                NeutralCriteria.CRITERIA_GTE, getFilterDate(gracePeriod));
    }
    
    public String getFilterDate(String gracePeriod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (gracePeriod != null && !gracePeriod.equals("")) {
            int numDays = Integer.parseInt(gracePeriod) * -1;
            calendar.add(Calendar.DATE, numDays);
        }
        
        return String.format("%1$tY-%1$tm-%1$td", calendar);
    }

    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
