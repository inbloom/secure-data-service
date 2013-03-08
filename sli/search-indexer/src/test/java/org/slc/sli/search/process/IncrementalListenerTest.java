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
package org.slc.sli.search.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.process.impl.IncrementalListenerImpl;
import org.slc.sli.search.transform.EntityConverterFactory;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.transform.impl.GenericEntityConverter;

/**
 * Test class for the Sarje incremental update listener
 *
 * @author dwu
 *
 */
@SuppressWarnings("unchecked")
public class IncrementalListenerTest {

    private String opLogInsert;
    private String opLogUpdate;
    private String opLogDelete;

    private final IncrementalListenerImpl listener = new IncrementalListenerImpl();
    private final IndexEntityConverter indexEntityConverter = new IndexEntityConverter();
    private final EntityConverterFactory entityConverterFactory = new EntityConverterFactory();
    private final GenericEntityConverter genericEntityConverter = new GenericEntityConverter();

    @Before
    public void init() throws Exception {

        indexEntityConverter.setDecrypt(false);
        genericEntityConverter.setIndexConfigStore(new IndexConfigStore("index-config-test.json"));
        entityConverterFactory.setGenericEntityConverter(genericEntityConverter);
        indexEntityConverter.setEntityConverterFactory(entityConverterFactory);
        listener.setIndexEntityConverter(indexEntityConverter);
        // read in test oplog messages
        File inFile = new File(Thread.currentThread().getContextClassLoader().getResource("studentOpLog.json").getFile());
        BufferedReader br = new BufferedReader(new FileReader(inFile));
        opLogInsert = br.readLine();
        // System.out.println(opLogInsert);
        opLogUpdate = br.readLine();
        // System.out.println(opLogUpdate1);
        opLogDelete = br.readLine();
    }

    /**
     * Test oplog insert -> index entity conversion
     */
    @Test
    public void testInsert() throws Exception {
        // convert to index entity
        List<IndexEntity> entity = listener.processEntities(opLogInsert);

        // check result
        Assert.assertEquals("index", entity.get(0).getActionValue());
        Assert.assertEquals("4ef33d4356e3e757e5c3662e6a79ddbfd8b31866_id", entity.get(0).getId());
        Assert.assertEquals("student", entity.get(0).getType());
        Assert.assertEquals("midgar", entity.get(0).getIndex());
        Map<String, Object> name = (Map<String, Object>) entity.get(0).getBody().get("name");
        Assert.assertEquals(name.get("firstName"), "ESTRING:oF9iD6JYVIXWiLxhlEY5Rw==");
        Assert.assertEquals(name.get("lastSurname"), "ESTRING:B8eYiF6KTM4Fab9/A1lHsQ==");
    }

    /**
     * Test oplog update -> index entity conversion
     * Updates the entire body and metadata
     *
     * @throws Exception
     */
    @Test
    public void testUpdate() throws Exception {

        // convert to index entity
        List<IndexEntity> entity = listener.processEntities(opLogUpdate);

        // check result
        Assert.assertEquals(entity.get(0).getActionValue(), "index");
        Assert.assertEquals(entity.get(0).getId(), "067198fd6da91e1aa8d67e28e850f224d6851713_id");
        Assert.assertEquals(entity.get(0).getType(), "student");
        Assert.assertEquals(entity.get(0).getIndex(), "midgar");
        Map<String, Object> name = (Map<String, Object>) entity.get(0).getBody().get("name");
        // updated name
        Assert.assertEquals(name.get("lastSurname"), "ESTRING:eQhKVMY2pD1swnuIyLvSxA==");
        Assert.assertEquals(name.get("firstName"), "ESTRING:xctp43ByzulEIH6YylKuGQ==");
    }

    /**
     * Test oplog delete -> index entity conversion
     */
    @Test
    public void testDelete() throws Exception {

        // convert to index entity
        List<IndexEntity> entity = listener.processEntities(opLogDelete);

        // check result
        Assert.assertEquals(entity.get(0).getActionValue(), "delete");
        Assert.assertEquals(entity.get(0).getId(), "4ef33d4356e3e757e5c3662e6a79ddbfd8b31866_id");
        Assert.assertEquals(entity.get(0).getType(), "student");
        Assert.assertEquals(entity.get(0).getIndex(), "midgar");

    }

}
