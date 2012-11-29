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
package org.slc.sli.ingestion.smooks;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.concurrent.ExecutionException;
import static org.mockito.Matchers.any;
/**
 * Tests for SliDeltaManager
 *
 * @author ldalgado
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class SliDeltaManagerTest {
    @Mock
    private BatchJobMongoDA mockBatchJobMongoDA;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testIsPreviouslyIngested()  {
        NeutralRecord record = new NeutralRecord();
        record.setRecordType("recordType");
        record.getAttributes().put("key1", "value1");
        record.getAttributes().put("key2", "value2");
        record.getAttributes().put("key3", "value3");
        RecordHash hash = new RecordHash();
        hash.set_id("id");
        hash.setTimestamp("timestamp");
        hash.setTenantId("tenantId");
        TenantContext.setTenantId("tenantId");
        Mockito.when(mockBatchJobMongoDA.findRecordHash(any(String.class), any(String.class))).thenReturn(null);
        Assert.assertFalse(SliDeltaManager.isPreviouslyIngested(record, mockBatchJobMongoDA));
        String fId = (String)record.getMetaData().get("rhId");
        String fTenantId = (String)record.getMetaData().get("rhId");

        Mockito.when(mockBatchJobMongoDA.findRecordHash(any(String.class), any(String.class))).thenReturn(hash);
        Assert.assertTrue(SliDeltaManager.isPreviouslyIngested(record, mockBatchJobMongoDA));
        Assert.assertNotNull(record.getMetaData().get("rhId"));
        Assert.assertNotNull(record.getMetaData().get("rhTenantId"));
        Assert.assertNotNull(record.getMetaData().get("rhTimeStamp"));

        String sId = (String)record.getMetaData().get("rhId");
        String sTenantId = (String)record.getMetaData().get("rhId");

        Assert.assertEquals(fId, sId);
        Assert.assertEquals(fTenantId, sTenantId);
    }
}
