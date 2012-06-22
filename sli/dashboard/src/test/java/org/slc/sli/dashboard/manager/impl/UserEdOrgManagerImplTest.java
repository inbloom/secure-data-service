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


/**
 *
 */
package org.slc.sli.dashboard.manager.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.client.SDKAPIClient;
import org.slc.sli.dashboard.entity.ConfigMap;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.impl.UserEdOrgManagerImpl;

/**
 * @author tosako
 *
 */
public class UserEdOrgManagerImplTest {
    UserEdOrgManagerImpl testInstitutionalHierarchyManagerImpl = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

            APIClient apiClient = new SDKAPIClient() {

            private String customConfigJson = "{}";

            @Override
            public ConfigMap getEdOrgCustomData(String token, String id) {
                return new GsonBuilder().create().fromJson(customConfigJson, ConfigMap.class);
            }

            public void putEdOrgCustomData(String token, String id, String customJson) {
                customConfigJson = customJson;
            }

            @Override
            public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
                List<GenericEntity> schools = new ArrayList<GenericEntity>();
                schools.add(new GenericEntity()); // dummy GenericEntity
                return schools;
            }

            @Override
            public GenericEntity getParentEducationalOrganization(final String token, GenericEntity edOrg) {
                GenericEntity entity = new GenericEntity();
                entity.put("id", "AA");
                return entity;
            }

        };
        this.testInstitutionalHierarchyManagerImpl = new UserEdOrgManagerImpl() {
            @Override
            public String getToken() {
                return "";
            }
        };
        this.testInstitutionalHierarchyManagerImpl.setApiClient(apiClient);
    }

    /**
     * Test method for
     * {@link org.slc.sli.dashboard.manager.impl.UserEdOrgManagerImpl#getUserEdOrg(java.lang.String)}
     * .
     */
    @Test
    @Ignore
    public void testGetUserDistrictId() {
        //EdOrgKey key = this.testInstitutionalHierarchyManagerImpl.getUserEdOrg("fakeToken");
        //Assert.assertEquals("my test district name", key.getDistrictId());
    }

}
