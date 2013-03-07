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


package org.slc.sli.api.resources.v1.view.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;

/**
 * Provides data about students and assessments to construct the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class StudentAssessmentOptionalFieldAppender implements OptionalFieldAppender {

    @Autowired
    private OptionalFieldAppenderHelper optionalFieldAppenderHelper;

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {

        //get the student Ids
        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");
        //get the student assessment associations for the students
        List<EntityBody> studentAssessments = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_ASSESSMENTS,
                ParameterConstants.STUDENT_ID, studentIds);

        //get the assessment ids from the associations
        List<String> assessmentIds = optionalFieldAppenderHelper.getIdList(studentAssessments, ParameterConstants.ASSESSMENT_ID);
        //get a list of assessments
        List<EntityBody> assessments = optionalFieldAppenderHelper.queryEntities(ResourceNames.ASSESSMENTS, "_id", assessmentIds);

        for (EntityBody student : entities) {
            //get the student assessment associations for the given student
            List<EntityBody> studentAssessmentsForStudent = optionalFieldAppenderHelper.getEntitySubList(studentAssessments, ParameterConstants.STUDENT_ID,
                    (String) student.get("id"));

            for (EntityBody studentAssessment : studentAssessmentsForStudent) {
                //get the assessment
                EntityBody assessment = optionalFieldAppenderHelper.getEntityFromList(assessments, "id",
                        (String) studentAssessment.get(ParameterConstants.ASSESSMENT_ID));

                studentAssessment.put(PathConstants.ASSESSMENTS, assessment);
            }

            //add the body to the student
            student.put(PathConstants.STUDENT_ASSESSMENTS, studentAssessmentsForStudent);
        }

        return entities;
    }


}
