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

package org.slc.sli.dal.convert;

import java.util.Arrays;
import java.util.List;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

/**
 * studentAssessment converter that transform studentAssessment superdoc to sli studentAssessment
 * schema
 * 
 * @author Dong Liu dliu@wgen.net
 */
@Component
public class StudentAssessmentConverter extends GenericSuperdocConverter implements SuperdocConverter {
    
    @Override
    public void subdocToBodyField(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.STUDENT_ASSESSMENT)) {
            Entity assessment = retrieveAssessment(entity.getBody().get("assessmentId"));
            
            // replace assessmentItem reference in studentAssessmentItem with actual assessmentItem
            // entity
            referenceResolve(entity, assessment, "studentAssessmentItem", "assessmentItemId", "assessmentItem");
            subdocsToBody(entity, "studentAssessmentItem", Arrays.asList("studentAssessmentId"));
            
            // replace objectiveAssessment reference in studentObjectiveAssessment with actual
            // objectiveAssessment entity
            referenceResolve(entity, assessment, "studentObjectiveAssessment", "objectiveAssessmentId",
                    "objectiveAssessment");
            subdocsToBody(entity, "studentObjectiveAssessment", Arrays.asList("studentAssessmentId"));
            
            entity.getEmbeddedData().clear();
        }
    }

    /*
     * Look inside each "subentityType", use "referenceKey" to lookup the counter part in entity
     * "assessment",
     * then replace the referenceKey with the body of the item from "assessment"
     */
    private void referenceResolve(Entity studentAssessment, Entity assessment, String saSubEntityType,
            String referenceKey, String assmtSubEntityType) {
        if (studentAssessment != null && studentAssessment.getEmbeddedData().get(saSubEntityType) != null
                && studentAssessment.getEmbeddedData().get(saSubEntityType).size() > 0 && assessment != null
                && assessment.getEmbeddedData().get(assmtSubEntityType) != null
                && assessment.getEmbeddedData().get(assmtSubEntityType).size() > 0) {
            List<Entity> saSubEntities = studentAssessment.getEmbeddedData().get(saSubEntityType);
            List<Entity> assessmentSubEntities = assessment.getEmbeddedData().get(assmtSubEntityType);
            for (Entity saSubEntity : saSubEntities) {
                String referenceKeyId = (String) saSubEntity.getBody().remove(referenceKey);
                Entity assessmentSubEntity = getEntityById(assessmentSubEntities, referenceKeyId);
                saSubEntity.getBody().put(assmtSubEntityType, assessmentSubEntity.getBody());
            }
        }
        return;
    }
    
    // TO-DO fill this in
    private Entity retrieveAssessment(Object object) {
        if (!(object instanceof String)) {
            return null;
        }
        String assessmentId = (String) object;
        Entity assessmentEntity = ((MongoEntityRepository) repo).getTemplate().findById(assessmentId, Entity.class,
                EntityNames.ASSESSMENT);

        return assessmentEntity;
    }
    
    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void subdocToBodyField(Iterable<Entity> entities) {
        if (entities != null) {
            for (Entity entity : entities) {
                subdocToBodyField(entity);
            }
        }
    }

    @Override
    public void bodyFieldToSubdoc(Iterable<Entity> entities) {
        // TODO Auto-generated method stub
        
    }
    
    private Entity getEntityById(List<Entity> entities, String id) {
        if (entities != null && entities.size() > 0) {
            for (Entity entity : entities) {
                if (entity.getEntityId().equals(id)) {
                    return entity;
                }
            }
        }
        return null;
    }

}
