/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;

/**
 * Tests the ReferenceSchema methods
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ReferenceSchemaTest {

    ReferenceSchema schema; // class under test

    @Autowired
    SchemaRepository schemaRepository;

    private ReferenceSchema spySchema;
    private ValidationRepo repo = new ValidationRepo();
    private static final String UUID = "123456789";
    private static final String REFERENCED_COLLECTION = "section";
    private static final String REFERENCE_FIELDNAME = "courseId";

    @Before
    public void setup() {
        schema = new ReferenceSchema("school", schemaRepository);

        Entity entity = mock(Entity.class);
        when(entity.getEntityId()).thenReturn(UUID);

        repo.addEntity(REFERENCED_COLLECTION, entity);

        AppInfo info = mock(AppInfo.class);
        when(info.getReferenceType()).thenReturn(REFERENCED_COLLECTION);

        spySchema = spy(schema);
        when(spySchema.getAppInfo()).thenReturn(info);
    }

    @Test
    public void testReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        assertTrue("Reference entity validation failed", spySchema.validate(REFERENCE_FIELDNAME, UUID, errors, repo));
    }

    @Test
    public void testInvalidReferenceValidation() throws IllegalArgumentException {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        assertFalse("Invalid Reference entity validation failed",
                spySchema.validate(REFERENCE_FIELDNAME, "45679", errors, repo));
    }

    /**
     * Mock repo class
     *
     * @author srupasinghe
     *
     */
    static class ValidationRepo implements Repository<Entity> {
        Map<String, List<Entity>> data = new HashMap<String, List<Entity>>();

        public void addEntity(String collectionName, Entity entity) {
            if (data.get(collectionName) != null) {
                data.get(collectionName).add(entity);
            } else {
                List<Entity> list = new ArrayList<Entity>();
                list.add(entity);

                data.put(collectionName, list);
            }
        }

        @Override
        public boolean collectionExists(String collection) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void createCollection(String collection) {
            // TODO Auto-generated method stub

        }

        @Override
        public void ensureIndex(IndexDefinition index, String collection) {
            // TODO Auto-generated method stub

        }

        @Override
        public Entity findById(String collectioName, String id) {
            List<Entity> list = data.get(collectioName);

            for (Entity e : list) {
                if (e.getEntityId().equals(id)) {
                    return e;
                }
            }

            return null;
        }

        @Override
        public Iterable<Entity> findAll(String collectionName, NeutralQuery query) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<Entity> findAll(String collectioName) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean update(String collection, Entity entity) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Entity create(String type, Map<String, Object> body) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Entity create(String type, Map<String, Object> body, String collectionName) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean delete(String collectionName, String id) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void deleteAll(String collectionName) {
            // TODO Auto-generated method stub

        }

        @Override
        public Iterable<Entity> findAllByPaths(String collectionName, Map<String, String> paths,
                NeutralQuery neutralQuery) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long count(String collectionName, NeutralQuery query) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Entity findOne(String collectionName, NeutralQuery query) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<String> findAllIds(String collectionName, NeutralQuery query) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public CommandResult execute(DBObject command) {
            return null;
        }

        @Override
        public DBCollection getCollection(String collectionName) {
            return null;
        }

        @Override
        public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean exists(String collectionName, String id) {
            List<Entity> list = data.get(collectionName);

            for (Entity e : list) {
                if (e.getEntityId().equals(id)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void setWriteConcern(String writeConcern) {
            // TODO Auto-generated method stub

        }

        @Override
        public long count(String collectionName, Query query) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void setReferenceCheck(String referenceCheck) {
            // TODO Auto-generated method stub
        }

        @Override
        public List<DBCollection> getCollections(boolean includeSystemCollections) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean doUpdate(String collection, String id, Update update) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean doUpdate(String collection, NeutralQuery query, Update update) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Entity createWithRetries(String type, Map<String, Object> body, Map<String, Object> metaData,
                String collectionName, int noOfRetries) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean updateWithRetries(String collection, Entity object, int noOfRetries) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public List<Entity> insert(List<Entity> records, String collectionName) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Entity createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
                String collectionName, int noOfRetries) {
            return null;
        }

        @Override
        public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
            return false;
        }

        @Override
        public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
