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

package org.slc.sli.ingestion.routes;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 *
 * @author npandey
 *
 */
public class BatchJobManagerTest {

    @InjectMocks
    BatchJobManager batchJobManager = new BatchJobManager();

    @Mock
    protected BatchJobDAO batchJobDAO;

    NewBatchJob job = null;

    String jobId = "jobId";

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        job = Mockito.mock(NewBatchJob.class);
        Mockito.when(batchJobDAO.findBatchJobById(Mockito.anyString())).thenReturn(job);
    }

    @Test
    public void testPurgeDisableDeltas() throws Exception {

        Mockito.when(batchJobDAO.getDuplicateDetectionMode(jobId)).thenReturn(RecordHash.RECORD_HASH_MODE_DISABLE);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        WorkNote workNote = new WorkNote(jobId, false);
        exchange.getIn().setBody(workNote);

        boolean isEligible = batchJobManager.isEligibleForDeltaPurge(exchange);

        Assert.assertTrue(isEligible);

    }

    @Test
    public void testPurgeResetDeltas() throws Exception {
        Mockito.when(batchJobDAO.getDuplicateDetectionMode(jobId)).thenReturn(RecordHash.RECORD_HASH_MODE_RESET);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        WorkNote workNote = new WorkNote(jobId, false);
        exchange.getIn().setBody(workNote);

        boolean isEligible = batchJobManager.isEligibleForDeltaPurge(exchange);

        Assert.assertTrue(isEligible);
    }

    @Test
    public void testNoPurgeDeltas() throws Exception {
        Mockito.when(batchJobDAO.getDuplicateDetectionMode(jobId)).thenReturn(null);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        WorkNote workNote = new WorkNote(jobId, false);
        exchange.getIn().setBody(workNote);

        boolean isEligible = batchJobManager.isEligibleForDeltaPurge(exchange);

        Assert.assertFalse(isEligible);
    }

}
