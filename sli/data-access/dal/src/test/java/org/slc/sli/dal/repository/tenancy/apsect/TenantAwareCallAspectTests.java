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


package org.slc.sli.dal.repository.tenancy.apsect;

import junit.framework.Assert;

import org.junit.Test;

import org.slc.sli.dal.repository.tenancy.CurrentTenantHolder;
import org.slc.sli.dal.repository.tenancy.SystemCall;
import org.slc.sli.dal.repository.tenancy.TenantCall;

/**
 * @author okrook
 *
 */
public class TenantAwareCallAspectTests {

    @Test
    public void testTenantCall() {
        tenantCall("MyTenant");

        systemCall();

        tenantCall2(null, "MyTenant");
    }

    @Test(expected = IllegalStateException.class)
    public void testBadTenantCall() {
        badTenantCall("MyTenant");
    }

    @TenantCall(param = "tenantId")
    private void tenantCall(String tenantId) {
        Assert.assertEquals(tenantId, CurrentTenantHolder.getCurrentTenant());

        systemCall();

        Assert.assertEquals(tenantId, CurrentTenantHolder.getCurrentTenant());
    }

    @TenantCall(param = "tenantId")
    private void tenantCall2(Object someValue, String tenantId) {
        Assert.assertEquals(tenantId, CurrentTenantHolder.getCurrentTenant());
    }

    @TenantCall(param = "myTenant")
    private void badTenantCall(String tenantId) {
        Assert.assertEquals(tenantId, CurrentTenantHolder.getCurrentTenant());
    }

    @SystemCall
    private void systemCall() {
        Assert.assertEquals(null, CurrentTenantHolder.getCurrentTenant());
    }
}
