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

import java.io.File;

import junitx.util.PrivateAccessor;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * ZipFileProcessor unit tests.
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ZipFileProcessorTest {

    @Autowired
    private ZipFileProcessor zipProc;

    @Test
    public void testHappyZipFileProcessor() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        preObject.getIn().setBody(IngestionTest.getFile("zip/ValidZip.zip"));

        zipProc.process(preObject);

        Assert.assertNotNull(preObject.getIn().getBody());
        Assert.assertEquals("ControlFile.ctl", preObject.getIn().getHeader("ResourceId"));
    }

    @Test
    public void testNoControlFileZipFileProcessor() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        preObject.getIn().setBody(IngestionTest.getFile("zip/NoControlFile.zip"));

        zipProc.process(preObject);

        Assert.assertNotNull(preObject.getIn().getBody());
        Assert.assertTrue((Boolean) preObject.getIn().getHeader("hasErrors"));
        Assert.assertEquals(preObject.getIn().getHeader("IngestionMessageType") , MessageType.BATCH_REQUEST.name());
    }

    @Test
    public void testBatchJobCreation() throws Throwable {
        File zipFile = IngestionTest.getFile("zip/ValidZip.zip");

        NewBatchJob job = (NewBatchJob) PrivateAccessor.invoke(zipProc, "createNewBatchJob", new Class<?>[] { File.class }, new Object[] { zipFile });

        Assert.assertEquals(zipFile.getParent(), job.getTopLevelSourceId());
    }
}
