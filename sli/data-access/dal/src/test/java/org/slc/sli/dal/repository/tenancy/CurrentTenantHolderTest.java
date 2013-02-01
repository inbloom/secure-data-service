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


package org.slc.sli.dal.repository.tenancy;

import java.util.EmptyStackException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author okrook
 *
 */
public class CurrentTenantHolderTest {

    @Test
    public void testTenant() {
        CurrentTenantHolder.push("MyTenant");

        Assert.assertEquals("MyTenant", CurrentTenantHolder.getCurrentTenant());
        Assert.assertEquals("MyTenant", CurrentTenantHolder.getCurrentTenant());
        Assert.assertEquals("MyTenant", CurrentTenantHolder.pop());
    }

    @Test(expected = EmptyStackException.class)
    public void testEmptyHolderPop() {
        CurrentTenantHolder.pop();
    }

    @Test(expected = EmptyStackException.class)
    public void testEmptyHolderPeek() {
        CurrentTenantHolder.getCurrentTenant();
    }
}
