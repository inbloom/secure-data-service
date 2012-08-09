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
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FaultsReport;

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

    @Autowired
    private MessageSource messageSource;

    @Test
    public void testZipHandling() {
        File zip = new File("src/test/resources/zip/ValidZip.zip");

        FaultsReport errorReport = new FaultsReport();

        File ctlFile = zipHandler.handle(zip, errorReport);

        Assert.assertFalse(errorReport.hasErrors());
        Assert.assertNotNull(ctlFile);
        Assert.assertTrue(ctlFile.exists());
    }

    @Test
    public void testAbsenceOfZipHandling() {
        File zip = new File("src/test/resources/zip/NoControlFile.zip");

        FaultsReport errorReport = new FaultsReport();

        File ctlFile = zipHandler.handle(zip, errorReport);

        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertNull(ctlFile);
    }

    @Test
    public void testIOExceptionHandling() {
        File zip = new File("src/test/resources/zip/NoControlFile2.zip");

        ZipFileHandler zipHandler = new ZipFileHandler();
        zipHandler.setMessageSource(messageSource);

        FaultsReport errorReport = new FaultsReport();

        File ctlFile = zipHandler.handle(zip, errorReport);

        Assert.assertTrue(errorReport.hasErrors());
        Assert.assertNull(ctlFile);
    }

}
