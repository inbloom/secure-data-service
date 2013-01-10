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


package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertNotNull;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.model.NewBatchJob;

/**
 * Tests for ControlFileProcessor
 *
 */
@Ignore
// made obsolete by NewBatchJob integration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class ControlFileProcessorTest {

    @Autowired
    ControlFileProcessor processor;

    @Test
    public void shouldAcceptExchangeObjectReadExchangeControlFileAndSetExchangeBatchJob() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        preObject.getIn().setBody(IngestionTest.getFile("smooks/InterchangeStudentCsv.ctl"));

        Exchange eObject = new DefaultExchange(new DefaultCamelContext());

        eObject.getIn().setHeaders(preObject.getIn().getHeaders());
        eObject.getIn().setBody(preObject.getIn().getBody());

        processor.process(eObject);

        NewBatchJob bj = eObject.getIn().getBody(NewBatchJob.class);

        assertNotNull("BatchJob is not defined", bj);

        boolean hasErrors = (Boolean) eObject.getIn().getHeader("hasErrors");

        assertNotNull("header [hasErrors] not set", hasErrors);
    }

}
