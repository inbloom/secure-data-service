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

package org.slc.sli.dal.migration.strategy;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.dal.migration.strategy.impl.RemoveFieldStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;

/**
 * Tests the migration code responsible for adding new fields with or without a
 * default value.
 * 
 * 
 * @author kmyers
 *
 */
public class RemoveFieldStrategyTest {
    
    private RemoveFieldStrategy addStrategy;
    private Entity testEntity;
    
    @Before
    public void init() {
        this.addStrategy = new RemoveFieldStrategy();
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
        Map<String, Object> params = new HashMap<String, Object>();
        this.addStrategy.setParameters(params);
    }

    /*
     * no field name specified (null map of parameters)
     */
    @Test(expected = MigrationException.class)
    public void testNullParams() throws MigrationException {
        this.addStrategy.setParameters(null);
    }

    @Test
    public void testSimpleRenameWithFieldMissing() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(RemoveFieldStrategy.FIELD_NAME, "foo");
        
        this.addStrategy.setParameters(parameters);
        this.addStrategy.migrate(this.testEntity);
        
        assertFalse(this.testEntity.getBody().containsKey("foo"));
    }

    @Test
    public void testSimpleRenameWithFieldPresent() throws MigrationException {
        Object testData = "testString";
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(RemoveFieldStrategy.FIELD_NAME, "foo");
        this.testEntity.getBody().put("foo", testData);
        
        this.addStrategy.setParameters(parameters);
        this.addStrategy.migrate(this.testEntity);
        
        assertFalse(this.testEntity.getBody().containsKey("foo"));
    }
}
