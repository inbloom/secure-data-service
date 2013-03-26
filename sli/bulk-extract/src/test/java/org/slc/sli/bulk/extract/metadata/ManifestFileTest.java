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
package org.slc.sli.bulk.extract.metadata;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tke
 *
 */
public class ManifestFileTest {

    ManifestFile meta = null;
    
    @Before
    public void init() throws IOException {
        meta = new ManifestFile("./");
    }
    
    @After
    public void cleanup() throws IOException {
        FileUtils.forceDelete(meta.getFile());
    }
    @Test
    public void testGetVersion() throws IOException {
        
        String version = meta.getApiVersion();
        
        Assert.assertTrue(version.equals("v1.4"));
    }
    
    @Test
    public void testGenerateFile() throws IOException {
        ManifestFile meta = new ManifestFile("./");
        Date startTime = new Date();
        
        meta.generateMetaFile(startTime);
        
        File manifestFile = meta.getFile();
        
        String fileContent = FileUtils.readFileToString(manifestFile);
        
        assertTrue("Correct metadata version not found in metadata file", fileContent.contains(ManifestFile.METADATA_VERSION + "1.0"));
        assertTrue("Correct api version not found in metadata file", fileContent.contains(ManifestFile.API_VERSION + "v1.4"));
        assertTrue("Correct time stamp entry not found in metadata file", fileContent.contains(ManifestFile.TIME_STAMP + startTime));
    }

}
