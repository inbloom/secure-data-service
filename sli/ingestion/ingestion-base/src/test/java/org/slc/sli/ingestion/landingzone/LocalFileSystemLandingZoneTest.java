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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.ingestion.util.MD5;

/**
 * Test for the LocalFileSystemlandingZone
 *
 */
public class LocalFileSystemLandingZoneTest {

    LocalFileSystemLandingZone lz;

    public static final String DUMMY_DIR = FilenameUtils.normalize("src/test/resources/dummylz");

    public static final String DUMMY_FILE_CTL = "dummycontrolfile.txt";
    public static final String DUMMY_FILE_CTL_MD5HEX = "8c5dd6c07296fdcfefa199a9f1bd220e";

    public static final String DUMMY_FILE_NOT_EXISTS = "does_not_exist.txt";

    @Before
    public void setUp() {
        lz = new LocalFileSystemLandingZone(new File(DUMMY_DIR));
    }

    @After
    public void tearDown() {
        lz = null;
    }

    @Test
    public void testGetSetDirectory() {

        lz.directory = new File(".");
        assertEquals("directory does not contain expected value",
                new File("."), lz.getDirectory());

        lz.directory = new File(DUMMY_DIR);
        assertEquals("directory does not contain expected value (2)",
                new File(DUMMY_DIR), lz.getDirectory());
    }

    @Test
    public void testGetFile() throws IOException {
        File f = lz.getFile(DUMMY_FILE_CTL);
        assertEquals("file does not contain expected value",
                new File(DUMMY_DIR + File.separator + DUMMY_FILE_CTL), f);
    }

    @Test
    public void testCreateFile() throws IOException {
        // ensure the file doesn't exist
        File f = FileUtils.getFile(lz.getDirectory(), DUMMY_FILE_NOT_EXISTS);
        FileUtils.deleteQuietly(f);
        f = lz.createFile(DUMMY_FILE_NOT_EXISTS);
        assertTrue("file was not created", f.exists());
        FileUtils.deleteQuietly(f);
    }

    @Test(expected = IOException.class)
    public void testCreateFileAlreadyExists() throws IOException {
        lz.createFile(DUMMY_FILE_CTL);
    }

    @Test
    public void testGetFileNotFound() throws IOException {
        File f = lz.getFile(DUMMY_FILE_NOT_EXISTS);
        assertNull("getFile didn't return null for nonexistent file", f);
    }

    @Test
    public void testGetMd5Hex() throws IOException {
        File f = lz.getFile(DUMMY_FILE_CTL);
        assertEquals("incorrect result for md5 calculation",
                MD5.calculate(f), lz.getMd5Hex(f));
    }

    @Test(expected = IOException.class)
    public void testGetMd5HexNotFound() throws IOException {
        lz.getMd5Hex(new File(DUMMY_DIR + File.separator + DUMMY_FILE_NOT_EXISTS));
    }

}
