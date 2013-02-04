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
package org.slc.sli.validation;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Test SelfReferenceExtractor
 * @author ablum
 */
public class SelfReferenceExtractorTest {
    private static final String SELF_REFERENCE_COLLECTION = "school";
    private static final String NON_SELF_REFERENCE_COLLECTION = "student";

    @Spy
    @InjectMocks
    SelfReferenceExtractor selfReferenceExtractor = new SelfReferenceExtractor();

    @Mock
    SchemaRepository entitySchemaRegistry;

    @Mock
    AppInfo srAppInfo;

    @Mock
    NeutralSchema srSchema;

    @Mock
    AppInfo nonSrAppInfo;

    @Mock
    NeutralSchema nonSrSchema;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        NeutralSchema field1 = Mockito.mock(NeutralSchema.class);
        AppInfo field1AppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(field1.getAppInfo()).thenReturn(field1AppInfo);
        Mockito.when(field1AppInfo.isSelfReference()).thenReturn(false);
        Mockito.when(field1.getValidatorClass()).thenReturn("");

        NeutralSchema field2 = Mockito.mock(NeutralSchema.class);
        AppInfo field2AppInfo = Mockito.mock(AppInfo.class);
        Mockito.when(field2.getAppInfo()).thenReturn(field2AppInfo);
        Mockito.when(field2AppInfo.isSelfReference()).thenReturn(true);

        Map<String, NeutralSchema> fields = new HashMap<String, NeutralSchema>();
        fields.put("field1", field1);
        fields.put("field2", field2);

        Mockito.when(selfReferenceExtractor.getSchemaFields(srSchema)).thenReturn(fields);

        Mockito.when(entitySchemaRegistry.getSchema(SELF_REFERENCE_COLLECTION)).thenReturn(srSchema);
        Mockito.when(srSchema.getAppInfo()).thenReturn(srAppInfo);

        Mockito.when(entitySchemaRegistry.getSchema(NON_SELF_REFERENCE_COLLECTION)).thenReturn(nonSrSchema);
        Mockito.when(nonSrSchema.getAppInfo()).thenReturn(nonSrAppInfo);

    }

    @Test
    public void testGetSelfReferenceFields() {
        Entity srEntity = Mockito.mock(Entity.class);
        Mockito.when(srEntity.getType()).thenReturn(SELF_REFERENCE_COLLECTION);

        Entity nonSrEntity = Mockito.mock(Entity.class);
        Mockito.when(nonSrEntity.getType()).thenReturn(NON_SELF_REFERENCE_COLLECTION);

        String selfReferenceField = selfReferenceExtractor.getSelfReferenceFields(srEntity);

        Assert.assertEquals("field2", selfReferenceField);

        selfReferenceField = selfReferenceExtractor.getSelfReferenceFields(nonSrEntity);

        Assert.assertEquals(null, selfReferenceField);
    }
}
