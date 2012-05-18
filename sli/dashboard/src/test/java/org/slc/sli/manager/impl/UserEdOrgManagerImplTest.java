/**
 *
 */
package org.slc.sli.manager.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import junit.framework.Assert;

import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.entity.ConfigMap;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.entity.GenericEntity;

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
        LiveAPIClient apiClient = new LiveAPIClient() {

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
                LinkedHashMap<String, Object> metaData = new LinkedHashMap<String, Object>();
                metaData.put("externalId", "my test district name");
                entity.put("metaData", metaData);
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
     * {@link org.slc.sli.manager.impl.UserEdOrgManagerImpl#getUserEdOrg(java.lang.String)}
     * .
     */
    @Test
    @Ignore
    public void testGetUserDistrictId() {
        EdOrgKey key = this.testInstitutionalHierarchyManagerImpl.getUserEdOrg("fakeToken");
        Assert.assertEquals("my test district name", key.getDistrictId());
    }

}
