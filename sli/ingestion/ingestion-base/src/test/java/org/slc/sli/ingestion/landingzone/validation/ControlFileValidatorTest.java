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

package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 * Tests for control file validator.
 *
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ControlFileValidatorTest {

    @Autowired
    ControlFileValidator controlFileValidator;

    private ControlFile controlFile;
    private IngestionFileEntry entry;
    private List<IngestionFileEntry> fileEntries;
    private File xmlFile;
    private ControlFileValidator cfv;
    private final String fileName = "Test.xml";
    private final String path = String.format("%1$shome%1$slandingzone%1$stest%1$s", File.separator);

    @Before
    public void setup() {
        controlFile = Mockito.mock(ControlFile.class);
        entry = Mockito.mock(IngestionFileEntry.class);
        cfv = Mockito.spy(controlFileValidator);
        xmlFile = Mockito.mock(File.class);
        fileEntries = new ArrayList<IngestionFileEntry>();

        Mockito.when(xmlFile.getName()).thenReturn(fileName);
        Mockito.when(xmlFile.getParent()).thenReturn(path);
        Mockito.when(entry.getFileName()).thenReturn(fileName);

        Mockito.when(controlFile.getFileEntries()).thenReturn(fileEntries);
    }

    @Test
    public void noFileEntriesTest() {
        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();
        Source source = new JobSource(null);

        boolean isValid = controlFileValidator.isValid(controlFile, report, reportStats, source);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertFalse(isValid);
    }

    @Test
    public void fileNotPresentTest() {
        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();
        Source source = new JobSource(null);

        Mockito.when(entry.isValid()).thenReturn(false);
        fileEntries.add(entry);

        boolean isValid = controlFileValidator.isValid(controlFile, report, reportStats, source);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertFalse(isValid);
    }

    @Test
    public void fileValidTest() {
        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        fileEntries.add(entry);
        Mockito.doReturn(true)
                .when(cfv)
                .isValid(Mockito.any(IngestionFileEntry.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class), Mockito.any(Source.class));

        boolean isValid = cfv.isValid(controlFile, report, reportStats, null);

        Assert.assertFalse(reportStats.hasErrors());
        Assert.assertTrue(isValid);
    }

    @Test
    public void fileNotValidTest() {
        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();
        Source source = new JobSource(null);

        fileEntries.add(entry);
        Mockito.doReturn(false)
                .when(cfv)
                .isValid(Mockito.any(IngestionFileEntry.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class), Mockito.any(Source.class));

        boolean isValid = cfv.isValid(controlFile, report, reportStats, source);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertFalse(isValid);
    }

    @Test
    public void controlFileHasPath() {
        AbstractMessageReport report = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();
        Source source = new JobSource(null);

        Mockito.when(entry.getFileName()).thenReturn(path + fileName);
        fileEntries.add(entry);

        boolean isValid = controlFileValidator.isValid(controlFile, report, reportStats, source);

        Assert.assertTrue(reportStats.hasErrors());
        Assert.assertFalse(isValid);
    }
}
