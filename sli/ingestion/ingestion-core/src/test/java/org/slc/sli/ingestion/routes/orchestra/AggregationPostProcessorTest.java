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

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
/**
*
* @author mpatel
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/processor-test.xml" })
public class AggregationPostProcessorTest {

    @Autowired
    AggregationPostProcessor aggregationPostProcessor;

    private static final String JOBID = "MT.ctl-1234235235";
    private static final String RECORD_TYPE = "testRecord";
    private static final List<String> FINISHED_ENTITIES = new ArrayList<String>();

    @Test
    public void testProcess() {
        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        BatchJobDAO dao = Mockito.mock(BatchJobDAO.class);
        FINISHED_ENTITIES.add(RECORD_TYPE);
        Mockito.when(dao.removeAllPersistedStagedEntitiesFromJob(JOBID)).thenReturn(true);
        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(JOBID);
        preObject.getIn().setBody(workNote);

        aggregationPostProcessor.setBatchJobDAO(dao);

        try {
            aggregationPostProcessor.process(preObject);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(workNote.getBatchJobId(), preObject.getIn().getBody(RangedWorkNote.class).getBatchJobId());
        Assert.assertEquals(true, preObject.getIn().getHeader("processedAllStagedEntities"));

    }
}
