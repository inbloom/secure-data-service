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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            referenceEntityResolve(entity, assessment, "studentAssessmentItem", "assessmentItemId", "assessmentItem");
            subdocsToBody(entity, "studentAssessmentItem", "studentAssessmentItems",
                    Arrays.asList("studentAssessmentId"));
            
            // replace objectiveAssessment reference in studentObjectiveAssessment with actual
            // objectiveAssessment entity
            referenceEntityResolve(entity, assessment, "studentObjectiveAssessment", "objectiveAssessmentId",
                    "objectiveAssessment");
            subdocsToBody(entity, "studentObjectiveAssessment", "studentObjectiveAssessments",
                    Arrays.asList("studentAssessmentId"));
            
            entity.getEmbeddedData().clear();
        }
    }

    /*
     * Look inside each "subentityType", use "referenceKey" to lookup the counter part in entity
     * "assessment",
     * then replace the referenceKey with the body of the item from "assessment"
     */
    private void referenceEntityResolve(Entity studentAssessment, Entity assessment, String saSubEntityType,
            String referenceKey, String assmtSubEntityType) {
        if (studentAssessment != null && studentAssessment.getEmbeddedData().get(saSubEntityType) != null
                && studentAssessment.getEmbeddedData().get(saSubEntityType).size() > 0 && assessment != null
                && assessment.getEmbeddedData().get(assmtSubEntityType) != null
                && assessment.getEmbeddedData().get(assmtSubEntityType).size() > 0) {
            List<Entity> saSubEntities = studentAssessment.getEmbeddedData().get(saSubEntityType);
            List<Entity> assessmentSubEntities = assessment.getEmbeddedData().get(assmtSubEntityType);
            List<Entity> saConvertedSubEntities = new ArrayList<Entity>();
            
            Map<String, Entity> assessmentSubEntityMap = buildAssessmentSubEntitiesMap(assessmentSubEntities);
            for (Entity saSubEntity : saSubEntities) {
                String referenceKeyId = (String) saSubEntity.getBody().remove(referenceKey);
                Entity assessmentSubEntity = getEntityById(assessmentSubEntityMap, referenceKeyId);
                if (assessmentSubEntity != null) {
                	saSubEntity.getBody().put(assmtSubEntityType, assessmentSubEntity.getBody());
                	saConvertedSubEntities.add(saSubEntity);
                }
            }
            
            studentAssessment.getEmbeddedData().remove(saSubEntityType);
            if (!saConvertedSubEntities.isEmpty()) {
            	studentAssessment.getEmbeddedData().put(saSubEntityType, saConvertedSubEntities);
            }
        }
    }
    
    private Map<String, Entity> buildAssessmentSubEntitiesMap(List<Entity> assessmentSubEntities) {
    	Map<String, Entity> result = new HashMap<String, Entity>();
    	for (Entity e : assessmentSubEntities) {
    		result.put(e.getEntityId(), e);
    	}
		return result;
	}

	/*
     * Look inside each subEntity(studentObjectiveAssessment and studentAssessmentItem) in
     * studentAssessment, replace refEntity(objectiveAssessment and assessmentItem) to
     * refId(objectiveAssessmentId and assessmentItemId)
     */
    @SuppressWarnings("unchecked")
    private void referenceIdResolve(Entity studentAssessment, String inBodyFieldName, String refEntityType,
            String refEntityKey) {
        if (studentAssessment != null && studentAssessment.getBody().get(inBodyFieldName) != null
                && ((List<Map<String, Object>>) (studentAssessment.getBody().get(inBodyFieldName))).size() > 0) {
            List<Map<String, Object>> subEntityBodies = (List<Map<String, Object>>) studentAssessment.getBody().get(
                    inBodyFieldName);
            for (Map<String, Object> subEntityBody : subEntityBodies) {
                Map<String, Object> refEntityBody = (Map<String, Object>) subEntityBody.get(refEntityType);
                // generate Did for entity(assessmentItem and objectiveAssessment) that is collapsed
                // into subEntity(studentAssessmentItem and studentObjectiveAssessment) which is in
                // superdoc studentAssessment
                String refEntityDid = generateSubdocDid(refEntityBody, refEntityType);
                // remove the ref entity (objectiveAssessment and assessmentItem) from subEntity
                // (studentObjectiveAssessment and studentAssessmentItem)
                subEntityBody.remove(refEntityType);
                // add the refKey(objectiveAssessmentId and assessmentItemId) and refId into
                // subEntity (studentObjectiveAssessment and studentAssessmentItem)
                subEntityBody.put(refEntityKey, refEntityDid);
            }
        }
        return;
    }
    
    // look up in mongo to retrieve assessment entity
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
        if (entity != null && entity.getType().equals(EntityNames.STUDENT_ASSESSMENT)) {
            referenceIdResolve(entity, "studentAssessmentItems", "assessmentItem", "assessmentItemId");
            bodyToSubdocs(entity, "studentAssessmentItem", "studentAssessmentItems", "studentAssessmentId");
            
            referenceIdResolve(entity, "studentObjectiveAssessments", "objectiveAssessment", "objectiveAssessmentId");
            bodyToSubdocs(entity, "studentObjectiveAssessment", "studentObjectiveAssessments", "studentAssessmentId");
        }
        
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
        if (entities != null) {
            for (Entity entity : entities) {
                bodyFieldToSubdoc(entity);
            }
        }
    }
    
    private Entity getEntityById(Map<String, Entity> assessmentSubEntityMap, String id) {
        if (assessmentSubEntityMap != null && assessmentSubEntityMap.containsKey(id)) {
        	return assessmentSubEntityMap.get(id);
        }
        return null;
    }

}
