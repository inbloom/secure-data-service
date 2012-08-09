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


package org.slc.sli.dashboard.unit.manager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.manager.MockAPIClient;
import org.slc.sli.dashboard.manager.impl.UserEdOrgManagerImpl;
import org.slc.sli.dashboard.util.Constants;

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
