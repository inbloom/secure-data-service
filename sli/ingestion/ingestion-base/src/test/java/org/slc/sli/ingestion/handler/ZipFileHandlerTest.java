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

package org.slc.sli.ingestion.handler;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.DummyMessageReport;
import org.slc.sli.ingestion.reporting.JobSource;
import org.slc.sli.ingestion.reporting.SimpleReportStats;

/**
 * ZipFileHandler unit tests.
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ZipFileHandlerTest {

    @Autowired
    private ZipFileHandler zipHandler;

    @Test
    public void testZipHandling() {
        File zip = new File("src/test/resources/zip/ValidZip.zip");

        AbstractMessageReport report = new DummyMessageReport();
        AbstractReportStats reportStats = new SimpleReportStats(new JobSource(null, null, null));

        File ctlFile = zipHandler.handle(zip, report, reportStats);

        Assert.assertFalse(reportStats.hasErrors());
        Assert.assertNotNull(ctlFile);
        Assert.assertTrue(ctlFile.exists());
    }

    @Test
    public void testAbsenceOfZipHandling() {
        File zip = new File("src/test/resources/zip/NoControlFile.zip");

        AbstractMessageReport report = new DummyMessageReport();
        AbstractReportStats reportStats = new SimpleReportStats(new JobSource(null, null, null));

        File ctlFile = zipHandler.handle(zip, report, reportStats);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertNull(ctlFile);
    }

    @Test
    public void testIOExceptionHandling() {
        File zip = new File("src/test/resources/zip/NoControlFile2.zip");

        AbstractMessageReport report = new DummyMessageReport();
        AbstractReportStats reportStats = new SimpleReportStats(new JobSource(null, null, null));

        File ctlFile = zipHandler.handle(zip, report, reportStats);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertNull(ctlFile);
    }

}
