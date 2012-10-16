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

package org.slc.sli.common.util.tenantdb;

import junit.framework.Assert;

import org.junit.Test;

public class TenantIdToDbNameTest {

    @Test
    public void testConvertTenantIdToDbName() {
        Assert.assertEquals("ABCDE", TenantIdToDbName.convertTenantIdToDbName("ABCDE"));
        Assert.assertEquals("ABC_DE", TenantIdToDbName.convertTenantIdToDbName("ABC.DE"));
        Assert.assertEquals("_ABC_DE_", TenantIdToDbName.convertTenantIdToDbName(" ABC DE "));
        Assert.assertEquals("_ABCDE", TenantIdToDbName.convertTenantIdToDbName("$ABCDE"));
        Assert.assertEquals("ABC_DE", TenantIdToDbName.convertTenantIdToDbName("ABC/DE"));
    }

}
