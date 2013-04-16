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
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * JUnit test for Launcher class.
 * @author npandey
 *
 */
public class LauncherTest {

    Launcher launcher;
    Repository<Entity> repository;
    TenantExtractor tenantExtractor;
    LocalEdOrgExtractor localEdOrgExtractor;

    /**
     * Runs before JUnit tests and does the initiation work for the tests.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        launcher = new Launcher();
        repository = Mockito.mock(MongoEntityRepository.class);
        tenantExtractor = Mockito.mock(TenantExtractor.class);
        localEdOrgExtractor = Mockito.mock(LocalEdOrgExtractor.class);
        launcher.setRepository(repository);
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
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL ,tenantId));
        query.addCriteria(new NeutralCriteria("tenantIsReady", NeutralCriteria.OPERATOR_EQUAL, true));

        Mockito.when(repository.findOne("tenant", query)).thenReturn(null);

        launcher.execute(tenantId);

        Mockito.verify(tenantExtractor, Mockito.never()).execute(Mockito.eq("tenant"), Mockito.any(ExtractFile.class), Mockito.any(DateTime.class));
    }

    /**
     * Test initiating bulk extractor for a valid tenant.
     */
    @Test
    public void testValidTenant() {
        String tenantId = "Midgar";
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL ,tenantId));
        query.addCriteria(new NeutralCriteria("tenantIsReady", NeutralCriteria.OPERATOR_EQUAL, true));
        Mockito.doNothing().when(tenantExtractor).execute(Mockito.eq(tenantId), Mockito.any(ExtractFile.class), Mockito.any(DateTime.class));


        Mockito.when(repository.findOne("tenant", query)).thenReturn(new MongoEntity("1234_id", null));

        launcher.execute(tenantId);

        Mockito.verify(tenantExtractor, Mockito.times(1)).execute(Mockito.eq(tenantId), Mockito.any(ExtractFile.class), Mockito.any(DateTime.class));
    }

}
