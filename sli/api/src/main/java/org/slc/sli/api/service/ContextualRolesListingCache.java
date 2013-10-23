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

package org.slc.sli.api.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import org.slc.sli.api.util.RequestUtil;
import org.slc.sli.api.util.SecurityUtil.UserContext;

/**
 * Structure to cache needed information used by listBasedOnContextualRoles, when user has multiple contexts or contexts with differing rights.
 *
 * @author tshewchuk
 *
 * Information in this cache is stored and used per API web request.
 */
public class ContextualRolesListingCache {

    private UUID currentRequestId = RequestUtil.generateRequestId();

    private Map<String, Long> accessibleEntitiesCount =  new HashMap<String, Long>();

    private Map<String, UserContext> entityContexts =  new HashMap<String, UserContext>();

    private Map<String, Collection<GrantedAuthority>> entityAuthorities =  new HashMap<String, Collection<GrantedAuthority>>();


    public long getAccessibleEntitiesCount(String collectionName) {
        return accessibleEntitiesCount.get(collectionName);
    }

    public void setAccessibleEntitiesCount(String collectionName, long count) {
        if (!currentRequestId.equals(RequestUtil.getCurrentRequestId())) {
            accessibleEntitiesCount =  new HashMap<String, Long>();
            currentRequestId = RequestUtil.getCurrentRequestId();
        }
        accessibleEntitiesCount.put(collectionName, count);
    }

    public Map<String, UserContext> getEntityContexts() {
        return entityContexts;
    }

    public void setEntityContexts(Map<String, UserContext> entityContexts) {
        this.entityContexts = entityContexts;
    }

    public Collection<GrantedAuthority> getEntityAuthorities(String entityId) {
        return entityAuthorities.get(entityId);
    }

    public void setEntityAuthorities(String entityId, Collection<GrantedAuthority> authorities) {
        if (!currentRequestId.equals(RequestUtil.getCurrentRequestId())) {
            entityAuthorities =  new HashMap<String, Collection<GrantedAuthority>>();
            currentRequestId = RequestUtil.getCurrentRequestId();
        }
        entityAuthorities.put(entityId, authorities);
    }

}
