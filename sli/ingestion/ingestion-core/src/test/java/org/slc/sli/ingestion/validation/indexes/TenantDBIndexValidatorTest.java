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
package org.slc.sli.ingestion.validation.indexes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.MongoIndex;

/**
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TenantDBIndexValidatorTest {

    private MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
    private TenantDA tenantDA = Mockito.mock(TenantDA.class);
    private DB db = Mockito.mock(DB.class);
    private DBCollection assessmentCollection = Mockito.mock(DBCollection.class);
    private DBCollection assessmentFamilyCollection = Mockito.mock(DBCollection.class);
    private DBCollection assessmentItemCollection = Mockito.mock(DBCollection.class);

    private Map<String, List<MongoIndex>> indexCache = new HashMap<String, List<MongoIndex>>();

    private TenantDBIndexValidator tenantDBIndexValidator = Mockito.mock(TenantDBIndexValidator.class);

    @SuppressWarnings("boxing")
    @Before
    public void setup() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchMethodException {

        List<String> tenantDbs = new ArrayList<String>();
        tenantDbs.add("assessment");
        tenantDbs.add("assessmentFamily");
        tenantDbs.add("assessmentItem");

        Mockito.when(db.getSisterDB(Matchers.anyString())).thenReturn(db);
        Mockito.when(mongoTemplate.getDb()).thenReturn(db);
        Mockito.when(db.getName()).thenReturn("test");

        Mockito.when(db.collectionExists("assessment")).thenReturn(true);
        Mockito.when(db.collectionExists("assessmentFamily")).thenReturn(true);
        Mockito.when(db.collectionExists("assessmentFamily")).thenReturn(true);

        Mockito.when(db.getCollection("assessment")).thenReturn(assessmentCollection);
        Mockito.when(db.getCollection("assessmentFamily")).thenReturn(assessmentFamilyCollection);
        Mockito.when(db.getCollection("assessmentItem")).thenReturn(assessmentItemCollection);

        List<DBObject> listIndexInfo = new ArrayList<DBObject>();
        DBObject indexInfo = new BasicDBObject();
        DBObject key = new BasicDBObject("creationTime",1);
        indexInfo.put("unique", false);
        indexInfo.put("key", key);
        listIndexInfo.add(indexInfo);

        List<DBObject> emptyList = new ArrayList<DBObject>();

        Mockito.when(assessmentCollection.getIndexInfo()).thenReturn(listIndexInfo);
        Mockito.when(assessmentFamilyCollection.getIndexInfo()).thenReturn(listIndexInfo);
        Mockito.when(assessmentItemCollection.getIndexInfo()).thenReturn(emptyList);

        Mockito.when(tenantDA.getAllTenantDbs()).thenReturn(tenantDbs);
/*
        Mockito.doCallRealMethod().when(tenantDBIndexValidator).setMongoTemplate(Matchers.any(MongoTemplate.class));
        Mockito.doCallRealMethod().when(tenantDBIndexValidator).setTenantDA(Matchers.any(TenantDA.class));

        tenantDBIndexValidator.setMongoTemplate(mongoTemplate);
        tenantDBIndexValidator.setTenantDA(tenantDA);

        Mockito.doCallRealMethod().when(tenantDBIndexValidator).setIndexCache((Map<String, List<MongoIndex>>) Matchers.any());
        tenantDBIndexValidator.setIndexCache(indexCache);*/
    }

    @Test
    public void test() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        //Mockito.doCallRealMethod().when(tenantDBIndexValidator).verifyIndexes();
        Mockito.doCallRealMethod().when(tenantDBIndexValidator).checkIndexes(Matchers.any(MongoIndex.class), Matchers.any(DB.class));

        Mockito.doNothing().when(tenantDBIndexValidator).logError(Matchers.anyString());
        Mockito.doNothing().when(tenantDBIndexValidator).logInfo(Matchers.anyString());
        //tenantDBIndexValidator.verifyIndexes();
        Mockito.verify(tenantDBIndexValidator, Mockito.atLeast(1)).logError("Index missing: assessmentItem { \"creationTime\" : 1}, unique:false");
        Mockito.verify(tenantDBIndexValidator, Mockito.atLeast(1)).logInfo("Index verified: assessment { \"creationTime\" : 1}, unique:false");
        Mockito.verify(tenantDBIndexValidator, Mockito.atLeast(1)).logInfo("Index verified: assessmentFamily { \"creationTime\" : 1}, unique:false");

    }
}
