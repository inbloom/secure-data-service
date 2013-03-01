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
package org.slc.sli.api.migration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.dal.migration.strategy.impl.AddStrategy;
import org.slc.sli.dal.migration.strategy.impl.RemoveFieldStrategy;
import org.slc.sli.dal.migration.strategy.impl.RenameFieldStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ApiSchemaAdapterTest {

    @Value("classpath:migration/apiSchemaAdapterTest-down.json/")
    private Resource downResource;

    @Value("classpath:migration/apiSchemaAdapterTest-up.json/")
    private Resource upResource;

    @Value("classpath:migration/api-entity-transform.json/")
    private Resource entityResource;

    @Test
    public void testBuildMigrationStrategyMap() throws NoSuchFieldException, IllegalAccessException{
        ApiSchemaAdapter apiSchemaAdapter = initApiSchemaAdapter();
        List<MigrationStrategy> downList = apiSchemaAdapter.getDownMigrationStrategies("session", 1);
        List<MigrationStrategy> upList = apiSchemaAdapter.getUpMigrationStrategies("session", 1);
        assertTrue(downList != null);
        assertTrue(upList != null);
        assertTrue(downList.get(0) instanceof AddStrategy);
        assertTrue(((String) getField(downList.get(0), "fieldName")).equals("downFoo"));
        assertTrue(((String) getField(downList.get(0), "defaultValue")).equals("downBar"));
        assertTrue(downList.get(1) instanceof RemoveFieldStrategy);
        assertTrue(((String) getField(downList.get(1), "fieldName")).equals("removeDownFoo"));
        assertTrue(downList.get(2) instanceof RenameFieldStrategy);
        assertTrue(((String) getField(downList.get(2), "oldFieldName")).equals("oldDownFoo"));
        assertTrue(((String) getField(downList.get(2), "newFieldName")).equals("newDownFoo"));
        assertTrue(upList.get(0) instanceof AddStrategy);
        assertTrue(((String) getField(upList.get(0), "fieldName")).equals("upFoo"));
        assertTrue(((String) getField(upList.get(0), "defaultValue")).equals("upBar"));
        assertTrue(upList.get(1) instanceof RemoveFieldStrategy);
        assertTrue(((String) getField(upList.get(1), "fieldName")).equals("removeUpFoo"));
        assertTrue(upList.get(2) instanceof RenameFieldStrategy);
        assertTrue(((String) getField(upList.get(2), "oldFieldName")).equals("oldUpFoo"));
        assertTrue(((String) getField(upList.get(2), "newFieldName")).equals("newUpFoo"));
    }

    @Test
    public void testMigrate() {
        ApiSchemaAdapter apiSchemaAdapter = initApiSchemaAdapter();
        EntityBody downBody = new EntityBody();
        downBody.put("removeDownFoo", "removeDownBar");
        downBody.put("oldDownFoo", "oldDownBar");
        Entity downEntity =  new MongoEntity("session", downBody);

        EntityBody upBody = new EntityBody();
        upBody.put("removeUpFoo", "removeUpBar");
        upBody.put("oldUpFoo", "oldUpBar");
        Entity upEntity =  new MongoEntity("session", upBody);

        Entity newDownEntity = apiSchemaAdapter.migrate(downEntity, 1, false);
        Entity newUpEntity = apiSchemaAdapter.migrate(upEntity, 1, true);

        assertTrue(newDownEntity.getBody().containsKey("downFoo"));
        assertTrue(newDownEntity.getBody().get("downFoo").equals("downBar"));
        assertTrue(!newDownEntity.getBody().containsKey("removeDownFoo"));
        assertTrue(!newDownEntity.getBody().containsKey("oldDownFoo"));
        assertTrue(newDownEntity.getBody().containsKey("newDownFoo"));
        assertTrue(newDownEntity.getBody().get("newDownFoo").equals("oldDownBar"));

        assertTrue(newUpEntity.getBody().containsKey("upFoo"));
        assertTrue(newUpEntity.getBody().get("upFoo").equals("upBar"));
        assertTrue(!newUpEntity.getBody().containsKey("removeUpFoo"));
        assertTrue(!newUpEntity.getBody().containsKey("oldUpFoo"));
        assertTrue(newUpEntity.getBody().containsKey("newUpFoo"));
        assertTrue(newUpEntity.getBody().get("newUpFoo").equals("oldUpBar"));
    }

    @Test
    public void testMigrateIterable() {
        ApiSchemaAdapter apiSchemaAdapter = initApiSchemaAdapter();
        EntityBody downBody = new EntityBody();
        downBody.put("removeDownFoo", "removeDownBar");
        downBody.put("oldDownFoo", "oldDownBar");
        Entity downSessionEntity =  new MongoEntity("session", downBody);
        Entity downStudentEntity =  new MongoEntity("student", null);

        EntityBody upBody = new EntityBody();
        upBody.put("removeUpFoo", "removeUpBar");
        upBody.put("oldUpFoo", "oldUpBar");
        Entity upSessionEntity =  new MongoEntity("session", upBody);
        Entity upStudentEntity =  new MongoEntity("student", null);

        ArrayList<Entity> oldDownEntityList = new ArrayList<Entity>();
        oldDownEntityList.add(downSessionEntity);
        oldDownEntityList.add(downStudentEntity);

        ArrayList<Entity> oldUpEntityList = new ArrayList<Entity>();
        oldUpEntityList.add(upSessionEntity);
        oldUpEntityList.add(upStudentEntity);

        Iterator<Entity> newDownEntityList = apiSchemaAdapter.migrate(oldDownEntityList, 1, false).iterator();
        Entity currDownEntity = newDownEntityList.next();

        assertTrue(currDownEntity.getBody().containsKey("downFoo"));
        assertTrue(currDownEntity.getBody().get("downFoo").equals("downBar"));
        assertTrue(!currDownEntity.getBody().containsKey("removeDownFoo"));
        assertTrue(!currDownEntity.getBody().containsKey("oldDownFoo"));
        assertTrue(currDownEntity.getBody().containsKey("newDownFoo"));
        assertTrue(currDownEntity.getBody().get("newDownFoo").equals("oldDownBar"));
        assertTrue(newDownEntityList.next() != null);
        assertTrue(!newDownEntityList.hasNext());

        Iterator<Entity> newUpEntityList = apiSchemaAdapter.migrate(oldUpEntityList, 1, true).iterator();
        Entity firstUpEntity = newUpEntityList.next();
        assertTrue(firstUpEntity.getBody().containsKey("upFoo"));
        assertTrue(firstUpEntity.getBody().get("upFoo").equals("upBar"));
        assertTrue(!firstUpEntity.getBody().containsKey("removeUpFoo"));
        assertTrue(!firstUpEntity.getBody().containsKey("oldUpFoo"));
        assertTrue(firstUpEntity.getBody().containsKey("newUpFoo"));
        assertTrue(firstUpEntity.getBody().get("newUpFoo").equals("oldUpBar"));
        assertTrue(newUpEntityList.next() != null);
        assertTrue(!newUpEntityList.hasNext());
    }

    private ApiSchemaAdapter initApiSchemaAdapter() {
        ApiSchemaAdapter apiSchemaAdapter = new ApiSchemaAdapter();
        apiSchemaAdapter.setDownMigrationConfigResource(downResource);
        apiSchemaAdapter.setUpMigrationConfigResource(upResource);
        apiSchemaAdapter.setEntityTransformConfigResource(entityResource);
        apiSchemaAdapter.initMigration();
        return apiSchemaAdapter;
    }

    private Object getField(Object o, String fieldName) throws NoSuchFieldException, IllegalAccessException
    {
        Class<?> clazz = o.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(o);
    }

}
