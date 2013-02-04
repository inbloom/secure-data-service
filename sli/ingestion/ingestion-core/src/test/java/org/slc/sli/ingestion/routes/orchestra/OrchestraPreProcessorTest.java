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

package org.slc.sli.ingestion.routes.orchestra;

import java.util.HashSet;
import java.util.Set;

import junitx.util.PrivateAccessor;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;

/**
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class OrchestraPreProcessorTest {

    private static final String BATCHJOBID = "MT.ctl-1234235235";

    @InjectMocks
    @Autowired
    private OrchestraPreProcessor orchestraPreProcessor;

    @Mock
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Mock
    private NeutralRecordRepository neutralRecordRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {

        String testJobId = "test-id";

        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(BATCHJOBID);
        //get a test tenantRecord
        Exchange ex = new DefaultExchange(new DefaultCamelContext());
        ex.getIn().setBody(workNote);

        Set<String> stagedCollectionNames = new HashSet<String>();

        Mockito.when(neutralRecordMongoAccess.getRecordRepository()).thenReturn(neutralRecordRepository);
        Mockito.when(neutralRecordRepository.getStagedCollectionsForJob()).thenReturn(stagedCollectionNames);

        PrivateAccessor.setField(orchestraPreProcessor, "neutralRecordMongoAccess", neutralRecordMongoAccess);

        orchestraPreProcessor.process(ex);

        Assert.assertEquals(true, ex.getIn().getHeader("stagedEntitiesEmpty"));
        Assert.assertEquals(BATCHJOBID, ex.getIn().getHeader("jobId"));
    }

}
