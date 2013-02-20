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

package org.slc.sli.dal.migration.strategy.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.dal.migration.strategy.MigrationException;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Tests the migration code responsible for adding new fields with or without a
 * default value.
 * 
 * 
 * @author ycao 
 *
 */
public class CombineStrategyTest {

	private CombineStrategy combineStrategy;
	private Entity testEntity;
	private Entity clone;

	@Before
	public void init() {
		this.combineStrategy = new CombineStrategy();
		this.testEntity = this.createTestEntity();
        this.clone = this.createTestEntity();
	}

	private Entity createTestEntity() {
		String entityType = "Type";
		String entityId = "ID";
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> metaData = new HashMap<String, Object>();
		Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();
		body.put("nested", new HashMap<String, Object>());

		List<Map<String, Object>> insideBodyItems = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("a", "1");
		insideBodyItems.add(item);
		body.put("assessmentItem", insideBodyItems);

		List<Entity> outsideBodyItems = new ArrayList<Entity>();
		Map<String, Object> item2 = new HashMap<String, Object>();
		item2.put("y", "25");
		outsideBodyItems.add(new MongoEntity("assessmentItem", "item_id", item2, null));
		embeddedData.put("assessmentItem", outsideBodyItems);
		
		List<Entity> outsideBodyObjAssessments = new ArrayList<Entity>();
		Map<String, Object> item3 = new HashMap<String, Object>();
		item3.put("z", "26");
		outsideBodyObjAssessments.add(new MongoEntity("objectiveAssessment", "objAssessment_id", item3, null));
		embeddedData.put("objectiveAssessment", outsideBodyObjAssessments);

		return new MongoEntity(entityType, entityId, body, metaData, null, null, embeddedData, null);
	}   

	/*
	 * no field name specified (non-null map of parameters)
	 */
	@Test(expected = MigrationException.class)
	public void testBadParams() throws MigrationException {
		this.combineStrategy.setParameters(new HashMap<String, Object>());
	}

	@Test
	public void bodyNotChangedIfSubentityDoesNotExist() {
        Entity transformedEntity = transform("doesnotexist");
        assertEquals(this.clone.getBody(), transformedEntity.getBody());
	}

    @SuppressWarnings("unchecked")
	@Test
	public void subentitiesShouldMoveInsideBodyWhenNoCollision() {
    	String subentity = "objectiveAssessment";
        //should have been moved inside body
    	Entity transformedEntity = transform(subentity);
        assertNull(transformedEntity.getEmbeddedData().get(subentity));
        //should contain the same data 
        assertEquals(this.clone.getEmbeddedData().get(subentity).get(0).getBody(),
        		((List<Map<String, Object>>)transformedEntity.getBody().get(subentity)).get(0));
	}
    
    @SuppressWarnings("unchecked")
	@Test
    public void differentSubentityShouldBeAppendedInsideBody() {
    	String subentity = "assessmentItem";
    	Entity transformedEntity = transform(subentity);
        assertNull(transformedEntity.getEmbeddedData().get(subentity));
        assertEquals(((List<Map<String, Object>>)this.clone.getBody().get(subentity)).size() + this.clone.getEmbeddedData().get(subentity).size(),
        			((List<Map<String, Object>>)transformedEntity.getBody().get(subentity)).size());
    }
   
    private Entity transform(String subentity) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("subentity", subentity);
        this.combineStrategy.setParameters(parameters);
        return this.combineStrategy.migrate(this.testEntity);
    }
    
}
