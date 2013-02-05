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
package org.slc.sli.ingestion.validation.indexes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
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

        Set<String> tenantCollections = new HashSet<String>();
        tenantCollections.add("assessment");
        tenantCollections.add("assessmentFamily");
        tenantCollections.add("assessmentItem");

        Mockito.when(db.getSisterDB(Matchers.anyString())).thenReturn(db);
        Mockito.when(mongoTemplate.getDb()).thenReturn(db);
        Mockito.when(db.getName()).thenReturn("test");

        List<String> tenantDbs = new ArrayList<String>();
        tenantDbs.add("test");

        Mockito.when(db.getCollectionNames()).thenReturn(tenantCollections);

        Mockito.when(db.collectionExists("assessment")).thenReturn(true);
        Mockito.when(db.collectionExists("assessmentFamily")).thenReturn(true);
        Mockito.when(db.collectionExists("assessmentFamily")).thenReturn(true);

        Mockito.when(db.getCollection("assessment")).thenReturn(assessmentCollection);
        Mockito.when(db.getCollection("assessmentFamily")).thenReturn(assessmentFamilyCollection);
        Mockito.when(db.getCollection("assessmentItem")).thenReturn(assessmentItemCollection);

        List<DBObject> listIndexInfo = new ArrayList<DBObject>();
        DBObject indexInfo = new BasicDBObject();
        DBObject key = new BasicDBObject("creationTime", 1);
        indexInfo.put("unique", false);
        indexInfo.put("key", key);
        listIndexInfo.add(indexInfo);

        List<DBObject> emptyList = new ArrayList<DBObject>();

        Mockito.when(assessmentCollection.getIndexInfo()).thenReturn(listIndexInfo);
        Mockito.when(assessmentFamilyCollection.getIndexInfo()).thenReturn(listIndexInfo);
        Mockito.when(assessmentItemCollection.getIndexInfo()).thenReturn(emptyList);

        Mockito.when(tenantDA.getAllTenantDbs()).thenReturn(tenantDbs);

        Mockito.doCallRealMethod().when(tenantDBIndexValidator).setTenantDA(Matchers.any(TenantDA.class));
        Mockito.doCallRealMethod().when(tenantDBIndexValidator).isValid(Matchers.any(DB.class), Matchers.any(AbstractMessageReport.class), Matchers.any(ReportStats.class), Matchers.any(Source.class));
        Set<MongoIndex> expectedIndex = new HashSet<MongoIndex>();
        DBObject adminDelegationIndex = new BasicDBObject();
        adminDelegationIndex.put("creationTime", 1);
        expectedIndex.add(new MongoIndex("assessment", false, adminDelegationIndex));

        DBObject applicationAuthorizationIndex = new BasicDBObject();
        applicationAuthorizationIndex.put("assessmentFamilyCollection", 1);
        expectedIndex.add(new MongoIndex("assessmentFamilyCollection", false, applicationAuthorizationIndex));

        Mockito.when(tenantDBIndexValidator.loadExpectedIndexes()).thenReturn(expectedIndex);

        tenantDBIndexValidator.setTenantDA(tenantDA);
    }

    @Test
    public void test() throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        tenantDBIndexValidator.setTenantDA(tenantDA);

        Mockito.doCallRealMethod().when(tenantDBIndexValidator).isValid(Matchers.any(DB.class), Matchers.any(List.class), Matchers.any(AbstractMessageReport.class), Matchers.any(ReportStats.class), Matchers.any(Source.class));

        AbstractMessageReport report = Mockito.mock(AbstractMessageReport.class);
        ReportStats reportStats = Mockito.mock(ReportStats.class);
        Source source = Mockito.mock(Source.class);

        tenantDBIndexValidator.isValid(db, report, reportStats, source);

        Mockito.verify(report, Mockito.atLeast(1)).info(Matchers.eq(reportStats), Matchers.eq(source), Matchers.eq(CoreMessageCode.CORE_0018), Matchers.eq("assessment"), Matchers.any(Map.class), Matchers.eq(false));
        Mockito.verify(report, Mockito.atLeast(1)).error(Matchers.eq(reportStats), Matchers.eq(source), Matchers.eq(CoreMessageCode.CORE_0038), Matchers.eq("assessmentFamilyCollection"), Matchers.any(Map.class), Matchers.eq(false));
    }
}
