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
package org.slc.sli.validation.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.Annotation.AnnotationType;

/**
 *
 * TODO: add class javadoc
 *
 */
public class NaturalKeyExtractorTest {

    private static final String COLLECTION_TYPE = "StampCollection";

    @InjectMocks
    NaturalKeyExtractor naturalKeyExtractor = new NaturalKeyExtractor();

    @Mock
    SchemaRepository entitySchemaRegistry;

    NeutralSchema mockSchema;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNaturalKeyFields() throws NoNaturalKeysDefinedException {

        Entity e = setup();

        Map<String, Boolean> naturalKeyFields = naturalKeyExtractor.getNaturalKeyFields(e);

        Assert.assertEquals(1, naturalKeyFields.size());
        Assert.assertEquals("someField", naturalKeyFields.entrySet().iterator().next().getKey());
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test
    public void testGetNaturalKeyDescriptor() throws NoNaturalKeysDefinedException {

        Entity e = setup();

        TenantContext.setTenantId("someTenant");
        NaturalKeyDescriptor desc = naturalKeyExtractor.getNaturalKeyDescriptor(e);
        Map<String, String> naturalKeys = desc.getNaturalKeys();

        Assert.assertEquals(COLLECTION_TYPE, desc.getEntityType());
        Assert.assertEquals("someTenant", desc.getTenantId());
        Assert.assertEquals(1, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(2)).getSchema(Mockito.anyString());
    }

    @Test
    public void testGetNaturalKeys() throws NoNaturalKeysDefinedException {

        Entity e = setup();

        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);

        Assert.assertEquals(1, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test(expected = NaturalKeyValidationException.class)
    public void shouldThrowExceptionWhenMissingRequiredKeyField() throws NoNaturalKeysDefinedException {

        Entity e = setup();
        // remove field so that exception is thrown
        e.getBody().remove("someField");

        @SuppressWarnings("unused")
        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);
    }

    @Test
    public void shouldNotThrowExceptionWhenMissingOptionalKeyField() throws NoNaturalKeysDefinedException {

        Entity e = setup();

        // add another optional schema field that is not present
        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfo.applyNaturalKeys()).thenReturn(true);
        Mockito.when(mockAppInfo.getType()).thenReturn(AnnotationType.APPINFO);
        mockSchema.addAnnotation(mockAppInfo);

        NeutralSchema mockFieldSchema = Mockito.mock(NeutralSchema.class);
        mockSchema.addField("optionalField", mockFieldSchema);

        AppInfo mockFieldAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockFieldSchema.getAppInfo()).thenReturn(mockFieldAppInfo);
        Mockito.when(mockFieldAppInfo.isNaturalKey()).thenReturn(true);
        Mockito.when(mockFieldAppInfo.isRequired()).thenReturn(false);

        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);

        Assert.assertEquals(2, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Assert.assertEquals("", naturalKeys.get("optionalField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test
    public void shouldExtractNestedKeyField() throws NoNaturalKeysDefinedException {

        Entity e = setup();
        Map<String, String> parentValue = new HashMap<String, String>();
        parentValue.put("childField", "someNestedValue");
        e.getBody().put("parentField", parentValue);

        // add another optional schema field that is not present
        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfo.applyNaturalKeys()).thenReturn(true);
        Mockito.when(mockAppInfo.getType()).thenReturn(AnnotationType.APPINFO);
        mockSchema.addAnnotation(mockAppInfo);

        // create the parent field schema
        ComplexSchema parentFieldSchema = new ComplexSchema();
        mockSchema.addField("parentField", parentFieldSchema);
        AppInfo mockAppInfoForParent = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfoForParent.isNaturalKey()).thenReturn(true);
        Mockito.when(mockAppInfoForParent.getType()).thenReturn(AnnotationType.APPINFO);
        parentFieldSchema.addAnnotation(mockAppInfoForParent);

        // create the child field schema
        NeutralSchema mockChildFieldSchema = Mockito.mock(NeutralSchema.class);
        AppInfo mockFieldAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockChildFieldSchema.getAppInfo()).thenReturn(mockFieldAppInfo);
        Mockito.when(mockFieldAppInfo.isNaturalKey()).thenReturn(true);
        Mockito.when(mockFieldAppInfo.isRequired()).thenReturn(false);

        // add the child schema to the parent
        parentFieldSchema.addField("childField", mockChildFieldSchema);

        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);

        Assert.assertEquals(2, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Assert.assertEquals("someNestedValue", naturalKeys.get("parentField.childField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test
    public void shouldNotExtractNestedKeyFieldWhenParentFieldIsNotANaturalKey() throws NoNaturalKeysDefinedException {

        Entity e = setup();
        Map<String, String> parentValue = new HashMap<String, String>();
        parentValue.put("childField", "someNestedValue");
        e.getBody().put("parentField", parentValue);

        // add another optional schema field that is not present
        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfo.applyNaturalKeys()).thenReturn(true);
        Mockito.when(mockAppInfo.getType()).thenReturn(AnnotationType.APPINFO);
        mockSchema.addAnnotation(mockAppInfo);

        // create the parent field schema
        ComplexSchema parentFieldSchema = new ComplexSchema();
        mockSchema.addField("parentField", parentFieldSchema);
        AppInfo mockAppInfoForParent = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfoForParent.isNaturalKey()).thenReturn(false);
        Mockito.when(mockAppInfoForParent.getType()).thenReturn(AnnotationType.APPINFO);
        parentFieldSchema.addAnnotation(mockAppInfoForParent);

        // create the child field schema
        NeutralSchema mockChildFieldSchema = Mockito.mock(NeutralSchema.class);
        AppInfo mockFieldAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockChildFieldSchema.getAppInfo()).thenReturn(mockFieldAppInfo);
        Mockito.when(mockFieldAppInfo.isNaturalKey()).thenReturn(true);
        Mockito.when(mockFieldAppInfo.isRequired()).thenReturn(false);

        // add the child schema to the parent
        parentFieldSchema.addField("childField", mockChildFieldSchema);

        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);

        Assert.assertEquals(1, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Assert.assertNull("The nested field should not be extracted", naturalKeys.get("parentField.childField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test
    public void shouldLookupEntityCollection() {

        Entity e = setup();

        // add another optional schema field that is not present
        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);
        mockSchema.addAnnotation(mockAppInfo);

        String collectionName = naturalKeyExtractor.getCollectionName(e);

        Assert.assertEquals(COLLECTION_TYPE, collectionName);
    }

    private Entity setup() {
        String entityType = "entityType";
        Entity e = createEntity(entityType);
        e.getBody().put("someField", "someValue");

        mockSchema = new StringSchema(entityType);
        Mockito.when(entitySchemaRegistry.getSchema(entityType)).thenReturn(mockSchema);

        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfo.applyNaturalKeys()).thenReturn(true);
        Mockito.when(mockAppInfo.getType()).thenReturn(AnnotationType.APPINFO);
        Mockito.when(mockAppInfo.getCollectionType()).thenReturn(COLLECTION_TYPE);
        mockSchema.addAnnotation(mockAppInfo);

        NeutralSchema mockFieldSchema = Mockito.mock(NeutralSchema.class);
        mockSchema.addField("someField", mockFieldSchema);

        AppInfo mockFieldAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockFieldSchema.getAppInfo()).thenReturn(mockFieldAppInfo);
        Mockito.when(mockFieldAppInfo.isNaturalKey()).thenReturn(true);
        Mockito.when(mockFieldAppInfo.isRequired()).thenReturn(true);

        return e;

    }

    private Entity createEntity(final String type) {
        return new Entity() {
            private Map<String, Object> body = new HashMap<String, Object>();
            private Map<String, Object> metadata = new HashMap<String, Object>();

            @Override
            public String getType() {
                return type;
            }

            @Override
            public String getEntityId() {
                return null;
            }

            @Override
            public Map<String, Object> getBody() {
                return body;
            }

            @Override
            public Map<String, Object> getMetaData() {
                return metadata;
            }

            @Override
            public CalculatedData<String> getCalculatedValues() {
                return null;
            }

            @Override
            public CalculatedData<Map<String, Integer>> getAggregates() {
                return null;
            }

            @Override
            public Map<String, List<Entity>> getEmbeddedData() {
                return null;
            }

            @Override
            public Map<String, List<Map<String, Object>>> getDenormalizedData() {
                return null;
            }

            @Override
            public String getStagedEntityId() {
                return null;
            }
        };
    }
}
