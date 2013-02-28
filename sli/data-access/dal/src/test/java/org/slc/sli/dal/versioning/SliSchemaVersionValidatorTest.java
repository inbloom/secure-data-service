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

package org.slc.sli.dal.versioning;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.dal.migration.strategy.impl.AddStrategy;
import org.slc.sli.dal.repository.ValidationWithoutNaturalKeys;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * Tests for schema version checking logic.
 * 
 * 
 * @author kmyers
 * 
 */
public class SliSchemaVersionValidatorTest {

    public static final String STUDENT = "student";
    public static final String SECTION = "section";
    public static final String TEACHER = "teacher";
    @InjectMocks
    private SliSchemaVersionValidator sliSchemaVersionValidator;

    @Mock
    private SchemaRepository entitySchemaRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        sliSchemaVersionValidator = new SliSchemaVersionValidator();
        sliSchemaVersionValidator.migrationConfigResource = new ClassPathResource(
                "migration/test-migration-config.json");
        MockitoAnnotations.initMocks(this);
    }

    private AppInfo createAppInfo(int version) {
        AppInfo appInfo = new AppInfo(null);
        appInfo.put(AppInfo.SCHEMA_VERSION, "" + version);
        return appInfo;
    }

    private NeutralSchema createMockSchema(String type, int version) {
        NeutralSchema mockNeutralSchema = mock(NeutralSchema.class);
        when(mockNeutralSchema.getAppInfo()).thenReturn(this.createAppInfo(version));
        when(mockNeutralSchema.getType()).thenReturn(type);
        return mockNeutralSchema;
    }

    @Test
    public void testInitMigration() {
        initMockMigration();

        Mockito.verify(mongoTemplate, Mockito.times(1)).updateFirst(Mockito.any(Query.class),
                Mockito.any(Update.class), Mockito.any(String.class));
        Mockito.verify(mongoTemplate, Mockito.times(1)).insert(Mockito.any(Object.class), Mockito.any(String.class));

    }

    @Test
    public void shouldCreateAddStrategy() {
        this.sliSchemaVersionValidator.initMigration();

        List<MigrationStrategy> transforms = this.sliSchemaVersionValidator.getMigrationStrategies("student", 0, 2);

        Assert.assertEquals(1, transforms.size());
        MigrationStrategy strategy = transforms.get(0);
        assertTrue("Expected AddStrategy", strategy instanceof AddStrategy);
    }

    @Test
    public void shouldAddVersion() {
        List<NeutralSchema> neutralSchemas = new ArrayList<NeutralSchema>();
        neutralSchemas.add(this.createMockSchema(STUDENT, 2));

        List<EntityVersionData> data = Arrays.asList(new EntityVersionData[] { new EntityVersionData(STUDENT, 2,
                new CollectionVersionData(1, 0)) });

        setupMockVersions(data);

        this.sliSchemaVersionValidator.initMigration();

        Entity student = new MongoEntity("student", new HashMap<String, Object>());
        this.sliSchemaVersionValidator.insertVersionInformation(student);

        Assert.assertEquals(2, student.getMetaData().get("version"));
    }

    @Test
    public void shouldMigrateEntities() {

        ValidationWithoutNaturalKeys mockRepo = Mockito.mock(ValidationWithoutNaturalKeys.class);

        List<NeutralSchema> neutralSchemas = new ArrayList<NeutralSchema>();
        neutralSchemas.add(this.createMockSchema(STUDENT, 2));

        List<EntityVersionData> data = Arrays.asList(new EntityVersionData[] { new EntityVersionData(STUDENT, 2,
                new CollectionVersionData(1, 0)) });

        setupMockVersions(data);

        this.sliSchemaVersionValidator.initMigration();

        List<Entity> students = new ArrayList<Entity>();
        students.add(new MongoEntity("student", new HashMap<String, Object>()));
        students.add(new MongoEntity("student", new HashMap<String, Object>()));
        students.add(new MongoEntity("student", new HashMap<String, Object>()));

        Iterable<Entity> result = sliSchemaVersionValidator.migrate("student", students, mockRepo);

        for (Entity e : result) {
            Assert.assertEquals("yellow", e.getBody().get("favoriteColor"));
        }

    }

    @Test
    public void testIsMigrationNeeded() {
        String entityType = "student";
        Entity entity = new MongoEntity("student", new HashMap<String, Object>());

        initMockMigration();
        assertTrue("Should be true", sliSchemaVersionValidator.isMigrationNeeded(entityType, entity));

        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("version", 5);
        entity = new MongoEntity("student", "someId", new HashMap<String, Object>(), metaData);
        assertFalse("Should be false", sliSchemaVersionValidator.isMigrationNeeded(entityType, entity));
    }

    @Test
    public void testPerformMigration() {
        String entityType = "student";
        String collectionName = "student";
        Entity entity = new MongoEntity("student", new HashMap<String, Object>());

        ValidationWithoutNaturalKeys repo = mock(ValidationWithoutNaturalKeys.class);
        when(repo.updateWithoutValidatingNaturalKeys(anyString(), any(Entity.class))).thenReturn(true);

        initMockMigration();

        sliSchemaVersionValidator.performMigration(entityType, entity, repo, collectionName, false);
        Mockito.verify(repo, never()).updateWithoutValidatingNaturalKeys(anyString(), any(Entity.class));

        sliSchemaVersionValidator.performMigration(entityType, entity, repo, collectionName, true);
        Mockito.verify(repo, Mockito.times(1)).updateWithoutValidatingNaturalKeys(anyString(), any(Entity.class));
    }

    @Test
    public void testGetMigrationStrategies() {
        initMockMigration();

        Entity entity = new MongoEntity("staff", new HashMap<String, Object>());
        List<MigrationStrategy> strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 0, 2);
        assertEquals("Should match", 1, strategies.size());
        assertTrue("Should be true", strategies.get(0) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(0).migrate(entity), "somefield", "somevalue"));

        entity = new MongoEntity("staff", new HashMap<String, Object>());
        strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 0, 3);
        assertEquals("Should match", 2, strategies.size());
        assertTrue("Should be true", strategies.get(0) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(0).migrate(entity), "somefield", "somevalue"));
        assertTrue("Should be true", strategies.get(1) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(1).migrate(entity), "favoriteColor", "yellow"));

        entity = new MongoEntity("staff", new HashMap<String, Object>());
        strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 0, 4);
        assertEquals("Should match", 3, strategies.size());
        assertTrue("Should be true", strategies.get(0) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(0).migrate(entity), "somefield", "somevalue"));
        assertTrue("Should be true", strategies.get(1) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(1).migrate(entity), "favoriteColor", "yellow"));
        assertTrue("Should be true", strategies.get(2) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(2).migrate(entity), "mascot", "red"));

        entity = new MongoEntity("staff", new HashMap<String, Object>());
        strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 3, 4);
        assertEquals("Should match", 1, strategies.size());
        assertTrue("Should be true", strategies.get(0) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(0).migrate(entity), "mascot", "red"));

        strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 0, 1);
        assertEquals("Should match", 0, strategies.size());

        entity = new MongoEntity("staff", new HashMap<String, Object>());
        strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 2, 4);
        assertEquals("Should match", 2, strategies.size());
        assertTrue("Should be true", strategies.get(0) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(0).migrate(entity), "favoriteColor", "yellow"));
        assertTrue("Should be true", strategies.get(1) instanceof  AddStrategy);
        assertTrue("Should be true", checkEntity((Entity) strategies.get(1).migrate(entity), "mascot", "red"));

        strategies = sliSchemaVersionValidator.getMigrationStrategies("staff", 3, 1);
        assertEquals("Should match", 0, strategies.size());
    }

    private boolean checkEntity(Entity entity, String key, String value) {
        String entityValue = (String) entity.getBody().get(key);

        return value.equals(entityValue);
    }

    private void initMockMigration() {
        List<EntityVersionData> data = Arrays.asList(new EntityVersionData[] {
                new EntityVersionData(STUDENT, 1, new CollectionVersionData(1, 0)),
                new EntityVersionData(SECTION, 2, new CollectionVersionData(1, 0)),
                new EntityVersionData(TEACHER, 1, null) });

        setupMockVersions(data);

        this.sliSchemaVersionValidator.initMigration();
    }

    private void setupMockVersions(List<EntityVersionData> enitityData) {

        List<NeutralSchema> neutralSchemas = new ArrayList<NeutralSchema>();

        for (EntityVersionData data : enitityData) {
            neutralSchemas.add(this.createMockSchema(data.entityType, data.schemaVersion));
        }

        when(entitySchemaRepository.getSchemas()).thenReturn(neutralSchemas);

        final List<DBObject> findOnes = new ArrayList<DBObject>();
        for (EntityVersionData data : enitityData) {

            BasicDBObject dbObject = null;
            if (data.collectionVersion != null) {
                dbObject = new BasicDBObject();
                dbObject.put(SliSchemaVersionValidator.ID, data.entityType);
                dbObject.put(SliSchemaVersionValidator.DAL_SV, new Double(data.collectionVersion.dalVersion));
                dbObject.put(SliSchemaVersionValidator.MONGO_SV, new Double(data.collectionVersion.mongoVersion));
            }

            findOnes.add(dbObject);
        }

        when(
                mongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(BasicDBObject.class),
                        Mockito.eq(SliSchemaVersionValidator.METADATA_COLLECTION))).thenAnswer(new Answer<DBObject>() {
            @Override
            public DBObject answer(InvocationOnMock invocation) throws Throwable {
                return findOnes.remove(0);
            }
        });
    }

    private class EntityVersionData {
        public final String entityType;
        public final int schemaVersion;
        public final CollectionVersionData collectionVersion;

        public EntityVersionData(String entityType, int schemaVersion, CollectionVersionData collectionVersion) {
            super();
            this.entityType = entityType;
            this.schemaVersion = schemaVersion;
            this.collectionVersion = collectionVersion;
        }
    }

    private class CollectionVersionData {
        public final int dalVersion;
        public final int mongoVersion;

        public CollectionVersionData(int dalVersion, int mongoVersion) {
            super();
            this.dalVersion = dalVersion;
            this.mongoVersion = mongoVersion;
        }
    }

}
