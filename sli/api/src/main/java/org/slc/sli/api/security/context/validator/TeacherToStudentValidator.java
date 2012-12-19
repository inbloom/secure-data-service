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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

/**
 * Validates the context of a teacher to see the requested set of student entities.
 * Returns true if the teacher member can see ALL of the entities, and false otherwise.
 *
 * @author mabernathy
 */
@Component
public class TeacherToStudentValidator extends AbstractContextValidator {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private AssociativeContextHelper helper;

    @Value("${sli.IdsPerAPICall:10}")
    private int idsPerAPICall;

    @Override
    public boolean canValidate(String entityType, boolean through) {
        return EntityNames.STUDENT.equals(entityType)
                && SecurityUtil.getSLIPrincipal().getEntity().getType().equals(EntityNames.TEACHER);
    }

    @Override
    public boolean validate(String entityName, Set<String> ids) {
        return ids.size() != 0 &&
                (validatedWithSections(ids) || validatedWithCohorts(ids) || validatedWithPrograms(ids));
    }

    private boolean validatedWithSections(Set<String> ids) {
	    
	    if (ids.size() == 0) {
	    	return false;
	    }
	    
	    Set<String> teacherSections = getTeacherSections();
	    
	    Map<String, List<String>> studentSectionIds = getStudentParameterIds(Lists.newArrayList(ids), ParameterConstants.SECTION_ID,
                EntityNames.STUDENT_SECTION_ASSOCIATION);
	    
	    if (studentSectionIds.size() == 0) {
	    	// students not found by program
	    	return false;
	    }
	    
	    Set<String> tempSet = new HashSet<String>(teacherSections);
	    for (String studentId : studentSectionIds.keySet()) {
	        List<String> studentSections = studentSectionIds.get(studentId);
	
	        tempSet.retainAll(studentSections);
	        if (tempSet.isEmpty()) {
	            return false;
	        }

	        tempSet.clear();
	        tempSet.addAll(teacherSections);
	    }
	    
	    return true;
	}

	private boolean validatedWithCohorts(Set<String> ids) {
	    boolean match;
	    Set<String> staffCohortIds = getStaffCohortIds();
	
	    Iterable<Entity> studentList =
	            helper.getEntitiesWithDenormalizedReference(EntityNames.STUDENT, "cohort", new ArrayList<String>(staffCohortIds));
	    Set<String> studentIds = new HashSet<String>();
	    for (Entity student : studentList) {
	        List<Map<String, Object>> cohortList = student.getDenormalizedData().get("cohort");
	        for (Map<String, Object> cohort : cohortList) {
	            String cohortRefId = (String) cohort.get("_id");
	            if (staffCohortIds.contains(cohortRefId)) {
	                if (!isFieldExpired(cohort, ParameterConstants.END_DATE, false)) {
	                    studentIds.add(student.getEntityId());
	                    break;
	                }
	            }
	        }
	    }
	    match = studentIds.containsAll(ids);
	    return match;
	}

	private boolean validatedWithPrograms(Set<String> ids) {
	    if (ids.size() == 0) {
	    	return false;
	    }
	    
	    Set<String> staffProgramIds = getStaffPrograms();
	    
	    Map<String, List<String>> studentProgramIds = getStudentParameterIds(Lists.newArrayList(ids), ParameterConstants.PROGRAM_ID, EntityNames.STUDENT_PROGRAM_ASSOCIATION);
	    
	    if (studentProgramIds.size() == 0) {
	    	// students not found by program
	    	return false;
	    }
	    
	    Set<String> tempSet = new HashSet<String>(staffProgramIds);
	    // Get studentProgramAssociations
	    for (String studentId : studentProgramIds.keySet()) {
	    	tempSet.retainAll(studentProgramIds.get(studentId));
	        
	        if (tempSet.size() == 0) {
	            return false;
	        }
	        
	        tempSet.clear();
	        tempSet.addAll(staffProgramIds);
	    }
	    
	    return true;
	}

	private Map<String, List<String>> getStudentParameterIds(List<String> ids, String parameter, String entityName) {
		
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<Entity> studentEntities = new ArrayList<Entity>();

        // we are making API calls with multiple ids for performance optimizations
        for (int i = 0; i <= ids.size() / idsPerAPICall; i++) {
            List<String> subList = ids.subList(i * idsPerAPICall,
                    Math.min((i + 1) * idsPerAPICall, ids.size()));
            NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
    		        NeutralCriteria.CRITERIA_IN, subList));
    		
    		addEndDateToQuery(basicQuery, false);
    		studentEntities.addAll(Lists.newArrayList(repo.findAll(entityName, basicQuery)));
        }

        for (String id : ids) {
        	result.put(id, new ArrayList<String>());
        }
        
		for (Entity studentEntity : studentEntities) {
		    String studentId = (String) studentEntity.getBody().get(ParameterConstants.STUDENT_ID);
		    List<String> parameterIds = result.get(studentId);
			
		    String parameterId = (String) studentEntity.getBody().get(parameter);
		    parameterIds.add(parameterId);
		}
		
		return result;
	}

	private Set<String> getStaffPrograms() {
		Set<String> staffProgramIds = new HashSet<String>();

        // Get my staffProgramAssociations
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL,
                SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        addEndDateToQuery(basicQuery, false);
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        Iterable<Entity> staffCas = repo.findAll(EntityNames.STAFF_PROGRAM_ASSOCIATION, basicQuery);
        
        // Look at only the SCAs for programs in my edorg with date/record access
        for (Entity sca : staffCas) {
            String programId = (String) sca.getBody().get(ParameterConstants.PROGRAM_ID);
            staffProgramIds.add(programId);
        }
		return staffProgramIds;
	}
	
	private Set<String> getTeacherSections() {
		Set<String> teacherSectionIds = new HashSet<String>();

        NeutralQuery basicQuery = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.TEACHER_ID, NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                        .getSLIPrincipal().getEntity().getEntityId()));
        addEndDateToQuery(basicQuery, true);

        Iterable<Entity> tsas = repo.findAll(EntityNames.TEACHER_SECTION_ASSOCIATION, basicQuery);
        
        // Look at only the SCAs for programs in my edorg with date/record access
        for (Entity tsa : tsas) {
            String sectionId = (String) tsa.getBody().get(ParameterConstants.SECTION_ID);
            teacherSectionIds.add(sectionId);
        }
		return teacherSectionIds;
	}


    private Set<String> getStaffCohortIds() {
		Set<String> staffCohortIds = new HashSet<String>();

        // Get my staffCohortAssociations
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_ID, NeutralCriteria.OPERATOR_EQUAL,
                SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        basicQuery.addCriteria(new NeutralCriteria(ParameterConstants.STUDENT_RECORD_ACCESS,
                NeutralCriteria.OPERATOR_EQUAL, true));
        addEndDateToQuery(basicQuery, false);
        Iterable<Entity> staffCas = repo.findAll(EntityNames.STAFF_COHORT_ASSOCIATION, basicQuery);
        // Look at only the SCAs for cohorts in my edorg with date/record access
        if (staffCas != null) {
            for (Entity sca : staffCas) {
                String cohortId = (String) sca.getBody().get(ParameterConstants.COHORT_ID);
                staffCohortIds.add(cohortId);
            }
        }
		return staffCohortIds;
	}

    public void setRepo(PagingRepositoryDelegate<Entity> repo) {
        this.repo = repo;
    }
}
