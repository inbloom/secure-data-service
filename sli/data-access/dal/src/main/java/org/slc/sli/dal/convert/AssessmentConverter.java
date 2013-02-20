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
            subdocsToBody(entity, "assessmentItem", Arrays.asList("assessmentId")); 
            subdocsToBody(entity, "objectiveAssessment", Arrays.asList("assessmentId")); 
            entity.getEmbeddedData().clear();
        }
    }
   
    private Entity subdocsToBody(Entity parent, String subentityType, List<String> removeFields) {
    	if (parent.getEmbeddedData() == null || parent.getEmbeddedData().isEmpty()) {
    		return parent;
    	}
    	
    	List<Entity> subdocs = parent.getEmbeddedData().remove(subentityType);
    	if (subdocs == null || subdocs.isEmpty()) {
    		return parent;
    	}
    	
    	List<Map<String, Object>> subdocBody = new ArrayList<Map<String, Object>>();
    	for (Entity e : subdocs) {
    		for (String field : removeFields) {
    			e.getBody().remove(field);
    		}
    		subdocBody.add(e.getBody());
    	}
    	parent.getBody().put(subentityType, subdocBody);
    	
    	return parent;
    } 
    
    @Override
    public void bodyFieldToSubdoc(Entity entity) {
        if (entity != null && entity.getType().equals(EntityNames.ASSESSMENT)) {
        	bodyToSubdocs(entity, "assessmentItem", "assessmentId");
        	bodyToSubdocs(entity, "objectiveAssessment", "assessmentId");
        }
    }
    
    private Entity bodyToSubdocs(Entity parent, String subentityType, String parentKey) {
    	if (parent.getBody().get(subentityType) != null) {
    		List<Entity> subdocs = new ArrayList<Entity>();
    		@SuppressWarnings("unchecked")
			List<Map<String, Object>> subdocInBody = (List<Map<String, Object>>) parent.getBody().get(subentityType);
    		for (Map<String, Object> inbodyDoc : subdocInBody) {
    			inbodyDoc.put(parentKey, parent.getEntityId());
    			MongoEntity subdoc = new MongoEntity(subentityType, null, inbodyDoc, null);
    			subdocs.add(subdoc);
    		}
    		
    		if (!subdocs.isEmpty()) {
    			parent.getEmbeddedData().put(subentityType, subdocs);
    			parent.getBody().remove(subentityType);
    		}
    	}
    	
    	return parent;
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
    
}
