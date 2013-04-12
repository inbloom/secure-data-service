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
package org.slc.sli.bulk.extract.extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.metadata.ManifestFile;


/**
 * Test class for the Tenant Extractor.
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TenantExtractorTest {

    private final String extractDir = "data/tmp";

    private BulkExtractMongoDA bulkExtractMongoDA;

    @Autowired
    private TenantExtractor tenantExtractor;

    private ExtractFile archiveFile;

    private List<String> collections;

    /**
     * Runs before JUnit test and does the initiation work.
     * @throws IOException
     *          if an I/O error occurred
     */
    @Before
    public void init() throws IOException {
        collections = new ArrayList<String>();
        collections.add("student");
        collections.add("assessment");
        collections.add("staff");
        collections.add("staffEducationOrganizationAssociation");

        File file = Mockito.mock(File.class);
        Map<String, File> files = new HashMap<String, File>();
        files.put("test", file);
        ManifestFile metadataFile = Mockito.mock(ManifestFile.class);
        Mockito.doNothing().when(metadataFile).generateMetaFile(Matchers.any(DateTime.class));

        archiveFile = Mockito.mock(ExtractFile.class);
        Mockito.doNothing().when(archiveFile).generateArchive();
        Mockito.when(archiveFile.getArchiveFiles()).thenReturn(files);
        Mockito.when(archiveFile.getManifestFile()).thenReturn(metadataFile);
        Mockito.when(file.getAbsolutePath()).thenReturn(extractDir+"/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a");

        bulkExtractMongoDA = Mockito.mock(BulkExtractMongoDA.class);
        tenantExtractor.setBulkExtractMongoDA(bulkExtractMongoDA);
    }

    /**
     * JUnit test to test initiation and completion of the entity extraction.
     */
    @Test
    public void testinitiateExtractForEntites() {

        String tenant = "Midgar";
        EntityExtractor ex = Mockito.mock(EntityExtractor.class);
        Mockito.doNothing().when(ex).extractEntities(Matchers.anyString(), Matchers.any(ExtractFile.class), Matchers.anyString());

        tenantExtractor.setEntityExtractor(ex);

        tenantExtractor.execute(tenant, archiveFile, new DateTime());

        for(String collection : collections) {
            Mockito.verify(ex, Mockito.times(1)).extractEntities("Midgar", archiveFile, collection);
        }

        Mockito.verify(bulkExtractMongoDA, Mockito.times(1)).updateDBRecord(Matchers.anyString(), Matchers.anyString(),Matchers.anyString(), Matchers.any(Date.class), Matchers.eq(false));
    }


}
