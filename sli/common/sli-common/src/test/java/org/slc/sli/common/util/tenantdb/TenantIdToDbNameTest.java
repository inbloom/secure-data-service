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

package org.slc.sli.common.util.tenantdb;

import junit.framework.Assert;

import org.junit.Test;

public class TenantIdToDbNameTest {

    @Test
    public void testConvertTenantIdToDbName() {
        Assert.assertEquals("7be07aaf460d593a323d0db33da05b64bfdcb3a5", TenantIdToDbName.convertTenantIdToDbName("ABCDE"));
        Assert.assertEquals("782a35eb5b9cd3e771047a60381e1274d76bc069", TenantIdToDbName.convertTenantIdToDbName("ABC.DE"));
        Assert.assertEquals("1072a2a56f16654387d030014968a48f04ca7488", TenantIdToDbName.convertTenantIdToDbName(" ABC DE "));
        Assert.assertEquals("f89b39e01f5b1bb76655211472cd71274766070e", TenantIdToDbName.convertTenantIdToDbName("$ABCDE"));
        Assert.assertEquals("8e1cea182e0e0499fe1e0fe28e02d9ffb47ba098", TenantIdToDbName.convertTenantIdToDbName("ABC/DE"));
    }

}
