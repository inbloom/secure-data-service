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


package org.slc.sli.lander.util;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the FileUtils Handler
 * 
 * @author srichards
 * 
 */
public class FileUtilsTest {
    @Test
    public void zipFilesInDirectory() throws IOException {
        File directory = new File("target");
        File testFile = new File(directory, "test.xml");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            Assert.fail("Unable to set up test preconditions.");
        }
        Assert.assertTrue("zip operation should succeed", FileUtils.zipIngestionData("target") != null);
        File zip = new File(directory, FileUtils.ZIP_FILE_NAME);
        Assert.assertTrue("zip file should exist", zip.exists());
    }
}
