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

import static org.slc.sli.common.constants.EntityNames.ASSESSMENT;
import static org.slc.sli.common.constants.EntityNames.OBJECTIVE_ASSESSMENT;
import static org.slc.sli.common.constants.EntityNames.STUDENT_ASSESSMENT;
import static org.slc.sli.common.constants.EntityNames.STUDENT_OBJECTIVE_ASSESSMENT;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_ID;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_ITEM;
import static org.slc.sli.common.constants.ParameterConstants.STUDENT_ASSESSMENT_ITEM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;

/**
 * studentAssessment converter that transform studentAssessment superdoc to sli studentAssessment
 * schema
 *
 * @author Dong Liu dliu@wgen.net
 */
@Component
public class StudentAssessmentConverter extends GenericSuperdocConverter implements SuperdocConverter {

    private static final String OBJECTIVE_ASSESSMENT_ID = "objectiveAssessmentId";
    private static final String STUDENT_ASSESSMENT_ID = "studentAssessmentId";
    private static final String ASSESSMENT_ITEM_ID = "assessmentItemId";

    @Override
    public Entity subdocToBodyField(Entity entity) {
        if (entity != null && entity.getType().equals(STUDENT_ASSESSMENT)) {
            if (entity.getBody() == null || entity.getBody().isEmpty()) {
                return null;
            }
            Entity assessment = retrieveAssessment(entity.getBody().get(ASSESSMENT_ID));

            // replace assessmentItem reference in studentAssessmentItem with actual assessmentItem
            // entity
            referenceEntityResolve(entity, assessment, STUDENT_ASSESSMENT_ITEM, ASSESSMENT_ITEM_ID, ASSESSMENT_ITEM);
            subdocsToBody(entity, STUDENT_ASSESSMENT_ITEM, "studentAssessmentItems",
                    Arrays.asList(STUDENT_ASSESSMENT_ID));

            // replace objectiveAssessment reference in studentObjectiveAssessment with actual
            // objectiveAssessment entity
            referenceEntityResolve(entity, assessment, STUDENT_OBJECTIVE_ASSESSMENT, OBJECTIVE_ASSESSMENT_ID,
                    OBJECTIVE_ASSESSMENT);
            subdocsToBody(entity, STUDENT_OBJECTIVE_ASSESSMENT, "studentObjectiveAssessments",
                    Arrays.asList(STUDENT_ASSESSMENT_ID));

            entity.getEmbeddedData().clear();
        }
        return entity;
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
                    // remove the subObjectiveAssessment
                    if (assmtSubEntityType.equals(OBJECTIVE_ASSESSMENT)) {
                        assessmentSubEntity.getBody().remove(AssessmentConverter.SUB_OBJECTIVE_ASSESSMENT_REFS);
                    }
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

                // also need to put in the parent key for the refEntity so it will generate the same did
                refEntityBody.put(ASSESSMENT_ID, studentAssessment.getBody().get(ASSESSMENT_ID));

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
    protected Entity retrieveAssessment(Object object) {
        if (!(object instanceof String) || repo == null) {
            return null;
        }
        String assessmentId = (String) object;
        Entity assessmentEntity = ((MongoEntityRepository) repo).getTemplate().findById(assessmentId, Entity.class,
                ASSESSMENT);

        return assessmentEntity;
    }

    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        bodyFieldToSubdoc(entity, null);
    }

    @Override
    public void bodyFieldToSubdoc(Entity entity, SuperdocConverter.Option option) {
        if (entity != null && entity.getType().equals(STUDENT_ASSESSMENT)) {
            referenceIdResolve(entity, "studentAssessmentItems", ASSESSMENT_ITEM, ASSESSMENT_ITEM_ID);
            bodyToSubdocs(entity, STUDENT_ASSESSMENT_ITEM, "studentAssessmentItems", STUDENT_ASSESSMENT_ID);

            referenceIdResolve(entity, "studentObjectiveAssessments", OBJECTIVE_ASSESSMENT, OBJECTIVE_ASSESSMENT_ID);
            bodyToSubdocs(entity, STUDENT_OBJECTIVE_ASSESSMENT, "studentObjectiveAssessments", STUDENT_ASSESSMENT_ID);
        }

    }

    @Override
    public Iterable<Entity> subdocToBodyField(Iterable<Entity> entities) {
        List<Entity> converted = new ArrayList<Entity>();
        if (entities != null) {
            for (Entity entity : entities) {
                Entity newEntity = subdocToBodyField(entity);
                if(newEntity != null) {
                    converted.add(newEntity);
                }
            }
        }
        return converted;
    }

    @Override
    public void bodyFieldToSubdoc(Iterable<Entity> entities) {
        bodyFieldToSubdoc(entities, null);
    }

    @Override
    public void bodyFieldToSubdoc(Iterable<Entity> entities, SuperdocConverter.Option option) {
        if (entities != null) {
            for (Entity entity : entities) {
                bodyFieldToSubdoc(entity, option);
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
