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
package org.slc.sli.bulk.extract.files;

import java.io.File;
import java.io.OutputStream;

import junit.framework.Assert;

import org.junit.Test;

import org.slc.sli.bulk.extract.TestUtils;

/** Unit test for DataExtractFile.
 * @author tke
 *
 */
public class DataExtractFileTest {

    /**
     * test case.
     */
    @Test
    public void test() {
        String tempParentDir = "./tempDir";
        String tempFileName = "tempFile";

            try {

                File tempDir = new File(tempParentDir);
                if(!tempDir.exists()) {
                    tempDir.mkdir();
                }

                DataExtractFile testDEF = new DataExtractFile(tempDir.getAbsolutePath(), tempFileName);
                OutputStream os = testDEF.getOutputStream();

                if(os == null){
                    Assert.fail("Failed to create the data file");
                } else {
                    TestUtils.deleteDir(tempDir);
                    Assert.assertTrue(true);
                }
                testDEF.close();
            } catch (Exception e) {
                Assert.fail("Test fail due to exception");
            }
    }

}
