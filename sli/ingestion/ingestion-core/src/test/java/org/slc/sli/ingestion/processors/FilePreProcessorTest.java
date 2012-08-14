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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.IngestionTest;

/**
 * @author unavani
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class FilePreProcessorTest {

    @Autowired
    private FilePreProcessor filePreProc;

    @Test
    public void testFilePreProcessorWithCtlFile() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());
        preObject.getIn().setBody(IngestionTest.getFile("fileLevelTestData/validXML/validXML.ctl"));

        filePreProc.process(preObject);

        Assert.assertEquals(FileFormat.CONTROL_FILE.getExtension(), preObject.getIn().getHeader("fileType"));
    }

    @Test
    public void testFilePreProcessorWithZipFile() throws Exception {

        Exchange preObject = new DefaultExchange(new DefaultCamelContext());
        preObject.getIn().setBody(IngestionTest.getFile("zip/NoControlFile.zip"));

        filePreProc.process(preObject);

        Assert.assertEquals(FileFormat.ZIP_FILE.getExtension(), preObject.getIn().getHeader("fileType"));
    }

}
