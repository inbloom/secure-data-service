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
 * Tests the migration code responsible for moving fields inside and outside of body
 * 
 * 
 * @author ycao 
 *
 */
public class RelocateStrategyTest {

    private RelocateStrategy relocateStrategy;
    private Entity testEntity;

    @Before
    public void init() {
        this.relocateStrategy = new RelocateStrategy();
        this.testEntity = this.createTestEntity();
    }

    private Entity createTestEntity() {
        String entityType = "Type";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();
        body.put("nested", new HashMap<String, Object>());

        List<Map<String, Object>> insideBody = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("a", "1");
        item.put("b", "2");
        item.put("c", "3");
        insideBody.add(item);
        body.put("existsInsideBody", insideBody);

        List<Entity> outsideBody = new ArrayList<Entity>();
        Map<String, Object> item2 = new HashMap<String, Object>();
        item2.put("x", "24");
        item2.put("y", "25");
        item2.put("z", "26");
        outsideBody.add(new MongoEntity("outsideBody", "outsideBody", item2, null));
        embeddedData.put("existsOutsideBody", outsideBody);

        return new MongoEntity(entityType, entityId, body, metaData, null, null, embeddedData, null);
    }

    /*
     * no parameter set
     */
    @Test(expected = MigrationException.class)
    public void noParametersShouldThrowMigrationException() throws MigrationException {
        this.relocateStrategy.setParameters(new HashMap<String, Object>());
    }

    /*
     * "to" not set
     */
    @Test(expected = MigrationException.class)
    public void noToFieldShouldThrowMigrationException() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("from", "someWhereInsideBody");
        this.relocateStrategy.setParameters(parameters);
    }

    /*
     * "from" not set
     */
    @Test(expected = MigrationException.class)
    public void noFromFieldShouldThrowMigrationException() throws MigrationException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to", ".someWhereOutsideBody");
        this.relocateStrategy.setParameters(parameters);
    }

    /*
     * if "from" and "to" are valid field, relocate the specified "from" field
     * to "to" field
     * 
     * "from" must be a list of complex types, i.e. Map<String, Objects>
     *
     * duplicate functionality of rename strategy, so please consider using rename if 
     * both fields are inside "body"
     */
    @Test
    public void bothFromAndToAreInsideBody() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to", "someWhereElseInsideBody");
        parameters.put("from", "existsInsideBody");
        this.relocateStrategy.setParameters(parameters);
        Entity originClone = cloneEntity(this.testEntity);
        Entity transformedEntity = this.relocateStrategy.migrate(this.testEntity);
        assertTrue(transformedEntity.getBody().get("someWhereElseInsideBody")
                .equals(originClone.getBody().get("existsInsideBody")));
    }

    /*
     * relocate from inside body to outside body
     * 
     * a starting "." indicates outside of the body, it's
     * a top level field 
     * 
     */
    @Test  
    public void movingFromInsideBodyToOutsideBody() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to", ".someWhereElseOutsideBody");
        parameters.put("from", "existsInsideBody");
        this.relocateStrategy.setParameters(parameters);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> originClone = (List<Map<String, Object>>) (cloneEntity(this.testEntity).getBody().get("existsInsideBody"));
        Entity transformedEntity = this.relocateStrategy.migrate(this.testEntity);
        List<Entity> topLevelEntities = transformedEntity.getEmbeddedData().get("someWhereElseOutsideBody");
        //can only test 1 item, otherise sequence may not match
        for (int i=0; i<topLevelEntities.size(); ++i ) {
            Entity e = topLevelEntities.get(i);
            assertEquals(e.getType(), "existsInsideBody");
            assertEquals(e.getBody(), originClone.get(0));
        }
    }

    /*
     * relocate from outside body into body field 
     * 
     * a starting "." indicates outside of the body, it's
     * a top level field 
     * 
     */
    @Test
    public void movingFromOutsideBodyIntoBody() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to", "someWhereInsideBody");
        parameters.put("from", ".existsOutsideBody");
        this.relocateStrategy.setParameters(parameters);

        Entity originClone = cloneEntity(this.testEntity);
        Entity transformedEntity = this.relocateStrategy.migrate(this.testEntity);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) transformedEntity.getBody().get("someWhereInsideBody");
        assertTrue(originClone.getEmbeddedData().get("existsOutsideBody").get(0).getBody().equals(items.get(0)));
    }

    /*
     * if "from" points to nothing, don't move anything
     */
    @Test
    public void fromDoesNotExistNothingShouldChange() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("to", "someWhereOutsideBody");
        parameters.put("from", "someWhereInsideBodyButDoesNotExist");
        this.relocateStrategy.setParameters(parameters);
        Entity originClone = cloneEntity(this.testEntity);
        Entity transformedEntity = this.relocateStrategy.migrate(this.testEntity);
        assertTrue(originClone.getBody().equals((transformedEntity.getBody())));
        //only 1 item
        assertTrue(originClone.getEmbeddedData().get("existsOutsideBody").get(0).getBody()
                .equals((transformedEntity.getEmbeddedData().get("existsOutsideBody").get(0).getBody())));
    }



    private Entity cloneEntity(Entity entity) {
        return new MongoEntity(entity.getType(), entity.getEntityId(), new HashMap<String, Object>(entity.getBody()), 
                new HashMap<String, Object>(entity.getMetaData()), null, null, deepCopy(entity.getEmbeddedData()), null);
    } 

    private Map<String, List<Entity>> deepCopy(Map<String, List<Entity>> data) {
        if (data == null) {
            return null;
        }

        Map<String, List<Entity>> clone = new HashMap<String, List<Entity>>();
        for (Entry<String, List<Entity>> entry : data.entrySet()) {
            List<Entity> entities = entry.getValue();
            List<Entity> clonedEntities = new ArrayList<Entity>(entities.size());
            for (Entity e : entities) {
                clonedEntities.add(new MongoEntity(e.getType(), e.getEntityId(), 
                            new HashMap<String, Object>(e.getBody()), null));
            }
            clone.put(entry.getKey(), clonedEntities);
        }

        return clone;
    }
}
