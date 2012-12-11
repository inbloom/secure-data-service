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

package org.slc.sli.dal.versioning;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.dal.migration.strategy.MigrationStrategy;
import org.slc.sli.dal.migration.strategy.impl.AddStrategy;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
    public void test() {

        List<NeutralSchema> neutralSchemas = new ArrayList<NeutralSchema>();
        neutralSchemas.add(this.createMockSchema(STUDENT, 1));
        neutralSchemas.add(this.createMockSchema(SECTION, 2));
        neutralSchemas.add(this.createMockSchema(TEACHER, 1));

        when(entitySchemaRepository.getSchemas()).thenReturn(neutralSchemas);

        BasicDBObject studentDbObject = new BasicDBObject();
        studentDbObject.put(SliSchemaVersionValidator.ID, STUDENT);
        studentDbObject.put(SliSchemaVersionValidator.DAL_SV, new Double(1.0));
        BasicDBObject sectionDbObject = new BasicDBObject();
        sectionDbObject.put(SliSchemaVersionValidator.ID, SECTION);
        sectionDbObject.put(SliSchemaVersionValidator.DAL_SV, new Double(1.0));

        final List<DBObject> findOnes = new ArrayList<DBObject>();
        findOnes.add(studentDbObject);
        findOnes.add(sectionDbObject);
        findOnes.add(null);

        when(
                mongoTemplate.findOne(Mockito.any(Query.class), Mockito.eq(BasicDBObject.class),
                        Mockito.eq(SliSchemaVersionValidator.METADATA_COLLECTION))).thenAnswer(new Answer<DBObject>() {
            @Override
            public DBObject answer(InvocationOnMock invocation) throws Throwable {
                return findOnes.remove(0);
            }
        });

        this.sliSchemaVersionValidator.initMigration();

        Mockito.verify(mongoTemplate, Mockito.times(1)).updateFirst(Mockito.any(Query.class),
                Mockito.any(Update.class), Mockito.any(String.class));
        Mockito.verify(mongoTemplate, Mockito.times(1)).insert(Mockito.any(Object.class), Mockito.any(String.class));

    }

    @Test
    public void shouldCreateAddStrategy() {
        this.sliSchemaVersionValidator.initMigration();

        List<MigrationStrategy> transforms = this.sliSchemaVersionValidator.getMigrationStrategies("student", 2);

        Assert.assertEquals(1, transforms.size());
        MigrationStrategy strategy = transforms.get(0);
        Assert.assertTrue("Expected AddStrategy", strategy instanceof AddStrategy);
    }
}
