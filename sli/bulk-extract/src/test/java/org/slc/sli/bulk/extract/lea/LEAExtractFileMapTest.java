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

package org.slc.sli.bulk.extract.lea;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.files.ExtractFile;


public class LEAExtractFileMapTest {
    
    private ExtractFileMap extractMap;
    private Map<String, ExtractFile> edorgMap;

    @Before
    public void setUp() {
        edorgMap = new HashMap<String, ExtractFile>();
        extractMap = new ExtractFileMap(edorgMap);
    }
    
    @After
    public void tearDown() {
        edorgMap.clear();
    }
    
    @Test
    public void testValidLeaReturnsExtractFile() {
        edorgMap.put("BLOOP", Mockito.mock(ExtractFile.class));
        Assert.assertTrue(extractMap.getExtractFileForEdOrg("BLOOP") != null);
    }
    
    @Test
    public void testInvalidLeaReturnsNoFile() {
        Assert.assertTrue(extractMap.getExtractFileForEdOrg("MERP") == null);
    }
    
    @Test
    public void testCloseFiles() {
        try {
            extractMap.closeFiles();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertFalse(true);
        }
        edorgMap.put("BLOOP", Mockito.mock(ExtractFile.class));
        try {
            extractMap.closeFiles();
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertFalse(true);
        }
    }
    
    @Test
    public void testGenerateMedatDataFiles() throws Exception {
        extractMap.buildManifestFiles(null);
        ExtractFile mockFile = Mockito.mock(ExtractFile.class);
        Mockito.when(mockFile.getManifestFile()).thenThrow(new IOException("I HATE MANIFEST FILES"));
        edorgMap.put("BLOOP", mockFile);
        extractMap.buildManifestFiles(new DateTime());
    }
    
    @Test
    public void testGenerateFilesOnlyOnce() {
        ExtractFile mockFile = Mockito.mock(ExtractFile.class);
        ExtractFile mockFile2 = Mockito.mock(ExtractFile.class);
        edorgMap.put("One", mockFile);
        edorgMap.put("Two", mockFile);
        edorgMap.put("Three", mockFile2);
        
        extractMap.archiveFiles();
        Mockito.verify(mockFile, Mockito.times(1)).generateArchive();
        Mockito.verify(mockFile2, Mockito.times(1)).generateArchive();
    }
    
    @Test
    public void testArchiveFiles() throws Exception {
        extractMap.archiveFiles();
        ExtractFile mockFile = Mockito.mock(ExtractFile.class);
        Mockito.when(mockFile.generateArchive()).thenReturn(false);
        edorgMap.put("BLOOP", mockFile);
        extractMap.archiveFiles();
        
        Mockito.when(mockFile.generateArchive()).thenReturn(true);
        edorgMap.put("BLOOP", mockFile);
        extractMap.archiveFiles();
    }

}
