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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * DidRefConfig unit tests.
 *
 */
public class DidRefConfigTest {
    @Test
    public void shouldParseJson() throws Exception {
        Resource jsonFile = new ClassPathResource("DeterministicIdResolverConfigs/test_DID_ref_config.json");
        DidRefConfig testRefConfig = DidRefConfig.parse(jsonFile.getInputStream());

        Assert.assertNotNull(testRefConfig);

        Assert.assertEquals("entity_type", testRefConfig.getEntityType());

        List<KeyFieldDef> keyFields = testRefConfig.getKeyFields();
        Assert.assertNotNull(keyFields);
        Assert.assertEquals(2, keyFields.size());

        KeyFieldDef firstKeyField = keyFields.get(0);
        KeyFieldDef secondKeyField = keyFields.get(1);

        Assert.assertNotNull(firstKeyField);
        Assert.assertNotNull(secondKeyField);

        Assert.assertEquals("key_field_1", firstKeyField.getKeyFieldName());
        Assert.assertNull(firstKeyField.getRefConfig());
        Assert.assertNotNull(firstKeyField.getValueSource());
        Assert.assertEquals("key_field_source_path_1", firstKeyField.getValueSource());

        Assert.assertEquals("key_field_2", secondKeyField.getKeyFieldName());
        Assert.assertNull(secondKeyField.getValueSource());

        DidRefConfig nestedRefConfig = secondKeyField.getRefConfig();
        Assert.assertNotNull(nestedRefConfig);
        Assert.assertEquals("nested_entity_type", nestedRefConfig.getEntityType());
        Assert.assertNotNull(nestedRefConfig.getKeyFields());
        Assert.assertEquals(1, nestedRefConfig.getKeyFields().size());

        KeyFieldDef nestedKeyField = nestedRefConfig.getKeyFields().get(0);
        Assert.assertNotNull(nestedKeyField);
        Assert.assertNull(nestedKeyField.getRefConfig());
        Assert.assertNotNull(nestedKeyField.getValueSource());
        Assert.assertEquals("key_field_3", nestedKeyField.getKeyFieldName());
        Assert.assertEquals("key_field_source_path_3", nestedKeyField.getValueSource());
    }
}
