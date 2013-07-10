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
import static org.slc.sli.common.constants.EntityNames.ASSESSMENT_FAMILY;
import static org.slc.sli.common.constants.EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR;
import static org.slc.sli.common.constants.EntityNames.OBJECTIVE_ASSESSMENT;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_FAMILY_HIERARCHY;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_FAMILY_REFERENCE;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_FAMILY_TITLE;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_ID;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_ITEM;
import static org.slc.sli.common.constants.ParameterConstants.ASSESSMENT_PERIOD_DESCRIPTOR_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.common.base.Joiner;
import org.elasticsearch.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * assessment converter that transform assessment superdoc to sli assessment schema
 *
 * @author Dong Liu dliu@wgen.net
 */
@Component
public class AssessmentConverter extends GenericSuperdocConverter implements SuperdocConverter {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentConverter.class);

    private static final String OBJECTIVE_ASSESSSMENT_ID = "identificationCode";
    private static final String SUB_OBJECTIVE_ASSESSMENT_HIERARCHY = "objectiveAssessments";

    protected static final String ASSESSMENT_FAMILY_ASSESSMENT_FAMILY_REFERENCE = "assessmentFamilyReference";
    protected static final String SUB_OBJECTIVE_ASSESSMENT_REFS = "subObjectiveAssessment";
    protected static final String ASSESSMENT_ITEM_REFS = "assessmentItemRefs";

    @Override
    public Entity subdocToBodyField(Entity entity) {
        if (entity != null && entity.getType().equals(ASSESSMENT)) {
            transformToHierarchy(entity);
            subdocsToBody(entity, ASSESSMENT_ITEM, ASSESSMENT_ITEM, Arrays.asList(ASSESSMENT_ID));
            subdocsToBody(entity, OBJECTIVE_ASSESSMENT, OBJECTIVE_ASSESSMENT, Arrays.asList(ASSESSMENT_ID));
            entity.getEmbeddedData().clear();
            collapseAssessmentPeriodDescriptor(entity);
            addFamilyHierarchy(entity);
        }
        return entity;
    }

    private void collapseAssessmentPeriodDescriptor(Entity entity) {
        Object apdId = entity.getBody().remove(ASSESSMENT_PERIOD_DESCRIPTOR_ID);
        if (apdId != null && apdId instanceof String) {
            Entity apd = retrieveAccessmentPeriodDecriptor((String) apdId);
            if (apd != null) {
                entity.getBody().put(ASSESSMENT_PERIOD_DESCRIPTOR, apd.getBody());
            }
        }
    }

    private Entity retrieveAccessmentPeriodDecriptor(String apdId) {
        Entity apd = ((MongoEntityRepository) repo).getTemplate().findById(apdId, Entity.class,
                ASSESSMENT_PERIOD_DESCRIPTOR);

        return apd;
    }

    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        bodyFieldToSubdoc(entity, null);
    }

    @Override
    public void bodyFieldToSubdoc(Entity entity, SuperdocConverter.Option option) {
        if (entity != null && entity.getType().equals(ASSESSMENT)) {
            //objectiveAssessment
            String parentKey = generateDid(entity);
            @SuppressWarnings("unchecked")
            List<Entity> objectiveAssessments = transformFromHierarchy((List<Map<String, Object>>) entity.getBody()
                    .get(OBJECTIVE_ASSESSMENT), parentKey);

            entity.getBody().remove(OBJECTIVE_ASSESSMENT);
            entity.getEmbeddedData().put(OBJECTIVE_ASSESSMENT, objectiveAssessments);

            //assessmentItem
            List<Map<String, Object>> assessmentItems = new ArrayList<Map<String, Object>>();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topLevels = (List<Map<String, Object>>) entity.getBody().remove(ASSESSMENT_ITEM);
            if (topLevels != null) {
                assessmentItems.addAll(topLevels);
                assessmentItems.addAll(extractAssessmentItems(objectiveAssessments, parentKey));
            }
            makeSubDocs(entity, ASSESSMENT_ITEM, ASSESSMENT_ID, assessmentItems);

            //assessmentPeriodDescriptor
            Object assessmentPeriodDescriptor = entity.getBody().remove(ASSESSMENT_PERIOD_DESCRIPTOR);
            if (assessmentPeriodDescriptor instanceof Map<?,?>) {
                //update embedded assessmentPeriodDescriptor
                @SuppressWarnings("unchecked")
                String did = generateSubdocDid((Map<String, Object>) assessmentPeriodDescriptor, ASSESSMENT_PERIOD_DESCRIPTOR);
                @SuppressWarnings("unchecked")
                MongoEntity apdEntity = new MongoEntity(ASSESSMENT_PERIOD_DESCRIPTOR, did, (Map<String, Object>) assessmentPeriodDescriptor, null);
                if (repo.update(ASSESSMENT_PERIOD_DESCRIPTOR, apdEntity, false)) {
                    //only record the id if it was successfully updated
                    entity.getBody().put(ASSESSMENT_PERIOD_DESCRIPTOR_ID, did);
                }
            }

            fixAssessmentFamilyReference(entity, option);
        }
    }

    public List<Map<String, Object>> extractAssessmentItems(List<Entity> objectiveAssessments, String parentKey) {
        List<Map<String, Object>> assessmentItems = new ArrayList<Map<String, Object>>();
        for (Entity objectiveAssessment : objectiveAssessments) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> objectiveItems = (List<Map<String, Object>>) objectiveAssessment.getBody()
                    .remove(ASSESSMENT_ITEM);
            if (objectiveItems != null) {
                assessmentItems.addAll(objectiveItems);
                List<String> refs = makeAssessmentItemRefs(objectiveItems, parentKey);
                objectiveAssessment.getBody().put(ASSESSMENT_ITEM_REFS, refs);
            }
        }
        return assessmentItems;
    }

    @Override
    public Iterable<Entity> subdocToBodyField(Iterable<Entity> entities) {
        List<Entity> converted = new ArrayList<Entity>();
        if (entities != null) {
            for (Entity entity : entities) {
                converted.add(subdocToBodyField(entity));
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

    /**
     * On update, AssessmentFamilyReference will not exist on the entity. Fetch it from
     * the db and re-add it to the entity.
     */
    private void fixAssessmentFamilyReference(Entity entity, SuperdocConverter.Option option) {
        //assessmentFamilyHierarchy is ignored on create/update
        Object assessmentFamilyHierarchy = entity.getBody().remove(ASSESSMENT_FAMILY_HIERARCHY);

        // There is a requirement that assessment.assessmentFamilyReference be READONLY via the API
        // so only add assessmentFamilyReference back if the delete option is NOT set
        if (option == null || Option.DELETE_ASSESSMENT_FAMILY_REFERENCE != option) {
            MongoTemplate mongo = ((MongoEntityRepository) repo).getTemplate();
            Entity existingAssessment = mongo.findById(entity.getEntityId(), Entity.class, ASSESSMENT);
            if (existingAssessment == null) {
                return;
            }
            if (existingAssessment.getBody().containsKey(ASSESSMENT_FAMILY_REFERENCE)) {
                String assessmentFamilyRef = (String) existingAssessment.getBody().get(ASSESSMENT_FAMILY_REFERENCE);
                entity.getBody().put(ASSESSMENT_FAMILY_REFERENCE, assessmentFamilyRef);
            }
        }
    }

    /**
     * Construct the assessmentFamilyHierarchy string from the assessment family reference
     */
    private void addFamilyHierarchy(Entity entity) {
        Object object = entity.getBody().remove(ASSESSMENT_FAMILY_REFERENCE);
        if (object == null || !(object instanceof String)) {
            // we don't validate assessmentFamilyHierarchy any more, so someone could have passed in
            // an object, array, number, etc.
            return;
        }
        String familyRef = (String) object;
        MongoTemplate mongo = ((MongoEntityRepository) repo).getTemplate();
        Entity family = mongo.findById(familyRef, Entity.class, ASSESSMENT_FAMILY);

        List<String> familyTitles = new LinkedList<String>();
        Set<String> seenFamilyRefs = new HashSet<String>();
        seenFamilyRefs.add(familyRef);
        while (family != null) {
            String familyTitle = (String)family.getBody().get(ASSESSMENT_FAMILY_TITLE);
            if (familyTitle == null) {
                LOG.error("Required assessmentFamilyTitle is null for assessmentFamily with _id : {}", new Object[] {family.getEntityId()});
                break;
            }
            familyTitles.add(familyTitle);
            String parentRef = (String)family.getBody().get(ASSESSMENT_FAMILY_ASSESSMENT_FAMILY_REFERENCE);
            family = null;

            if (parentRef != null) {
                if (!seenFamilyRefs.contains(parentRef)) {
                    family = mongo.findById(parentRef, Entity.class, ASSESSMENT_FAMILY);
                    seenFamilyRefs.add(parentRef);
                } else {
                    LOG.error("Circular reference detected in assessment family hierarchy. _id : {} occurs twice in hierarchy.", new Object[] { parentRef });
                }
            }
        }

        String familyHierarchyString = Joiner.on(".").join(Lists.reverse(familyTitles));
        entity.getBody().put(ASSESSMENT_FAMILY_HIERARCHY, familyHierarchyString);
    }

    /**
     * Transform the entity from the flat subdoc representation to one that shows nested objective
     * assessments and assessment items in a hierarchical fashion
     *
     * @param entity
     * @return
     */
    private Entity transformToHierarchy(Entity entity) {
        List<Entity> oas = entity.getEmbeddedData().get(OBJECTIVE_ASSESSMENT);
        Map<String, Entity> items = getAssessmentItemMap(entity.getEmbeddedData().get(ASSESSMENT_ITEM));
        if (oas != null) {
            Map<String, Entity> allOAs = new LinkedHashMap<String, Entity>();
            for (Entity oa : oas) {
                addEmbeddedAssessmentItems(items, oa);
                allOAs.put(oa.getBody().get(OBJECTIVE_ASSESSSMENT_ID).toString(), oa);
            }
            Set<String> topLevelOAs = new HashSet<String>(allOAs.keySet());
            for (Entry<String, Entity> oa : allOAs.entrySet()) {
                @SuppressWarnings("unchecked")
                List<String> subOAs = (List<String>) oa.getValue().getBody().get(SUB_OBJECTIVE_ASSESSMENT_REFS);
                if (subOAs != null) {
                    List<Map<String, Object>> actuals = new ArrayList<Map<String, Object>>(subOAs.size());
                    for (String ref : subOAs) {
                        if (topLevelOAs.remove(ref)) {
                            Map<String, Object> actual = allOAs.get(ref).getBody();
                            actual.remove(ASSESSMENT_ID);
                            actuals.add(actual);
                        }
                    }
                    oa.getValue().getBody().put(SUB_OBJECTIVE_ASSESSMENT_HIERARCHY, actuals);
                    oa.getValue().getBody().remove(SUB_OBJECTIVE_ASSESSMENT_REFS);
                }
            }
            List<Entity> transformed = new ArrayList<Entity>(topLevelOAs.size());
            for (String id : topLevelOAs) {
                transformed.add(allOAs.get(id));
            }
            entity.getEmbeddedData().put(OBJECTIVE_ASSESSMENT, transformed);
            entity.getEmbeddedData().put(ASSESSMENT_ITEM, new ArrayList<Entity>(items.values()));
        }
        return entity;
    }

    /**
     * Adds in any embedded assessment items to the objective assessment entity
     * Any assessment items added are also removed from the map of items so they won't show up again
     *
     * @param items
     * @param oa
     */
    private void addEmbeddedAssessmentItems(Map<String, Entity> items, Entity oa) {
        List<?> itemRefs = (List<?>) oa.getBody().remove(ASSESSMENT_ITEM_REFS);
        if (itemRefs != null) {
            List<Map<String, Object>> resolvedItems = new ArrayList<Map<String, Object>>();
            for (Object ref : itemRefs) {
                Entity resolved = items.remove(ref);
                if (resolved != null) {
                    Map<String, Object> item = resolved.getBody();
                    item.remove("_id");
                    item.remove(ASSESSMENT_ID);
                    resolvedItems.add(item);
                }
            }
            oa.getBody().put(ASSESSMENT_ITEM, resolvedItems);
        }
    }

    private Map<String, Entity> getAssessmentItemMap(List<Entity> items) {
        if (items == null) {
            return Collections.emptyMap();
        }
        Map<String, Entity> map = new HashMap<String, Entity>(items.size());
        for (Entity item : items) {
            map.put(item.getEntityId(), item);
        }
        return map;
    }

    private List<Entity> transformFromHierarchy(List<Map<String, Object>> originals, String parentKey) {
        List<Entity> oas = new ArrayList<Entity>();
        if (originals != null) {
            for (Map<String, Object> original : originals) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> subs = (List<Map<String, Object>>) original.get(SUB_OBJECTIVE_ASSESSMENT_HIERARCHY);
                Entity oa = makeOAEntity(original, parentKey);
                if (subs != null && !subs.isEmpty()) {
                    oas.addAll(transformFromHierarchy(subs, parentKey));
                    oa.getBody().put(SUB_OBJECTIVE_ASSESSMENT_REFS, makeOARefs(subs));
                    original.remove(SUB_OBJECTIVE_ASSESSMENT_HIERARCHY);
                }
                oas.add(oa);
            }
        }
        return oas;
    }

    private List<String> makeOARefs(List<Map<String, Object>> oas) {
        List<String> refs = new ArrayList<String>(oas.size());
        for (Map<String, Object> oa : oas) {
            refs.add(oa.get(OBJECTIVE_ASSESSSMENT_ID).toString());
        }
        return refs;
    }

    private Entity makeOAEntity(Map<String, Object> map, String parentKey) {
        map.put(ASSESSMENT_ID, parentKey);
        String did = generateSubdocDid(map, OBJECTIVE_ASSESSMENT);
        return new MongoEntity(OBJECTIVE_ASSESSMENT, did, map, null);
    }

    private List<String> makeAssessmentItemRefs(List<Map<String, Object>> items, String parentKey) {
        List<String> refs = new ArrayList<String>(items.size());
        for (Map<String, Object> item : items) {
            item.put(ASSESSMENT_ID, parentKey);
            refs.add(generateSubdocDid(item, ASSESSMENT_ITEM));
        }
        return refs;
    }

}
