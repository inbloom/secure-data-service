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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
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

    private static final String OBJECTIVE_ASSESSSMENT_ID = "identificationCode";
    private static final String SUB_OBJECTIVE_ASSESSMENT_HIERARCHY = "objectiveAssessments";
    private static final String SUB_OBJECTIVE_ASSESSMENT_REFS = "subObjectiveAssessment";
    private static final String ASSESSMENT_PERIOD_DESCRIPTOR_ID = "assessmentPeriodDescriptorId";
    
    protected static final String ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE = "assessmentFamilyReference";
    protected static final String ASSESSMENT_FAMILY_ASSESSMENT_FAMILY_REFERENCE = "assessmentFamilyReference";
    protected static final String ASSESSMENT_FAMILY_HIERARCHY_STRING = "assessmentFamilyHierarchyName";
    protected static final String ASSESSMENT_FAMILY_TITLE = "assessmentFamilyTitle";

    @Override
    public void subdocToBodyField(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
            transformToHierarchy(entity);
            subdocsToBody(entity, "assessmentItem", "assessmentItem", Arrays.asList("assessmentId"));
            subdocsToBody(entity, "objectiveAssessment", "objectiveAssessment", Arrays.asList("assessmentId"));
            entity.getEmbeddedData().clear();
            collapseAssessmentPeriodDescriptor(entity);
            addFamilyHierarchy(entity);
        }
    }

    private void collapseAssessmentPeriodDescriptor(Entity entity) {
        Object apdId = entity.getBody().remove(ASSESSMENT_PERIOD_DESCRIPTOR_ID); 
        if (apdId != null && apdId instanceof String) {
            Entity apd = retrieveAccessmentPeriodDecriptor((String) apdId);
            if (apd != null) {
                entity.getBody().put("assessmentPeriodDescriptor", apd.getBody());
            }
        }
    }

    private Entity retrieveAccessmentPeriodDecriptor(String apdId) {
        Entity apd = ((MongoEntityRepository) repo).getTemplate().findById(apdId, Entity.class,
                EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR);

        return apd;
    }

    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
            //objectiveAssessment
            String parentKey = generateDid(entity);
            @SuppressWarnings("unchecked")
            List<Entity> objectiveAssessments = transformFromHierarchy((List<Map<String, Object>>) entity.getBody()
                    .get("objectiveAssessment"), parentKey);

            entity.getBody().remove("objectiveAssessment");
            entity.getEmbeddedData().put("objectiveAssessment", objectiveAssessments);
            
            //assessmentItem
            List<Map<String, Object>> assessmentItems = new ArrayList<Map<String, Object>>();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topLevels = (List<Map<String, Object>>) entity.getBody().remove("assessmentItem");
            if (topLevels != null) {
                assessmentItems.addAll(topLevels);
                assessmentItems.addAll(extractAssessmentItems(objectiveAssessments, parentKey));
            }
            makeSubDocs(entity, "assessmentItem", "assessmentId", assessmentItems);
           
            //assessmentPeriodDescriptor
            Object assessmentPeriodDescrptor = entity.getBody().remove(EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR);
            if (assessmentPeriodDescrptor instanceof Map<?,?>) {
                //update embedded assessmentPeriodDescriptor
                @SuppressWarnings("unchecked")
                String did = generateSubdocDid((Map<String, Object>) assessmentPeriodDescrptor, EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR);
                @SuppressWarnings("unchecked")
                MongoEntity apdEntity = new MongoEntity(EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR, did, (Map<String, Object>) assessmentPeriodDescrptor, null);
                if (repo.update(EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR, apdEntity, false)) {
                    //only record the id if it was successfully updated
                    entity.getBody().put(ASSESSMENT_PERIOD_DESCRIPTOR_ID, did);
                }
            }
            
            //assessmentFamilyHierarchy is ignored on create/update
            entity.getBody().remove(ASSESSMENT_FAMILY_HIERARCHY_STRING);
        }
    }

    public List<Map<String, Object>> extractAssessmentItems(List<Entity> objectiveAssessments, String parentKey) {
        List<Map<String, Object>> assessmentItems = new ArrayList<Map<String, Object>>();
        for (Entity objectiveAssessment : objectiveAssessments) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> objectiveItems = (List<Map<String, Object>>) objectiveAssessment.getBody()
                    .remove("assessmentItem");
            if (objectiveItems != null) {
                assessmentItems.addAll(objectiveItems);
                List<String> refs = makeAssessmentItemRefs(objectiveItems, parentKey);
                objectiveAssessment.getBody().put("assessmentItemRefs", refs);
            }
        }
        return assessmentItems;
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
    
    /**
     * Construct the assessmentFamilyHierarchy string from the assessment family reference
     */
    private void addFamilyHierarchy(Entity entity) {
        Object object = entity.getBody().remove(ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE);
        if (object == null || !(object instanceof String)) {
            // we don't validate assessmentFamilyHierarchy any more, so someone could have passed in 
            // an object, array, number, etc.
            return;
        }
        String familyRef = (String) object;
        MongoTemplate mongo = ((MongoEntityRepository) repo).getTemplate();
        Entity family = mongo.findById(familyRef, Entity.class, EntityNames.ASSESSMENT_FAMILY);
        
        List<String> familyTitles = new LinkedList<String>();
        while (family != null) {
            familyTitles.add((String)family.getBody().get(ASSESSMENT_FAMILY_TITLE));
            String parentRef = (String)family.getBody().get(ASSESSMENT_FAMILY_ASSESSMENT_FAMILY_REFERENCE);
            family = null;
            if (parentRef != null) {
                family = mongo.findById(parentRef, Entity.class, EntityNames.ASSESSMENT_FAMILY);
            }
        }
        
        String familyHierarchyString = Joiner.on(".").join(Lists.reverse(familyTitles));
        entity.getBody().put(ASSESSMENT_FAMILY_HIERARCHY_STRING, familyHierarchyString);
    }

    /**
     * Transform the entity from the flat subdoc representation to one that shows nested objective
     * assessments and assessment items in a hierarchical fashion
     *
     * @param entity
     * @return
     */
    private Entity transformToHierarchy(Entity entity) {
        List<Entity> oas = entity.getEmbeddedData().get("objectiveAssessment");
        Map<String, Entity> items = getAssessmentItemMap(entity.getEmbeddedData().get("assessmentItem"));
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
                            actual.remove("assessmentId");
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
            entity.getEmbeddedData().put("objectiveAssessment", transformed);
            entity.getEmbeddedData().put("assessmentItem", new ArrayList<Entity>(items.values()));
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
        List<?> itemRefs = (List<?>) oa.getBody().remove("assessmentItemRefs");
        if (itemRefs != null) {
            List<Map<String, Object>> resolvedItems = new ArrayList<Map<String, Object>>();
            for (Object ref : itemRefs) {
                Entity resolved = items.remove(ref);
                if (resolved != null) {
                    Map<String, Object> item = resolved.getBody();
                    item.remove("_id");
                    item.remove("assessmentId");
                    resolvedItems.add(item);
                }
            }
            oa.getBody().put("assessmentItem", resolvedItems);
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
        map.put("assessmentId", parentKey);
        String did = generateSubdocDid(map, "objectiveAssessment");
        return new MongoEntity("objectiveAssessment", did, map, null);
    }

    private List<String> makeAssessmentItemRefs(List<Map<String, Object>> items, String parentKey) {
        List<String> refs = new ArrayList<String>(items.size());
        for (Map<String, Object> item : items) {
            item.put("assessmentId", parentKey);
            refs.add(generateSubdocDid(item, "assessmentItem"));
        }
        return refs;
    }

}
