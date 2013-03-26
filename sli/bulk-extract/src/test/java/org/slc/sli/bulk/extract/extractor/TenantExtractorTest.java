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
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;


/**
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

    private OutstreamZipFile zipFile;

    private List<String> collections;

    @Before
    public void init() throws IOException {
        collections = new ArrayList<String>();
        collections.add("student");
        collections.add("assessment");
        collections.add("staff");
        collections.add("staffEducationOrganizationAssociation");

        zipFile = Mockito.mock(OutstreamZipFile.class);
        File file = Mockito.mock(File.class);
        Mockito.when(zipFile.getOutputFile()).thenReturn(file);
        Mockito.when(file.getAbsolutePath()).thenReturn(extractDir+"/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a");

        bulkExtractMongoDA = Mockito.mock(BulkExtractMongoDA.class);
        tenantExtractor.setBulkExtractMongoDA(bulkExtractMongoDA);
    }

    @Test
    public void testinitiateExtractForEntites() {

        tenantExtractor.setEntities(collections);

        EntityExtractor ex = Mockito.mock(EntityExtractor.class);
        Mockito.doNothing().when(ex).extractEntity(Matchers.anyString(), Matchers.any(OutstreamZipFile.class), Matchers.anyString());

        tenantExtractor.setEntityExtractor(ex);

        tenantExtractor.execute("Midgar", zipFile, new Date());

        for(String collection : collections) {
            Mockito.verify(ex, Mockito.times(1)).extractEntity("Midgar", zipFile, collection);
        }
        Mockito.verify(bulkExtractMongoDA, Mockito.times(1)).updateDBRecord(Matchers.anyString(), Matchers.anyString(), Matchers.any(Date.class));
    }


}
