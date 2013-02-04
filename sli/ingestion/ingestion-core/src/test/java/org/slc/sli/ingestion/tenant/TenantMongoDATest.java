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

package org.slc.sli.ingestion.tenant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.transformation.SimpleEntity;

/**
 * JUnits for testing the TenantMongoDA class.
 *
 * @author jtully
 *
 */
public class TenantMongoDATest {

    private TenantMongoDA tenantDA;

    private Repository<Entity> mockRepository;

    private final String ingestionServerName = "ingestion_server_host";
    private final String lzPath1 = "lz_path_1";
    private final String lzPath2 = "lz_path_2";
    private final String tenantId = "test_tenant_id";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        tenantDA = new TenantMongoDA();
        // Setup the mocked Repository Template.
        mockRepository = mock(Repository.class);
        tenantDA.setEntityRepository(mockRepository);
        // createTestTenantRecord();
    }

    //Removing this unit test as now Ingestion server now polls all the landing zones.
    //@Test
    public void shouldGetLzPathsFromIngestionServerName() {
        List<Entity> testTenantRecords = new ArrayList<Entity>();
        testTenantRecords.add(createTenantEntity());

        when(mockRepository.findAll(Mockito.eq("tenant"), Mockito.any(NeutralQuery.class))).thenReturn(
                testTenantRecords);

        List<String> lzPathsResult = tenantDA.getLzPaths();

        assertNotNull("lzPathsResult was null", lzPathsResult);
        assertEquals("lzPathsResult was not match expected size", 2, lzPathsResult.size());
        assertEquals("lzPathsResult[0] did not match expected value", lzPath1, lzPathsResult.get(0));
        assertEquals("lzPathsResult[1] did not match expected value", lzPath2, lzPathsResult.get(1));

        Mockito.verify(mockRepository, Mockito.times(1)).findAll(Mockito.eq("tenant"), Mockito.any(NeutralQuery.class));
    }

    private Entity createTenantEntity() {
        List<Pair<String, String>> ingestionServersAndPaths = new ArrayList<Pair<String, String>>();
        ingestionServersAndPaths.add(ImmutablePair.of(ingestionServerName, lzPath1));
        ingestionServersAndPaths.add(ImmutablePair.of(ingestionServerName, lzPath2));
        return createEntityWithLzPaths(ingestionServersAndPaths);
    }

    private Entity createEntityWithLzPaths(List<Pair<String, String>> ingestionServersAndPaths) {

        SimpleEntity entity = new SimpleEntity();
        entity.setBody(new HashMap<String, Object>());

        List<Map<String, String>> landingZones = new ArrayList<Map<String, String>>();
        for (Pair<String, String> ingestionServerAndPath : ingestionServersAndPaths) {
            Map<String, String> landingZone = new HashMap<String, String>();

            landingZone.put(TenantMongoDA.INGESTION_SERVER, ingestionServerAndPath.getLeft());
            landingZone.put(TenantMongoDA.PATH, ingestionServerAndPath.getRight());
            landingZones.add(landingZone);
        }

        entity.getBody().put(TenantMongoDA.LANDING_ZONE, landingZones);
        entity.getBody().put(TenantMongoDA.TENANT_ID, tenantId);
        entity.setEntityId("42");
        return entity;
    }

    @Test
    public void shouldGetTenantIdFromLzPath() {

        // List<TenantRecord> testTenantRecords = new ArrayList<TenantRecord>();
        // testTenantRecords.add(tenantRecord);
        Entity tenantRecord = createTenantEntity();

        when(mockRepository.findOne(Mockito.eq("tenant"), Mockito.any(NeutralQuery.class))).thenReturn(tenantRecord);

        String tenantIdResult = tenantDA.getTenantId(lzPath1);

        assertNotNull("tenantIdResult was null", tenantIdResult);
        assertEquals("tenantIdResult did not match expected value", tenantId, tenantIdResult);

        Mockito.verify(mockRepository, Mockito.times(1)).findOne(Mockito.eq("tenant"), Mockito.any(NeutralQuery.class));
    }

    @Test
    public void shouldInsertTenant() {
        TenantRecord tenantRecord = createTestTenantRecord();

        tenantDA.insertTenant(tenantRecord);

        Mockito.verify(mockRepository).create(Mockito.eq("tenant"), Mockito.argThat(new IsCorrectBody(tenantRecord)));

    }

    private class IsCorrectBody extends ArgumentMatcher<Map<String, Object>> {

        private final TenantRecord tenant;

        public IsCorrectBody(TenantRecord tenant) {
            this.tenant = tenant;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object argument) {
            Map<String, Object> arg = (Map<String, Object>) argument;

            if (!StringUtils.equals(tenant.getTenantId(), (String) arg.get(TenantMongoDA.TENANT_ID))) {
                return false;
            }
            List<Map<String, String>> landingZones = (List<Map<String, String>>) arg.get(TenantMongoDA.LANDING_ZONE);

            if (landingZones.size() != tenant.getLandingZone().size()) {
                return false;
            }

            for (int i = 0; i < landingZones.size(); i++) {
                LandingZoneRecord lzRecord = tenant.getLandingZone().get(i);
                Map<String, String> lzMap = landingZones.get(i);
                if (!StringUtils.equals(lzRecord.getDesc(), lzMap.get(TenantMongoDA.DESC))) {
                    return false;
                }
                if (!StringUtils.equals(lzRecord.getEducationOrganization(),
                        lzMap.get(TenantMongoDA.EDUCATION_ORGANIZATION))) {
                    return false;
                }
                if (!StringUtils.equals(lzRecord.getIngestionServer(), lzMap.get(TenantMongoDA.INGESTION_SERVER))) {
                    return false;
                }
                if (!StringUtils.equals(lzRecord.getPath(), lzMap.get(TenantMongoDA.PATH))) {
                    return false;
                }
            }

            return true;
        }
    }

    private TenantRecord createTestTenantRecord() {
        TenantRecord tenant = new TenantRecord();
        List<LandingZoneRecord> lzList = new ArrayList<LandingZoneRecord>();

        LandingZoneRecord lz1 = new LandingZoneRecord();
        lz1.setIngestionServer(ingestionServerName);
        lz1.setPath(lzPath1);
        lz1.setDesc("desc");
        lz1.setEducationOrganization("org");

        LandingZoneRecord lz2 = new LandingZoneRecord();
        lz2.setIngestionServer(ingestionServerName);
        lz2.setPath(lzPath2);
        lz2.setDesc("desc");
        lz2.setEducationOrganization("org");

        lzList.add(lz1);
        lzList.add(lz2);

        tenant = new TenantRecord();
        tenant.setLandingZone(lzList);
        tenant.setTenantId(tenantId);
        return tenant;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAutoLoadFiles() throws UnknownHostException {
        Map<String, Object> tenantsForPreloading = new HashMap<String, Object>();
        tenantsForPreloading.put(lzPath1, Arrays.asList("smallDataSet.xml", "mediumDataSet.xml"));
        Entity tenant = createTenantEntity();
        List<Map<String, Object>> landingZone = (List<Map<String, Object>>) tenant.getBody().get(
                TenantMongoDA.LANDING_ZONE);
        Map<String, Object> preloadDef = new HashMap<String, Object>();
        preloadDef.put(TenantMongoDA.PRELOAD_STATUS, "ready");
        preloadDef.put(TenantMongoDA.PRELOAD_FILES, Arrays.asList("smallDataSet.xml", "mediumDataSet.xml"));
        landingZone.get(0).put(TenantMongoDA.PRELOAD_DATA, preloadDef);
        when(mockRepository.findAll(eq("tenant"), any(NeutralQuery.class))).thenReturn(Arrays.asList(tenant));
        when(mockRepository.doUpdate(eq("tenant"), any(NeutralQuery.class), any(Update.class))).thenReturn(true);
        assertEquals(tenantsForPreloading, tenantDA.getPreloadFiles("ingestion_server_host"));
        verify(mockRepository).doUpdate(eq("tenant"), any(NeutralQuery.class), argThat(new ArgumentMatcher<Update>() {
            @Override
            public boolean matches(Object argument) {
                Update expectedUpdate = Update.update("body." + TenantMongoDA.LANDING_ZONE + ".$." + TenantMongoDA.PRELOAD_DATA
                        + "." + TenantMongoDA.PRELOAD_STATUS, "started");
                return ((Update) argument).getUpdateObject().equals(expectedUpdate.getUpdateObject());
            }
        }));
    }
}
