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


package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.util.MD5;

/**
 * Unit Tests for batch job assembler.
 *
 * @author okrook
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class BatchJobAssemblerTest {

    @Autowired
    private BatchJobAssembler jobAssembler;

    @Autowired
    private LandingZone lz;

    @Autowired
    MessageSource messageSource;

    @Test
    public void testAssembleJobValid() throws IOException {

        // Set up some valid entries
        ArrayList<IngestionFileEntry> entries = new ArrayList<IngestionFileEntry>();
        entries.add(new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, "InterchangeStudent.xml", MD5
                .calculate("InterchangeStudent.xml", getLandingZone())));
        entries.add(new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_ENROLLMENT,
                "InterchangeEnrollment.xml", MD5.calculate("InterchangeEnrollment.xml", getLandingZone())));

        // Set up some valid properties
        Properties props = new Properties();
        props.setProperty("hello", "world");

        ControlFile ctlFile = new ControlFile(null, entries, props);

        Job job = jobAssembler.assembleJob(new ControlFileDescriptor(ctlFile, getLandingZone()));

        assertEquals(2, job.getFiles().size());
        assertEquals("world", job.getProperty("hello"));

    }

    public LandingZone getLandingZone() {
        return lz;
    }

    public void setLandingZone(LandingZone landingZone) {
        this.lz = landingZone;
    }

}
