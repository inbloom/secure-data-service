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

package org.slc.sli.bulk.extract.zip;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class OutstreamZipFileTest {
    private static final String TESTPATH = "src/test/resources/";

    @Test
    public void testOutstreamZipFile() {
        File tmpFile = null;
        try {
            new OutstreamZipFile(TESTPATH, "test");

            tmpFile = new File(TESTPATH + "test.zip");
            Assert.assertTrue(tmpFile.exists());
            Assert.assertTrue(!tmpFile.isDirectory());

        } catch (IOException e) {
            Assert.fail();
        } finally {
            FileUtils.deleteQuietly(tmpFile);
        }
    }

    @Test
    public void testCreateAndWriteArchiveEntry() {
        OutstreamZipFile zip = null;
        File unzipDirectory = null;
        try {
            zip = new OutstreamZipFile(TESTPATH, "test");

            zip.createArchiveEntry("testArchive.json");
            zip.writeData("testArchive");
            zip.closeZipFile();

            unzipDirectory = new File(TESTPATH + "unzip/test");
            if (unzipDirectory.exists()) {
                FileUtils.deleteQuietly(unzipDirectory);
            }
            ZipFileUtil.extract(zip.getOutputFile());
            File archivedFile = new File(TESTPATH + "unzip/test/testArchive.json");
            Assert.assertTrue(archivedFile.exists());
        } catch (IOException e) {
            Assert.fail();
        } finally {
            FileUtils.deleteQuietly(zip.getOutputFile());
            FileUtils.deleteQuietly(unzipDirectory);
        }
    }

}
