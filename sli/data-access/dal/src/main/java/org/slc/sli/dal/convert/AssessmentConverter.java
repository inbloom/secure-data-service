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
import java.util.List;
import java.util.Map;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * assessment converter that transform assessment superdoc to sli assessment schema
 * 
 * @author Dong Liu dliu@wgen.net
 */

public class AssessmentConverter implements SuperdocConverter {
    
    @Override
    public void subdocToBodyField(Entity entity) {

        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
            if ((entity.getEmbeddedData() != null) && !entity.getEmbeddedData().isEmpty()) {
                for (Map.Entry<String, List<Entity>> enbDocList : entity.getEmbeddedData().entrySet()) {
                    List<Map<String, Object>> subDocbody = new ArrayList<Map<String, Object>>();
                    for (Entity subEntity : enbDocList.getValue()) {
                        // remove the assessmentId field
                        subEntity.getBody().remove("assessmentId");
                        subDocbody.add(subEntity.getBody());
                    }
                    entity.getBody().put(enbDocList.getKey(), subDocbody);
                }
                entity.getEmbeddedData().clear();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
            // convert assessmentitem
            if (entity.getBody().get("assessmentItem") != null) {
                List<Entity> subdocAssessmentItems = new ArrayList<Entity>();
                List<Map<String, Object>> assessmentItems = (List<Map<String, Object>>) entity.getBody().get(
                        "assessmentItem");
                for (Map<String, Object> assessmentItem : assessmentItems) {
                 // TODO generate cat did for assessmentitem
                    MongoEntity subdocAssessmentItem = new MongoEntity("assessmentItem", "assessment_idassessmentItem_id", assessmentItem, null);
                    
                    
                    subdocAssessmentItems.add(subdocAssessmentItem);
                }
                entity.getEmbeddedData().put("assessmentItem", subdocAssessmentItems);
                entity.getBody().remove("assessmentItem");
            }
            // TODO convert objectiveAssessment
            if (entity.getBody().get("objectiveAssessment") != null) {
                // Do something
            }
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
        // TODO Auto-generated method stub
    }
}
