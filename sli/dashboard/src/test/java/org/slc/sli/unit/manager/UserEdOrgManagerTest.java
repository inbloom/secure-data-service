package org.slc.sli.unit.manager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.MockAPIClient;
import org.slc.sli.manager.impl.UserEdOrgManagerImpl;
import org.slc.sli.util.Constants;

/**
 * test for UserEdOrgManager
 * @author agrebneva
 *
 */
public class UserEdOrgManagerTest {
    UserEdOrgManagerImpl userEdOrgManager;
    @Before
    public void setup() {
        userEdOrgManager = new UserEdOrgManagerImpl() {
            @Override
            protected boolean isEducator() {
                return true;
            }
        };
        userEdOrgManager.setApiClient(new MockAPIClient());
    }

    @Test
    public void testGetStaffInfo() {
        GenericEntity entity = userEdOrgManager.getStaffInfo("1");
        Assert.assertEquals(false, entity.get(Constants.LOCAL_EDUCATION_AGENCY));
    }

    public void testUserEdOrg() {
        EdOrgKey edOrgKey = userEdOrgManager.getUserEdOrg("1");
        Assert.assertNotNull(edOrgKey);
    }
}
