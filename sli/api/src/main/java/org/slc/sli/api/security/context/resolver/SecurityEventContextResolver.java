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
package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import org.slc.sli.api.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Provides filtering for what security events a principle should be able to see based on their credentials.
 *
 * Now supports both hosted and federated users.
 */
@Component
public class SecurityEventContextResolver implements EntityContextResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityEventContextResolver.class);

	@Autowired
	private PagingRepositoryDelegate<Entity> repository;

	private static final String RESOURCE_NAME = "securityEvent";

	@Autowired
	private EdOrgHelper edOrgHelper;

	@Override
	public boolean canResolve(String fromEntityType, String toEntityType) {
		return toEntityType.equals("securityEvent");
	}

    /**
     * Constructs a list of primary keys of security events which the current user should be able to read.
     *
     * @param entity (unused)
     * @return list of ids of accessible security events
     */
	@Override
	public List<String> findAccessible(Entity entity) {
		List<String> securityEventIds = Collections.emptyList();
		List<NeutralQuery> filters = buildQualifyingFilters();
		if (filters.size() > 0) {
			NeutralQuery query = new NeutralQuery();
			for (NeutralQuery filter : filters) {
				query.addOrQuery(filter);
			}
			securityEventIds = Lists.newArrayList((repository.findAllIds(
					RESOURCE_NAME, query)));
		} else {
			LOG.info("Cannot access SecurityEvents!");
		}
		return securityEventIds;
	}

    /**
     * Constructs a list of filters for which security events the current user should be able to read.
     *
     * No filters implies no security events are accessible!
     */
	private List<NeutralQuery> buildQualifyingFilters() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<NeutralQuery> filters = new ArrayList<NeutralQuery>();

        if (auth == null) {
            LOG.warn("Could not find auth for SecurityEvents!");
            return filters;
        }

        SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
        if (principal == null) {
            LOG.warn("Could not find principal for SecurityEvents!");
            return filters;
        }

        List<String> roles = principal.getRoles();
        if (roles == null) {
            LOG.warn("Could not find roles for SecurityEvents!");
            return filters;
        }

        if (roles.contains(RoleInitializer.SLC_OPERATOR)) {
            // SLC operators can see all of the security events
            NeutralQuery or = new NeutralQuery();
            filters.add(or);
            return filters;
        } else if (roles.contains(RoleInitializer.SEA_ADMINISTRATOR) || roles.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
            // handle users who are both SEA admins and LEA admins, as the version of the class before US5459 did
            if (roles.contains(RoleInitializer.SEA_ADMINISTRATOR)) {
                addFiltersForSeaAdmin(principal, filters);
            }
            if (roles.contains(RoleInitializer.LEA_ADMINISTRATOR)) {
                addFiltersForLeaAdmin(principal, filters);
            }
        } else {
            addFiltersForFederatedUser(principal, filters);
        }

        return filters;
    }

    private List<NeutralQuery> addFiltersForSeaAdmin(SLIPrincipal principal, List<NeutralQuery> filtersSoFar) {

        String edOrg = principal.getEdOrg();
        if (edOrg == null) {
            LOG.warn("Could not find edOrgs for SecurityEvents!");
            return filtersSoFar;
        }

        String tenantId = SecurityUtil.getTenantId();
        Set<String> homeEdOrgs = new HashSet<String>();
        homeEdOrgs.add(edOrg);

        Set<String> delegatedLEAStateIds = edOrgHelper.getDelegatedEdorgDescendents();

        if (delegatedLEAStateIds.size() > 0) {
            LOG.info(delegatedLEAStateIds + " have delegated SecurityEvents access to SEA!");
            homeEdOrgs.addAll(delegatedLEAStateIds);
        }

        NeutralQuery or = new NeutralQuery();
        or.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, tenantId));
        or.addCriteria(new NeutralCriteria(
                "targetEdOrgList",
                NeutralCriteria.CRITERIA_IN, homeEdOrgs));
        filtersSoFar.add(or);

        return filtersSoFar;
    }

    private List<NeutralQuery> addFiltersForLeaAdmin(SLIPrincipal principal, List<NeutralQuery> filtersSoFar) {

        String edOrg = principal.getEdOrg();
        if (edOrg == null) {
            LOG.warn("Could not find edOrgs for SecurityEvents!");
            return filtersSoFar;
        }
        String edOrgId = principal.getEdOrgId();
        if (edOrgId == null) {
            LOG.warn("Could not find edOrgId for SecurityEvents!");
            return filtersSoFar;
        }


        String tenantId = SecurityUtil.getTenantId();
        Set<String> homeEdOrgs = new HashSet<String>();
        homeEdOrgs.add(edOrg);

        Set<String> edOrgs = new HashSet<String>();
        edOrgs.add(edOrgId);
        Set<String> childEdorgStateIds = edOrgHelper.getChildEdOrgsName(edOrgs);
        homeEdOrgs.addAll(childEdorgStateIds);
        NeutralQuery or = new NeutralQuery();
        or.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, tenantId));
        or.addCriteria(new NeutralCriteria(
                "targetEdOrgList",
                NeutralCriteria.CRITERIA_IN, homeEdOrgs));
        filtersSoFar.add(or);

        return filtersSoFar;
    }

    private List<NeutralQuery> addFiltersForFederatedUser(SLIPrincipal principal,
            List<NeutralQuery> filtersSoFar) {

        String tenantId = SecurityUtil.getTenantId();

        Map<String, Collection<GrantedAuthority>> edOrgRights = principal.getEdOrgRights();
        if (edOrgRights == null) {
            return filtersSoFar;
        }

        Set<String> explicitlyAuthorizedEdOrgIds = new HashSet<String>();
        for (String currentEdOrgId : edOrgRights.keySet()) {
            if (edOrgRights.get(currentEdOrgId).contains(Right.SECURITY_EVENT_VIEW)) {
                explicitlyAuthorizedEdOrgIds.add(currentEdOrgId);
            }
        }

        Set<String> allAuthorizedEdOrgIds = edOrgHelper.getChildEdOrgs(explicitlyAuthorizedEdOrgIds);
        allAuthorizedEdOrgIds.addAll(explicitlyAuthorizedEdOrgIds);

        // now get the ed Org names

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID,
                NeutralCriteria.CRITERIA_IN, allAuthorizedEdOrgIds));
        Iterable<Entity> allAuthorizedEdOrgEntities = repository.findAll(EntityNames.EDUCATION_ORGANIZATION, query);

        Set<String> allAuthorizedEdOrgNames = new HashSet<String>();
        for (Entity edOrgEntity : allAuthorizedEdOrgEntities) {
            allAuthorizedEdOrgNames.add((String) edOrgEntity.getBody().get(ParameterConstants.STATE_ORGANIZATION_ID));
        }

        if (allAuthorizedEdOrgNames.size() > 0) {
            NeutralQuery or = new NeutralQuery();
            or.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, tenantId));
            or.addCriteria(new NeutralCriteria(
                    "targetEdOrgList",
                    NeutralCriteria.CRITERIA_IN, allAuthorizedEdOrgNames));
            filtersSoFar.add(or);

        }

        return filtersSoFar;

    }


	public void setRepository(PagingRepositoryDelegate<Entity> repository) {
		this.repository = repository;
	}

	public void setEdOrgHelper(EdOrgHelper helper) {
		this.edOrgHelper = helper;

	}
}
