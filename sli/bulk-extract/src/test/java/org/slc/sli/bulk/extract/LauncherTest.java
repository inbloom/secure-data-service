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

package org.slc.sli.bulk.extract;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.extractor.LocalEdOrgExtractor;
import org.slc.sli.bulk.extract.extractor.TenantExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.domain.Entity;

/**
 * JUnit test for Launcher class.
 * @author npandey
 *
 */
public class LauncherTest {

    Launcher launcher;
    BulkExtractMongoDA bulkExtractMongoDA;
    TenantExtractor tenantExtractor;
    LocalEdOrgExtractor localEdOrgExtractor;

    Entity testTenantEntity = TestUtils.makeDummyEntity("tenant", "testTenant", null);

    /**
     * Runs before JUnit tests and does the initiation work for the tests.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        launcher = new Launcher();

        bulkExtractMongoDA = Mockito.mock(BulkExtractMongoDA.class);
        tenantExtractor = Mockito.mock(TenantExtractor.class);
        localEdOrgExtractor = Mockito.mock(LocalEdOrgExtractor.class);
        launcher.setBulkExtractMongoDA(bulkExtractMongoDA);

        launcher.setTenantExtractor(tenantExtractor);
        launcher.setLocalEdOrgExtractor(localEdOrgExtractor);
        launcher.setBaseDirectory("./");
    }

    /**
     *Test initiating bulk extract with a tenant that has not been onboarded.
     */
    @Test
    public void testInvalidTenant() {
        String tenantId = "testTenant";

        Mockito.when(bulkExtractMongoDA.getTenant(tenantId)).thenReturn(null);

        launcher.execute(tenantId);

        Mockito.verify(tenantExtractor, Mockito.never()).execute(Mockito.eq("tenant"), Mockito.any(ExtractFile.class), Mockito.any(DateTime.class));
    }

    /**
     * Test initiating bulk extractor for a valid tenant.
     */
    @Test
    public void testValidTenant() {
        String tenantId = "Midgar";
        Mockito.doNothing().when(tenantExtractor).execute(Mockito.eq(tenantId), Mockito.any(ExtractFile.class), Mockito.any(DateTime.class));


        Mockito.when(bulkExtractMongoDA.getTenant(tenantId)).thenReturn(testTenantEntity);

        launcher.execute(tenantId);

        Mockito.verify(tenantExtractor, Mockito.times(1)).execute(Mockito.eq(tenantId), Mockito.any(ExtractFile.class), Mockito.any(DateTime.class));
    }

}
