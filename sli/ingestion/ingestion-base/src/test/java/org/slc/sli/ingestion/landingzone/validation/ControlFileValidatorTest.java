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

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Tests for control file validator.
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/validation-context.xml" })
public class ControlFileValidatorTest {

    @Autowired
    ControlFileValidator controlFileValidator;

    private FaultsReport errorReport = new FaultsReport();
    private ControlFileDescriptor item;
    private ControlFile contorlFile;
    private IngestionFileEntry entry;
    private List<IngestionFileEntry> fileEntries;
    private LandingZone lz;
    private File xmlFile;
    private ControlFileValidator cfv;
    private final String fileName = "Test.xml";
    private final String path = String.format("%1$shome%1$slandingzone%1$stest%1$s", File.separator);

    @Before
    public void setup() {
        item = Mockito.mock(ControlFileDescriptor.class);
        lz = Mockito.mock(LandingZone.class);
        contorlFile = Mockito.mock(ControlFile.class);
        entry = Mockito.mock(IngestionFileEntry.class);
        cfv = Mockito.spy(controlFileValidator);
        xmlFile = Mockito.mock(File.class);
        fileEntries = new ArrayList<IngestionFileEntry>();

        Mockito.when(xmlFile.getName()).thenReturn(fileName);
        Mockito.when(xmlFile.getParent()).thenReturn(path);
        Mockito.when(entry.getFileName()).thenReturn(fileName);

        Mockito.when(contorlFile.getFileEntries()).thenReturn(fileEntries);
        Mockito.when(item.getFileItem()).thenReturn(contorlFile);

        Mockito.when(item.getLandingZone()).thenReturn(lz);
        Mockito.when(lz.getLZId()).thenReturn(path);
        Mockito.when(lz.getFile(fileName)).thenReturn(xmlFile);
    }

    @Test
    public void noFileEntriesTest() {
        boolean isValid = controlFileValidator.isValid(item, errorReport);

        Assert.assertEquals("ERROR: No files specified in control file.", errorReport.getFaults().get(0).toString());
        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertFalse(isValid);
    }

    @Test
    public void fileNotPresentTest() {
        Mockito.when(lz.getFile(fileName)).thenReturn(null);
        fileEntries.add(entry);

        boolean isValid = controlFileValidator.isValid(item, errorReport);

        Assert.assertEquals("ERROR: File " + fileName + ": Specified file is missing.", errorReport.getFaults().get(0).toString());
        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertFalse(isValid);
    }

    @Test
    public void fileValidTest() {

        fileEntries.add(entry);
        Mockito.doReturn(true).when(cfv).isValid(Mockito.any(FileEntryDescriptor.class), Mockito.any(ErrorReport.class));

        boolean isValid = cfv.isValid(item, errorReport);

        Assert.assertFalse(errorReport.hasErrors());
        Assert.assertTrue(isValid);
    }

    @Test
    public void fileNotValidTest() {
        fileEntries.add(entry);
        Mockito.doReturn(false).when(cfv).isValid(Mockito.any(FileEntryDescriptor.class), Mockito.any(ErrorReport.class));

        boolean isValid = cfv.isValid(item, errorReport);

        Assert.assertEquals("ERROR: No valid files specified in control file.", errorReport.getFaults().get(0).toString());
        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertFalse(isValid);
    }

    @Test
    public void controlFileHasPath() {
        Mockito.when(entry.getFileName()).thenReturn(path + fileName);
        fileEntries.add(entry);

        boolean isValid = controlFileValidator.isValid(item, errorReport);

        Assert.assertEquals("ERROR: File " + path + fileName + ": File name contains path.", errorReport.getFaults().get(0).toString());
        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertFalse(isValid);
    }
}
