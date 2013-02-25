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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * assessment converter that transform assessment superdoc to sli assessment schema
 *
 * @author Dong Liu dliu@wgen.net
 */
@Component
public class AssessmentConverter extends GenericSuperdocConverter implements SuperdocConverter {

    private static final String OA_ID = "identificationCode";
    private static final String SUB_OA_HIERARCHY = "objectiveAssessments";
    private static final String SUB_OA_REFS = "subObjectiveAssessment";

    @Override
    public void subdocToBodyField(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
            List<Entity> objectiveAssessments = transformToHierarchy(entity.getEmbeddedData()
                    .get("objectiveAssessment"));
            entity.getEmbeddedData().put("objectiveAssessment", objectiveAssessments);
            subdocsToBody(entity, "assessmentItem", "assessmentItem", Arrays.asList("assessmentId"));
            subdocsToBody(entity, "objectiveAssessment", "objectiveAssessment", Arrays.asList("assessmentId"));
            entity.getEmbeddedData().clear();
        }
    }

    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
            List<Entity> objectiveAssessments = transformFromHierarchy((List<Map<String, Object>>) entity.getBody()
                    .get("objectiveAssessment"));
            entity.getEmbeddedData().put("objectiveAssessment", objectiveAssessments);
            bodyToSubdocs(entity, "assessmentItem", "assessmentItem", "assessmentId");
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

    private List<Entity> transformToHierarchy(List<Entity> oas) {
        if (oas == null) {
            return null;
        }
        Map<String, Entity> allOAs = new LinkedHashMap<String, Entity>();
        for (Entity oa : oas) {
            allOAs.put(oa.getBody().get(OA_ID).toString(), oa);
        }
        Set<String> topLevelOAs = new HashSet<String>(allOAs.keySet());
        for (Entry<String, Entity> oa : allOAs.entrySet()) {
            @SuppressWarnings("unchecked")
            List<String> subOAs = (List<String>) oa.getValue().getBody().get(SUB_OA_REFS);
            if (subOAs != null) {
                List<Map<String, Object>> actuals = new ArrayList<Map<String, Object>>(subOAs.size());
                for (String ref : subOAs) {
                    if (topLevelOAs.remove(ref)) {
                        Map<String, Object> actual = allOAs.get(ref).getBody();
                        actual.remove("assessmentId");
                        actuals.add(actual);
                    }
                }
                oa.getValue().getBody().put(SUB_OA_HIERARCHY, actuals);
                oa.getValue().getBody().remove(SUB_OA_REFS);
            }
        }
        List<Entity> transformed = new ArrayList<Entity>(topLevelOAs.size());
        for (String id : topLevelOAs) {
            transformed.add(allOAs.get(id));
        }
        return transformed;
    }

    private List<Entity> transformFromHierarchy(List<Map<String, Object>> originals) {
        List<Entity> oas = new ArrayList<Entity>();
        if (originals != null) {
            for (Map<String, Object> original : originals) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> subs = (List<Map<String, Object>>) original.get(SUB_OA_HIERARCHY);
                Entity oa = makeOAEntity(original);
                if (subs != null && !subs.isEmpty()) {
                    oas.addAll(transformFromHierarchy(subs));
                    oa.getBody().put(SUB_OA_REFS, makeOARefs(subs));
                    original.remove(SUB_OA_HIERARCHY);
                }
                oas.add(oa);
            }
        }
        return oas;
    }

    private List<String> makeOARefs(List<Map<String, Object>> oas) {
        List<String> refs = new ArrayList<String>(oas.size());
        for (Map<String, Object> oa : oas) {
            refs.add(oa.get(OA_ID).toString());
        }
        return refs;
    }

    private Entity makeOAEntity(Map<String, Object> map) {
        return new MongoEntity("objectiveAssessment", map.get(OA_ID).toString(), map, null);
    }
}
