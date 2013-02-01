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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 
 * Convert tenant ID to tenant DB name.
 * 
 * @author tshewchuk
 */
public class TenantIdToDbName {
    
    // The sha calculation showed up in profiling.  Use a simple LRU cache.
    private static final LoadingCache<String, String> TENANT_ID_CACHE = CacheBuilder.newBuilder().maximumSize(10000).build(new CacheLoader<String, String>() {
                public String load(String tenant) {
                    return DigestUtils.shaHex(tenant);
                }
            });
    
    /**
     * Convert tenant ID to tenant DB name.
     * 
     * @param tenantId
     *            Tenant ID from client/user.
     *            return String
     *            Mongo DB name for tenant.
     */
    public static String convertTenantIdToDbName(String tenantId) {
        if (tenantId != null) {
            return TENANT_ID_CACHE.getUnchecked(tenantId);
        } else {
            return tenantId;
        }
    }
}
