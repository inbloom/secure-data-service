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

package org.slc.sli.modeling.sdkgen;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * @author jstokes
 */
public class SdkGenTest {

    @Test
    public void testMain() throws Exception {

        File outDir = new File("sli/modeling/sdkgen/test");
        File pojoDir = new File("sli/modeling/sdkgen/test/pojo/");

        if (!pojoDir.exists()) {
            if (!pojoDir.mkdirs()) {
                fail("failed to create temp dir " + outDir.getName());
            }
        }

        String[] args = new String[] {
                "--outFolder",  outDir.getAbsolutePath(),
                "--wadlFile",  getAbsPath("test_wadl.wadl"),
                "--package",  "org.slc.sli.shtick",
                "--xmiFile",  getAbsPath("test_sli.xmi")
        };

        SdkGen.main(args);

        List<String> sdkFiles = Arrays.asList(outDir.list());
        assertNotNull(sdkFiles);
        assertFalse(sdkFiles.isEmpty());

        assertTrue(sdkFiles.contains("pojo"));
        assertTrue(sdkFiles.contains("Level2Client.java"));
        assertTrue(sdkFiles.contains("Level3Client.java"));
        assertTrue(sdkFiles.contains("StandardLevel2Client.java"));
        assertTrue(sdkFiles.contains("StandardLevel3Client.java"));

        List<String> pojoFiles = Arrays.asList(pojoDir.list());
        assertNotNull(pojoFiles);
        assertFalse(pojoFiles.isEmpty());

        assertTrue(pojoFiles.contains("Assessment.java"));
        assertTrue(pojoFiles.contains("AssessmentTitle.java"));

        FileUtils.deleteDirectory(outDir);
    }

    private String getAbsPath(final String fileName) {
        return getClass().getResource("/" + fileName).getFile();
    }
}
