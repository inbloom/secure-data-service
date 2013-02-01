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
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
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
 * Validate's teacher's context to course offering by looking your edorg lineage.
 * 
 * 
 */
@Component
public class TransitiveTeacherToCourseValidator extends AbstractContextValidator {
    
    @Autowired
    EdOrgHelper helper;
    
    @Autowired
    TransitiveTeacherToSectionValidator sectionValidator;
    
    @Autowired
    TeacherToCourseValidator courseValidator;
    
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && isTeacher() && EntityNames.COURSE.equals(entityType);
    }
    
    @Override
    public boolean validate(String entityType, Set<String> ids) {    
        if (!areParametersValid(EntityNames.COURSE, entityType, ids)) {
            return false;
        }
        
        HashSet<String> toValidate = new HashSet<String>(ids);
        
        //CourseValidator has an efficient getValid implementation, so use that here to validate
        //everything accessible through the course's edorg association
        toValidate.removeAll(courseValidator.getValid(EntityNames.COURSE, ids));
        
        if (toValidate.size() == 0) {
            return true;
        } else {
            return validateThroughSections(toValidate);
        }
    }

    /**
     * Determine which course offerings are associated to each course, and validate that
     * for each course at least one course offering is valid.
     * 
     * A course offering is valid is the teacher can access one of the sections of the course offering.
     * 
     * @param courseIds
     * @return
     */
    private boolean validateThroughSections(Set<String> courseIds) {
        NeutralQuery nq = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.COURSE_ID, NeutralCriteria.CRITERIA_IN, courseIds));
        nq.setIncludeFields(Arrays.asList(ParameterConstants.COURSE_ID));
        Iterable<Entity> coEnts = getRepo().findAll(EntityNames.COURSE_OFFERING, nq);
        
        int validCourses = 0;
        //There can be multiple course offerings for a given course, and we just need access to one
        Map<String, Set<String>> courseToCourseOfferings = new HashMap<String, Set<String>>();
        for (Entity courseOffering : coEnts) {
            Set<String> courseOfferings = courseToCourseOfferings.get(courseOffering.getBody().get(ParameterConstants.COURSE_ID));
            if (courseOfferings == null) {
                courseOfferings = new HashSet<String>();
                courseToCourseOfferings.put((String) courseOffering.getBody().get(ParameterConstants.COURSE_ID), courseOfferings);
            }
            courseOfferings.add(courseOffering.getEntityId());
        }
        
        for (String courseId : courseToCourseOfferings.keySet()) {
            if (hasAccessToAtLeastOneCourseOffering(courseToCourseOfferings.get(courseId))) {
                validCourses++;
            } else {
                return false;
            }
        }
        return validCourses == courseIds.size();
    }

    /**
     * Determine which sections are associated with the given course offerings, and validate
     * that the user has access to at least one section for each offering
     * @param courseOfferingIds
     * @return
     */
    private boolean hasAccessToAtLeastOneCourseOffering(Set<String> courseOfferingIds) {
        NeutralQuery nq = new NeutralQuery(
                new NeutralCriteria(ParameterConstants.COURSE_OFFERING_ID, NeutralCriteria.CRITERIA_IN, courseOfferingIds));
        nq.setIncludeFields(Arrays.asList(ParameterConstants.COURSE_OFFERING_ID));
        Iterable<Entity> sectionEnts = getRepo().findAll(EntityNames.SECTION, nq);
        
        //There can be multiple sections for a given course offering, and we just need access to one
        Map<String, Set<String>> courseOfferingsToSections = new HashMap<String, Set<String>>();
        for (Entity section : sectionEnts) {
            Set<String> sections = courseOfferingsToSections.get(section.getBody().get(ParameterConstants.COURSE_OFFERING_ID));
            if (sections == null) {
                sections = new HashSet<String>();
                courseOfferingsToSections.put((String) section.getBody().get(ParameterConstants.COURSE_OFFERING_ID), sections);
            }
            sections.add(section.getEntityId());
        }
        
        for (String sectionId : courseOfferingsToSections.keySet()) {
            if (hasAccessToAtLeastOneSection(courseOfferingsToSections.get(sectionId))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAccessToAtLeastOneSection(Set<String> sectionIds) {
        return sectionValidator.getValid(EntityNames.SECTION, sectionIds).size() > 0;
    }
}
