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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.Assert;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * LogUtil unit-tests
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ZipFileUtilTest {

    private static final File ZIP_FILE_DIR = new File("/tmp/ZipFileUtilTest");
    private static final String ZIP_FILE_NAME = "DemoData.zip";

    @BeforeClass
    public static void createZipFileDir() throws IOException, URISyntaxException {
        try {
            FileUtils.forceDelete(ZIP_FILE_DIR);
        } catch (IOException e) {
            Assert.assertTrue(true);  // Do nothing.
        }

        FileUtils.forceMkdir(ZIP_FILE_DIR);
        File ResourceZipFile = new File(Thread.currentThread().getContextClassLoader().getResource(ZIP_FILE_NAME)
                .toURI());
        FileUtils.copyFileToDirectory(ResourceZipFile, ZIP_FILE_DIR);
    }

    @Test
    public void testExtract() throws IOException {
        // Verify file is zipped to correct extract target directory.
        File zipFile = new File(ZIP_FILE_DIR.getPath() + "/" + ZIP_FILE_NAME);
        File targetDir = ZipFileUtil.extract(zipFile);
        Assert.assertTrue("Zip extraction directory " + targetDir.getPath() + " does not exist",
                targetDir.isDirectory());
        ZipArchiveInputStream zipFileStrm = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(
                zipFile)));
        ArchiveEntry entry;
        ArrayList<String> zipFileSet = new ArrayList<String>();
        while ((entry = zipFileStrm.getNextEntry()) != null) {
            zipFileSet.add(entry.getName());
        }
        IOUtils.closeQuietly(zipFileStrm);
        String[] extractedFiles = targetDir.list();
        ArrayList<String> extractedFileSet = new ArrayList<String>();
        Collections.addAll(extractedFileSet, extractedFiles);
        Collections.sort(zipFileSet);
        Collections.sort(extractedFileSet);
        Assert.assertEquals("Zipped files do not match extracted files in target directory " + targetDir.getPath(),
                extractedFileSet, zipFileSet);
    }

    @Test
    public void testFindCtlFile() throws IOException {
        // Create a target directory containing extracted zip files (including control file).
        File zipFile = new File(ZIP_FILE_DIR.getPath() + "/" + ZIP_FILE_NAME);
        File targetDir = new File(ZIP_FILE_DIR.getPath() + "/" + "FindCtlFileTest");
        if (!targetDir.mkdir()) {
            Assert.fail("Creation of target directory " + targetDir.getPath() + " failed");
        }

        // Verify control file is correctly identified in target directory.
        ZipFileUtil.extract(zipFile, targetDir, true);
        String ctlFilePath = new File(targetDir + "/MainControlFile.ctl").getPath();
        File ctlFile = ZipFileUtil.findCtlFile(targetDir);
        Assert.assertNotNull(ctlFilePath + " not found", ctlFile);
        Assert.assertEquals("Found control file " + ctlFile.getPath() + " does not match expected file " + ctlFilePath,
                ctlFilePath, ctlFile.getPath());
    }

    @Test
    public void testNotFindCtlFile() throws IOException {
        // Create a target directory containing no control files.
        File targetDir = new File(ZIP_FILE_DIR.getPath() + "/" + "NotFindCtlFileTest");
        if (!targetDir.mkdir()) {
            Assert.fail("Creation of target directory " + targetDir.getPath() + " failed");
        }

        // Verify control file file is not found in target directory.
        File ctlFile = ZipFileUtil.findCtlFile(targetDir);
        Assert.assertNull("Control file exists in " + targetDir.getPath(), ctlFile);
    }

    @AfterClass
    public static void deleteZipFileDir() {
        try {
            FileUtils.forceDelete(ZIP_FILE_DIR);
        } catch (IOException e) {
            Assert.assertTrue(true);  // Do nothing.
        }
    }

}
