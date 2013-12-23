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
package org.slc.sli.search.transform;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.impl.LoaderImpl;
import org.slc.sli.search.transform.impl.GenericEntityConverter;
import org.slc.sli.search.util.DotPath;
import org.slc.sli.search.util.IndexEntityUtil;
import org.slc.sli.search.util.SearchIndexerException;

public class IndexEntityConverterTest {
    private final IndexEntityConverter indexEntityConverter = new IndexEntityConverter();
    private final EntityConverterFactory entityConverterFactory = new EntityConverterFactory();
    private final GenericEntityConverter genericEntityConverter = new GenericEntityConverter();

    private static final Logger LOG = LoggerFactory.getLogger(LoaderImpl.class);

    @Before
    public void setup() throws Exception{
        indexEntityConverter.setDecrypt(false);
        indexEntityConverter.setIndexConfigStore(new IndexConfigStore("index-config-test.json"));
        entityConverterFactory.setGenericEntityConverter(genericEntityConverter);
        indexEntityConverter.setEntityConverterFactory(entityConverterFactory);
    }

    @Test
    public void testToIndexEntity() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"name\":\"a\", \"a\":\"1\", \"b\":\"x\"}, \"metaData\": {\"tenantId\": \"tenant\"}}";
        List<IndexEntity> indexEntities = indexEntityConverter.fromEntityJson("tenant", entity);
        IndexEntity indexEntity = indexEntities.get(0);
        Assert.assertEquals("a", indexEntity.getId());
        Assert.assertEquals("tenant", indexEntity.getIndex());
        Assert.assertEquals("student", indexEntity.getType());
        Assert.assertEquals("{\"a\":\"1\",\"metaData\":{\"contextId\":\"x\"},\"append\":\"ALL\"}", IndexEntityUtil.getBody(Action.INDEX, indexEntity.getBody()));
    }

    @Test
    public void testFilter() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"name\":\"a\", \"a\":\"1\", \"b\":\"x\"}, \"test\": { \"filter\": \"notnull\"}, \"metaData\": {\"tenantId\": \"tenant\"}}";
        List<IndexEntity> indexEntities = indexEntityConverter.fromEntityJson("tenant", entity);
        Assert.assertEquals(0, indexEntities.size());
    }

    @Test
    public void testException() throws Exception {
        String entity = "{\"_id\": \"1\", \"type\": \"test\", \"body\":{\"b\":\"x\"}}";
        try {
          indexEntityConverter.fromEntityJson(null, entity);
          Assert.fail("Does not include metaData - should fail");
        } catch (SearchIndexerException sie) {
            LOG.info("There was a SearchIndexerException exception.", sie);
        }
    }

    @Test
    public void testConvertBlankConfigNullIndexNoDecrypt()
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        String entityType = "student";

        Map<String, Object> encryptedBody = new HashMap<String, Object>();
        encryptedBody.put("field1", "Encrypted value");
        Map<String, Object> decryptedBody = new HashMap<String, Object>();
        decryptedBody.put("field1", "Decrypted value");

        setupConverter(entityType, encryptedBody, decryptedBody, null, null);

        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", ("Tenant1"));

        String id = "id1";

        Map<String, Object> entity = makeEntity(id, entityType, encryptedBody, metaData);

        Action action = IndexEntity.Action.QUICK_UPDATE;

        IndexEntity ie = indexEntityConverter.convert(null, action, entity, false);

        Assert.assertEquals("tenant1", ie.getIndex());
        Assert.assertEquals(action.getType(), ie.getActionValue());
        Assert.assertEquals(id, ie.getId());
        Assert.assertEquals(encryptedBody, ie.getBody());
        Assert.assertEquals(metaData, ie.getMetaData());
    }

    @Test
    public void testConvertFullConfigWithIndexAndDecrypt()
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        String entityType = "student";

        Map<String, Object> encryptedBody = new HashMap<String, Object>();
        encryptedBody.put("field1", "Encrypted value");
        Map<String, Object> decryptedBody = new HashMap<String, Object>();
        decryptedBody.put("field1", "Decrypted value");

        setupConverter(entityType, encryptedBody, decryptedBody, "indexType1", null);

        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", ("Tenant1"));

        String id = "id1";

        Map<String, Object> entity = makeEntity(id, entityType, encryptedBody, metaData);

        String index = "index1";

        Action action = IndexEntity.Action.INDEX;

        IndexEntity ie = indexEntityConverter.convert(index, action, entity, true);

        Assert.assertEquals(index, ie.getIndex());
        Assert.assertEquals(IndexEntity.Action.UPDATE.getType(), ie.getActionValue());
        Assert.assertEquals(id, ie.getId());
        Assert.assertEquals(decryptedBody, ie.getBody());
        Assert.assertEquals(metaData, ie.getMetaData());
    }

    @Test
    public void testConvertTransformDoesntMatch()
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        String entityType = "student";

        Map<String, Object> encryptedBody = new HashMap<String, Object>();
        encryptedBody.put("field1", "Encrypted value");
        Map<String, Object> decryptedBody = new HashMap<String, Object>();
        decryptedBody.put("field1", "Decrypted value");

        HashMap<DotPath, Object> condition = new HashMap<DotPath, Object>();
        condition.put(new DotPath(), null);
        setupConverter(entityType, encryptedBody, decryptedBody, null, condition);

        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", ("Tenant1"));

        String id = "id1";

        Map<String, Object> entity = makeEntity(id, entityType, encryptedBody, metaData);

        Action action = IndexEntity.Action.QUICK_UPDATE;

        IndexEntity ie = indexEntityConverter.convert(null, action, entity, false);

        Assert.assertNull(ie);
    }

    @Test
    public void testConvertNullBody()
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        String entityType = "student";

        Map<String, Object> encryptedBody = null;
        Map<String, Object> decryptedBody = null;

        setupConverter(entityType, encryptedBody, decryptedBody, null, null);

        Map<String, Object> metaData = new HashMap<String, Object>();
        metaData.put("tenantId", ("Tenant1"));

        String id = "id1";

        Map<String, Object> entity = makeEntity(id, entityType, encryptedBody, metaData);

        Action action = IndexEntity.Action.QUICK_UPDATE;

        IndexEntity ie = indexEntityConverter.convert(null, action, entity, false);

        Assert.assertNull(ie);
    }

    private void setupConverter(String entityType, Map<String, Object> encryptedBody,
            Map<String, Object> decryptedBody, String indexType, Map<DotPath, Object> condition)
                    throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        EntityEncryption entityEncryption = Mockito.mock(EntityEncryption.class);
        Mockito.when(entityEncryption.decrypt(Mockito.eq(entityType), Mockito.eq(encryptedBody))).
                thenReturn(decryptedBody);
        indexEntityConverter.setEntityEncryption(entityEncryption);

        IndexConfig config = new IndexConfig();
        Field renameField = IndexConfig.class.getDeclaredField("rename");
        renameField.setAccessible(true);
        renameField.set(config, null);
        Field conditionField = IndexConfig.class.getDeclaredField("condition");
        conditionField.setAccessible(true);
        conditionField.set(config, condition);
        Field indexTypeField = IndexConfig.class.getDeclaredField("indexType");
        indexTypeField.setAccessible(true);
        indexTypeField.set(config, indexType);
        Field fieldsField = IndexConfig.class.getDeclaredField("fields");
        fieldsField.setAccessible(true);
        fieldsField.set(config, Arrays.asList("body"));
        config.prepare(entityType);
        IndexConfigStore indexConfigStore = Mockito.mock(IndexConfigStore.class);
        Mockito.when(indexConfigStore.getConfig(Mockito.eq(entityType))).thenReturn(config);
        indexEntityConverter.setIndexConfigStore(indexConfigStore);
    }

    private Map<String, Object> makeEntity(String id, String entityType,
            Map<String, Object> encryptedBody, Map<String, Object> metaData) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("_id", id);
        entity.put("type", entityType);
        entity.put("body", encryptedBody);
        entity.put("metaData", metaData);

        return entity;
    }

}
