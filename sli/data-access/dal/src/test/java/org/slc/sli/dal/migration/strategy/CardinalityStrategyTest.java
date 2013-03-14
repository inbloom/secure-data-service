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
import org.slc.sli.dal.migration.strategy.impl.CardinalityStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Tests the migration code responsible for cardinality changes
 * 
 * 
 * @author jcole
 *
 */
public class CardinalityStrategyTest {
    
    private CardinalityStrategy cardinalityStrategy;
    private Entity testEntityOptionalField;
    private Entity testEntityManyField;

    @Before
    public void init() {
        this.cardinalityStrategy = new CardinalityStrategy();
        this.testEntityOptionalField = this.createTestEntityOptionalField();
        this.testEntityManyField = this.createTestEntityManyField();
    }

    private Entity createTestEntityOptionalField() {
        String entityType = "Type";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        body.put("nested", new HashMap<String, Object>());

        return new MongoEntity(entityType, entityId, body, metaData);
    }

    private Entity createTestEntityManyField() {
        String entityType = "Type";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        body.put("nested", new HashMap<String, Object>());

        List<String> list = new ArrayList<String>();
        list.add ("string1");
        list.add ("string2");
        list.add ("string3");

        body.put("foo", list);

        return new MongoEntity(entityType, entityId, body, metaData);
    }

    /*
     * only FIELD_NAME specified (non-null map of parameters)
     */
    @Test(expected = MigrationException.class)
    public void testBadParams1() throws MigrationException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CardinalityStrategy.FIELD_NAME, "foo");
        this.cardinalityStrategy.setParameters(params);
    }

    /*
     * only MIN_COUNT specified (non-null map of parameters)
     */
    @Test(expected = MigrationException.class)
    public void testBadParams2() throws MigrationException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CardinalityStrategy.MIN_COUNT, "1");
        this.cardinalityStrategy.setParameters(params);
    }

    /*
     * only MAX_COUNT specified (non-null map of parameters)
     */
    @Test(expected = MigrationException.class)
    public void testBadParams3() throws MigrationException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CardinalityStrategy.MAX_COUNT, "1");
        this.cardinalityStrategy.setParameters(params);
    }

    /*
     * null map of parameters
     */
    @Test(expected = MigrationException.class)
    public void testNullParams() throws MigrationException {
        this.cardinalityStrategy.setParameters(null);
    }

    @Test
    public void testOptionalToRequired() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(CardinalityStrategy.FIELD_NAME, "foo");
        parameters.put(CardinalityStrategy.MIN_COUNT, "1");
        parameters.put(CardinalityStrategy.MAX_COUNT, "1");

        this.cardinalityStrategy.setParameters(parameters);
        this.cardinalityStrategy.migrate(this.testEntityOptionalField);

        assertTrue(this.testEntityOptionalField.getBody().containsKey("foo"));
        assertTrue(this.testEntityOptionalField.getBody().get("foo") != null);
    }

    @Test
    public void testManyToOne() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(CardinalityStrategy.FIELD_NAME, "foo");
        parameters.put(CardinalityStrategy.MIN_COUNT, "1");
        parameters.put(CardinalityStrategy.MAX_COUNT, "1");

        this.cardinalityStrategy.setParameters(parameters);
        this.cardinalityStrategy.migrate(this.testEntityManyField);

        assertTrue(this.testEntityManyField.getBody().containsKey("foo"));
        assertTrue(this.testEntityManyField.getBody().get("foo") != null);

        Object valueObject = this.testEntityManyField.getBody().get("foo");
        assertTrue(valueObject instanceof String);
        String value = (String)valueObject;
        assertTrue(value.equals ("string1"));
    }

}
