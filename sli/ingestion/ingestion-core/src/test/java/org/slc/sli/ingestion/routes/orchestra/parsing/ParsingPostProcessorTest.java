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

package org.slc.sli.ingestion.routes.orchestra.parsing;

import java.io.File;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Test the ParsingPostProcessor class
 * @author ablum
 *
 */
public class ParsingPostProcessorTest {

    ParsingPostProcessor processor = new ParsingPostProcessor();

    @Test
    public void testProcess() throws Exception {
        Exchange exchange =  new DefaultExchange(new DefaultCamelContext());
        IngestionFileEntry entry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PROGRAM, "fileName", "111");
        Resource file = new ClassPathResource("zip/ValidZip.zip");
        File fileCopy = new File(file.getFile().getParent() + "ValidZipCopy.zip");
        FileUtils.copyFile(file.getFile(), fileCopy);
        entry.setFileZipParent(fileCopy.getAbsolutePath());
        FileEntryWorkNote workNote = new FileEntryWorkNote("batchJobId", entry, 3);

        exchange.getIn().setBody(workNote, FileEntryWorkNote.class);

        Assert.assertTrue(fileCopy.exists());

        processor.process(exchange);

        Assert.assertFalse(fileCopy.exists());

    }
}
