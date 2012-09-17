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
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.Annotation.AnnotationType;

public class NaturalKeyExtractorTest {

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
    public void testGetNaturalKeyFields() {

        Entity e = setup();

        List<String> naturalKeyFields = naturalKeyExtractor.getNaturalKeyFields(e);

        Assert.assertEquals(1, naturalKeyFields.size());
        Assert.assertEquals("someField", naturalKeyFields.get(0));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test
    public void testGetNaturalKeyDescriptor() {

        Entity e = setup();

        NaturalKeyDescriptor desc = naturalKeyExtractor.getNaturalKeyDescriptor(e);
        Map<String, String> naturalKeys = desc.getNaturalKeys();

        Assert.assertEquals("entityType", desc.getEntityType());
        Assert.assertEquals("someTenant", desc.getTenantId());
        Assert.assertEquals(1, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test
    public void testGetNaturalKeys() {

        Entity e = setup();

        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);

        Assert.assertEquals(1, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    @Test(expected = NaturalKeyValidationException.class)
    public void shouldThrowExceptionWhenMissingRequiredKeyField() {

        Entity e = setup();
        // remove field so that exception is thrown
        e.getBody().remove("someField");

        @SuppressWarnings("unused")
        Map<String, String> naturalKeys = naturalKeyExtractor.getNaturalKeys(e);
    }

    @Test
    public void shouldNotThrowExceptionWhenMissingOptionalKeyField() {

        Entity e = setup();

        //add another optional schema field that is not present
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

        Assert.assertEquals(1, naturalKeys.size());
        Assert.assertEquals("someValue", naturalKeys.get("someField"));
        Mockito.verify(entitySchemaRegistry, Mockito.times(1)).getSchema(Mockito.anyString());
    }

    private Entity setup() {
        String entityType = "entityType";
        Entity e = createEntity(entityType);
        e.getBody().put("someField", "someValue");
        e.getMetaData().put("tenantId", "someTenant");

        mockSchema = new StringSchema(entityType);
        Mockito.when(entitySchemaRegistry.getSchema(entityType)).thenReturn(mockSchema);

        AppInfo mockAppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(mockAppInfo.applyNaturalKeys()).thenReturn(true);
        Mockito.when(mockAppInfo.getType()).thenReturn(AnnotationType.APPINFO);
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
            public String getStagedEntityId() {
                return null;
            }
        };
    }
}
