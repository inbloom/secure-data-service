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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Implements logic needed by the SAMT Users resource.
 */
@Component
public class SuperAdminService {
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private SecurityUtilProxy secUtil;

    public static final String STATE_EDUCATION_AGENCY = "State Education Agency";
    public static final String LOCAL_EDUCATION_AGENCY = "Local Education Agency";

    /**
     * Returns a list of possible ed-orgs. By default restricted to State and Local Education Agencies
     *
     * @param tenant
     *            Tenant in which the ed-orgs reside.
     * @param edOrg
     *            if provided, list will be restricted to that ed-org or lower.
     */
    public Set<String> getAllowedEdOrgs(String tenant, String edOrg) {
        return getAllowedEdOrgs(tenant, edOrg, null, false);
    }

    /**
     * Returns a list of possible ed-orgs that is one of the interested type.
     * By default restricted to State and Local Education Agencies
     * @param tenant
     *            Tenant in which the ed-orgs reside.
     * @param edOrg
     *            if provided, list will be restricted to that ed-org or lower.
     * @param interestedTypes
     *            if provided list will be restricted to those types
     * @param strict
     *            only return edorgs in database if set to true
     */
    public Set<String> getAllowedEdOrgs(final String tenant, final String edOrg, final Collection<String> interestedTypes, boolean strict) {
        Collection<String> iTypes = interestedTypes; 
        if (iTypes == null) {
            iTypes = new HashSet<String>();
            iTypes.add(STATE_EDUCATION_AGENCY);
            iTypes.add(LOCAL_EDUCATION_AGENCY);
        }
        TenantContext.setTenantId(tenant);

        Set<String> edOrgIds = new HashSet<String>();
        if (secUtil.hasRole(RoleInitializer.LEA_ADMINISTRATOR)) {
            edOrgIds.add(secUtil.getEdOrg());
            return edOrgIds;
        }

        NeutralQuery query = new NeutralQuery();

        for (Entity e : this.repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            String tmpEdOrg = (String) e.getBody().get("stateOrganizationId");
            @SuppressWarnings("unchecked")
            List<String> organizationCategories = (List<String>) e.getBody().get("organizationCategories");
            if (organizationCategories != null) {
                for (String category : organizationCategories) {
                    if (iTypes.contains(category)) {
                        edOrgIds.add(tmpEdOrg);
                        break;
                    }
                }
            }
        }

        if (!strict && secUtil.hasRole(RoleInitializer.SEA_ADMINISTRATOR) && !edOrgIds.contains(secUtil.getEdOrg())) {
            edOrgIds.add(secUtil.getEdOrg());
        }

        return edOrgIds;

    }

}
