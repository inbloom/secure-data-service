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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.landingzone.FileResource;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

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
        FileResource zip = new FileResource("src/test/resources/zip/ValidZip.zip");

        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        String ctlFile = zipHandler.handle(zip, report, reportStats);

        Assert.assertFalse(reportStats.hasErrors());
        Assert.assertEquals("ControlFile.ctl", ctlFile);
    }

    @Test
    public void testAbsenceOfZipHandling() {
        FileResource zip = new FileResource("src/test/resources/zip/NoControlFile.zip");

        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        String ctlFile = zipHandler.handle(zip, report, reportStats);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertNull(ctlFile);
    }

    @Test
    public void testIOExceptionHandling() {
        FileResource zip = new FileResource("src/test/resources/zip/NoControlFile2.zip");

        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        String ctlFile = zipHandler.handle(zip, report, reportStats);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertNull(ctlFile);
    }

}
