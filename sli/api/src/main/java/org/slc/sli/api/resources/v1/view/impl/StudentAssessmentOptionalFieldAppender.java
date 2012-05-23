package org.slc.sli.api.resources.v1.view.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppenderHelper;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

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

    public StudentAssessmentOptionalFieldAppender() {
    }

    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities, String parameters) {

        //get the student Ids
        List<String> studentIds = optionalFieldAppenderHelper.getIdList(entities, "id");
        //get the student assessment associations for the students
        List<EntityBody> studentAssessmentAssociations = optionalFieldAppenderHelper.queryEntities(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS,
                ParameterConstants.STUDENT_ID, studentIds);

        //get the assessment ids from the associations
        List<String> assessmentIds = optionalFieldAppenderHelper.getIdList(studentAssessmentAssociations, ParameterConstants.ASSESSMENT_ID);
        //get a list of assessments
        List<EntityBody> assessments = optionalFieldAppenderHelper.queryEntities(ResourceNames.ASSESSMENTS, "_id", assessmentIds);

        for (EntityBody student : entities) {
            //get the student assessment associations for the given student
            List<EntityBody> studentAssessmentAssociationsForStudent = optionalFieldAppenderHelper.getEntitySubList(studentAssessmentAssociations, ParameterConstants.STUDENT_ID,
                    (String) student.get("id"));

            for (EntityBody studentAssessmentAssociation : studentAssessmentAssociationsForStudent) {
                //get the assessment
                EntityBody assessment = optionalFieldAppenderHelper.getEntityFromList(assessments, "id",
                        (String) studentAssessmentAssociation.get(ParameterConstants.ASSESSMENT_ID));

                studentAssessmentAssociation.put(PathConstants.ASSESSMENTS, assessment);
            }

            //add the body to the student
            student.put(PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS, studentAssessmentAssociationsForStudent);
        }

        return entities;
    }


}
