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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

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
 * @author kmyers
 *
 */
public class AddStrategyTest {
    
    private AddStrategy addStrategy;
    private Entity testEntity;
    
    @Before
    public void init() {
        this.addStrategy = new AddStrategy();
        this.testEntity = this.createTestEntity();
    }
    
    private Entity createTestEntity() {
        String entityType = "Type";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        body.put("nested", new HashMap<String, Object>());
        
        return new MongoEntity(entityType, entityId, body, metaData);
    }
    
    /*
     * no field name specified (non-null map of parameters)
     */
    @Test(expected = MigrationException.class)
    public void testBadParams() throws MigrationException {
        this.addStrategy.setParameters(new HashMap<String, Object>());
    }
    
    /*
     * no field name specified (null map of parameters)
     */
    @Test(expected = MigrationException.class)
    public void testNullParams() throws MigrationException {
        this.addStrategy.setParameters(null);
    }
    
    @Test
    public void testValidMigrationNullDefault() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AddStrategy.FIELD_NAME, "foo");
        
        this.addStrategy.setParameters(parameters);
        this.addStrategy.migrate(this.testEntity);
        assertTrue(this.testEntity.getBody().containsKey("foo"));
        assertTrue(this.testEntity.getBody().get("foo") == null);
    }

    @Test
    public void testValidMigrationWithDefault() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AddStrategy.FIELD_NAME, "foo");
        parameters.put(AddStrategy.DEFAULT_VALUE, "bar");
        
        this.addStrategy.setParameters(parameters);
        this.addStrategy.migrate(this.testEntity);
        
        assertTrue(this.testEntity.getBody().containsKey("foo"));
        assertTrue(this.testEntity.getBody().get("foo").equals("bar"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidNestedMigrationWithDefault() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(AddStrategy.FIELD_NAME, "nested.foo");
        parameters.put(AddStrategy.DEFAULT_VALUE, "bar");
        
        this.addStrategy.setParameters(parameters);
        this.addStrategy.migrate(this.testEntity);
        
        Map<String, Object> nested = (Map<String, Object>) this.testEntity.getBody().get("nested");
        assertTrue(nested.containsKey("foo"));
        assertTrue(nested.get("foo").equals("bar"));
    }
}
