/**
 * 
 */
package org.slc.sli.manager.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.client.LiveAPIClient;
import org.slc.sli.entity.GenericEntity;

/**
 * @author tosako
 * 
 */
public class InstitutionalHierarchyManagerImplTest {
    InstitutionalHierarchyManagerImpl testInstitutionalHierarchyManagerImpl = null;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        LiveAPIClient apiClient = new LiveAPIClient() {
            public List<GenericEntity> getSchools(String token, List<String> schoolIds) {
                List<GenericEntity> schools = new ArrayList<GenericEntity>();
                schools.add(new GenericEntity()); // dummy GenericEntity
                return schools;
            }
            
            public GenericEntity getParentEducationalOrganization(final String token, GenericEntity edOrg) {
                GenericEntity entity = new GenericEntity();
                LinkedHashMap<String, Object> metaData = new LinkedHashMap<String, Object>();
                metaData.put("externalId", "my test district name");
                entity.put("metaData", metaData);
                return entity;
            }
            
        };
        this.testInstitutionalHierarchyManagerImpl = new InstitutionalHierarchyManagerImpl() {
            public String getToken() {
                return "";
            }
        };
        this.testInstitutionalHierarchyManagerImpl.setApiClient(apiClient);
    }
    
    /**
     * Test method for
     * {@link org.slc.sli.manager.impl.InstitutionalHierarchyManagerImpl#getUserDistrictId(java.lang.String)}
     * .
     */
    @Test
    public void testGetUserDistrictId() {
        String districtId = this.testInstitutionalHierarchyManagerImpl.getUserDistrictId("fakeToken");
        Assert.assertEquals("my test district name", districtId);
    }
    
}
