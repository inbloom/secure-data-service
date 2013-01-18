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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * LogUtil unit-tests
 *
 * @author tshewchuk
 *
 */
public class ZipFileUtilTest {

    private static final File ZIP_FILE_DIR = new File(FileUtils.getTempDirectory(), "ZipFileUtilTest");
    private static final String ZIP_FILE_WITH_NO_DIRS_NAME = "DemoData.zip";
    private static final String ZIP_FILE_WITH_DIRS_NAME = "ZipContainsSubfolder.zip";
    private static final String SUB_DIR_NAME = "DataFiles";

    @BeforeClass
    public static void createZipFileDir() throws IOException, URISyntaxException {
        deleteZipFileDir();

        FileUtils.forceMkdir(ZIP_FILE_DIR);
        File resourceZipFileWithNoDirs = new File(Thread.currentThread().getContextClassLoader()
                .getResource(ZIP_FILE_WITH_NO_DIRS_NAME).toURI());
        File resourceZipFileWithDirs = new File(Thread.currentThread().getContextClassLoader()
                .getResource(ZIP_FILE_WITH_DIRS_NAME).toURI());
        FileUtils.copyFileToDirectory(resourceZipFileWithNoDirs, ZIP_FILE_DIR);
        FileUtils.copyFileToDirectory(resourceZipFileWithDirs, ZIP_FILE_DIR);
    }

    @Test
    public void testExtractTrue() throws IOException {
        // Verify target directory containing extracted zip files is created.
        File zipFile = new File(ZIP_FILE_DIR, ZIP_FILE_WITH_DIRS_NAME);
        File targetDir = new File(ZIP_FILE_DIR, "ExtractTrueTest");

        ZipFileUtil.extract(zipFile, targetDir, true);
        Assert.assertTrue("Creation of target directory " + targetDir.getPath() + " failed", targetDir.isDirectory());

        File targetSubDir = new File(targetDir, SUB_DIR_NAME);
        Assert.assertTrue("Creation of target subdirectory failed", targetSubDir.isDirectory());

        assertTargetMatchesZip(targetDir, zipFile);
    }

    @Test
    public void testExtractFalseGood() throws IOException {
        // Verify target directory contains extracted zip files.
        File zipFile = new File(ZIP_FILE_DIR, ZIP_FILE_WITH_NO_DIRS_NAME);
        File targetDir = new File(ZIP_FILE_DIR, "ExtractFalseGoodTest");
        FileUtils.forceMkdir(targetDir);
        ZipFileUtil.extract(zipFile, targetDir, false);

        assertTargetMatchesZip(targetDir, zipFile);
    }

    @Test(expected = FileNotFoundException.class)
    public void testExtractFalseBad() throws IOException {
        // Verify target subdirectory is not created for zip file with subdirectory.
        File zipFile = new File(ZIP_FILE_DIR, ZIP_FILE_WITH_DIRS_NAME);
        File targetDir = new File(ZIP_FILE_DIR, "ExtractFalseBadTest");
        try {
            FileUtils.forceMkdir(targetDir);
        } catch (IOException e) {
            Assert.fail();  // We catch this exception to differentiate from the expected one.
        }
        ZipFileUtil.extract(zipFile, targetDir, false);
    }

    @Test(expected = FileNotFoundException.class)
    public void testFileInputStreamOnBadZip() throws IOException {
        File zipFile = new File("nozip.zip");

        ZipFileUtil.getInputStreamForFile(zipFile, "doesnotmatter");
    }

    @Test(expected = FileNotFoundException.class)
    public void testTryNoFileInputStreamZip() throws IOException {
        File zipFile = new File(ZIP_FILE_DIR, ZIP_FILE_WITH_NO_DIRS_NAME);

        InputStream is = null;

        try {
            is = ZipFileUtil.getInputStreamForFile(zipFile, "doesnotexist");
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Test
    public void testFileInputStreamZip() throws IOException {
        File zipFile = new File(ZIP_FILE_DIR, ZIP_FILE_WITH_NO_DIRS_NAME);
        File zipCopy = File.createTempFile(UUID.randomUUID().toString(), ".zip");
        FileUtils.copyFile(zipFile, zipCopy);

        InputStream ctlIs = null;
        InputStream xmlIs = null;

        try {
            ctlIs = ZipFileUtil.getInputStreamForFile(zipCopy, "MainControlFile.ctl");
            xmlIs = ZipFileUtil.getInputStreamForFile(zipCopy, "InterchangeStudent.xml");

            Assert.assertNotNull(ctlIs);
            Assert.assertNotNull(xmlIs);
            Assert.assertNotSame(ctlIs, xmlIs);
        } finally {
            IOUtils.closeQuietly(ctlIs);
            IOUtils.closeQuietly(xmlIs);
            FileUtils.deleteQuietly(zipCopy);
        }
    }

    @AfterClass
    public static void deleteZipFileDir() {
        try {
            FileUtils.forceDelete(ZIP_FILE_DIR);
        } catch (IOException e) {
            Assert.assertTrue(true);  // We ignore this exception. Assert is there just to keep
                                     // stylechecker happy.
        }
    }

    private void assertTargetMatchesZip(File targetDir, File zipFile) throws IOException {
        InputStream zipStream = null;
        ZipArchiveInputStream zipFileStrm = null;
        try {
            zipStream = new BufferedInputStream(new FileInputStream(zipFile));
            zipFileStrm = new ZipArchiveInputStream(zipStream);

            ArchiveEntry entry;
            ArrayList<String> zipFileSet = new ArrayList<String>();
            while ((entry = zipFileStrm.getNextEntry()) != null) {
                zipFileSet.add(File.separator + entry.getName().replace('/', File.separatorChar));
            }

            ArrayList<String> extractedFileSet = new ArrayList<String>();
            addExtractedFiles(targetDir, File.separator, extractedFileSet);
            Collections.sort(zipFileSet);
            Collections.sort(extractedFileSet);

            Assert.assertEquals(extractedFileSet, zipFileSet);
        } finally {
            IOUtils.closeQuietly(zipStream);
            IOUtils.closeQuietly(zipFileStrm);
        }
    }

    private void addExtractedFiles(File targetDir, String targetSubDirName, ArrayList<String> extractedFileSet) {
        String[] extractedSubFiles = targetDir.list();
        for (String extractedSubFileName : extractedSubFiles) {
            File extractedSubFile = new File(targetDir, extractedSubFileName);
            if (extractedSubFile.isDirectory()) {
                extractedFileSet.add(targetSubDirName + extractedSubFileName + File.separator);
                addExtractedFiles(extractedSubFile, targetSubDirName + extractedSubFileName + File.separator,
                        extractedFileSet);
            } else {
                extractedFileSet.add(targetSubDirName + extractedSubFileName);
            }
        }
    }

}
