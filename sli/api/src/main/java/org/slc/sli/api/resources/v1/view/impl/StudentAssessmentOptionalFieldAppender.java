package org.slc.sli.api.resources.v1.view.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;
import org.slc.sli.api.resources.v1.view.AbstractOptionalFieldAppender;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Provides data about students and assessments to construct the custom
 * views returned by the api
 * @author srupasinghe
 *
 */
@Component
public class StudentAssessmentOptionalFieldAppender extends AbstractOptionalFieldAppender {

    public StudentAssessmentOptionalFieldAppender() {
    }
    
    @Override
    public List<EntityBody> applyOptionalField(List<EntityBody> entities) {
        
        //get the student Ids
        List<String> studentIds = getIdList(entities, "id");
        //get the student assessment associations for the students
        List<EntityBody> studentAssessmentAssociations = queryEntities(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS,
                ParameterConstants.STUDENT_ID, studentIds);
        
        //get the assessment ids from the associations
        List<String> assessmentIds = getIdList(studentAssessmentAssociations, ParameterConstants.ASSESSMENT_ID);
        //get a list of assessments
        List<EntityBody> assessments = queryEntities(ResourceNames.ASSESSMENTS, "_id", assessmentIds);
        
        for (EntityBody student : entities) {
            //get the student assessment associations for the given student
            List<EntityBody> studentAssessmentAssociationForStudent = getSubList(studentAssessmentAssociations, ParameterConstants.STUDENT_ID, 
                    (String) student.get("id"));
            
            List<EntityBody> assessmentsForStudent = new ArrayList<EntityBody>();
            for (EntityBody studentAssessmentAssociation : studentAssessmentAssociationForStudent) {
                //get the assessment
                EntityBody assessment = getEntityFromList(assessments, "id", 
                        (String) studentAssessmentAssociation.get(ParameterConstants.ASSESSMENT_ID));
                assessmentsForStudent.add(assessment);
            }
            
            //create the map to hold the assessments and the student assessment associations
            EntityBody body = new EntityBody();
            body.put(PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS, studentAssessmentAssociationForStudent);
            body.put(PathConstants.ASSESSMENTS, assessmentsForStudent);
            
            //add the body to the student
            student.put(PathConstants.ASSESSMENTS, body);
        }
        
        return entities;
    }
    
    protected List<EntityBody> queryEntities(String resourceName, String key, List<String> values) {
        
        EntityDefinition entityDef = entityDefs.lookupByResourceName(resourceName);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria(key, NeutralCriteria.CRITERIA_IN, values));
        
        return (List<EntityBody>) entityDef.getService().list(neutralQuery);
    }
    
    protected EntityBody getEntityFromList(List<EntityBody> list, String field, String value) {
        for (EntityBody e : list) {
            if (value.equals(e.get(field))){
                return e;
            }
        }
        
        return null;
    }
    
    protected List<EntityBody> getSubList(List<EntityBody> list, String field, String value) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        
        for (EntityBody e : list) {
            if (value.equals(e.get(field))){
                results.add(e);
            }
        }
        
        return results;
    }
    
    protected List<String> getIdList(List<EntityBody> list, String field) {
        List<String> ids = new ArrayList<String>();
        
        for (EntityBody e : list) {
            ids.add((String) e.get(field));
        }
        
        return ids;
    }
}
