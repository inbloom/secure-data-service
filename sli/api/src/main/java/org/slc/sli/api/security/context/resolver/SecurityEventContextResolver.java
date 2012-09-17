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
package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.resources.security.DelegationUtil;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.enums.Right;

/**
 * TODO: add class level javadoc
 *
 */
@Component
public class SecurityEventContextResolver implements EntityContextResolver {

    static final List<String> ROLES_SEA_OR_REALM_ADMIN = Arrays.asList(RoleInitializer.SEA_ADMINISTRATOR,
            RoleInitializer.REALM_ADMINISTRATOR);
    static final List<String> ROLES_LEA_ADMIN          = Arrays.asList(RoleInitializer.LEA_ADMINISTRATOR);

    @Autowired
    
    private PagingRepositoryDelegate<Entity> repository;

    private static final String       RESOURCE_NAME              = "securityEvent";

    @Autowired
    private DelegationUtil delegationUtil;

    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return toEntityType.equals("securityEvent");
    }

    @Override
    public List<String> findAccessible(Entity entity) {
        List<String> securityEventIds = Collections.emptyList();
        List<NeutralQuery> filters = buildQualifyingFilters();
        if (filters.size() > 0) {
            NeutralQuery query = new NeutralQuery();
            for (NeutralQuery filter:filters) {
                query.addOrQuery(filter);
            }
            securityEventIds = Lists.newArrayList((repository.findAllIds(RESOURCE_NAME, query)));
        } else {
            info("User neither LEA Admin, nor SEA Admin, nor SLC Operator. Cannot access SecurityEvents!");
        }
        return securityEventIds;
    }

    private List<NeutralQuery> buildQualifyingFilters() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<NeutralQuery> filters  = new ArrayList<NeutralQuery>();
        if (auth != null) {
            SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
            if (principal != null) {
                List<String> roles = principal.getRoles();
                if (roles != null) {
                    if (roles.contains(RoleInitializer.SLC_OPERATOR)) {
                        NeutralQuery or = new NeutralQuery();
                        filters.add(or);
                    } else {
                        String edOrgs = principal.getEdOrg();
                        if (edOrgs != null) {
                            List<String> homeEdOrgs = Arrays.asList(edOrgs.split(","));
                            if (roles.contains(RoleInitializer.SEA_ADMINISTRATOR) && homeEdOrgs.size() > 0) {

                                NeutralQuery or = new NeutralQuery();
                                or.addCriteria(new NeutralCriteria("targetEdOrg",             NeutralCriteria.CRITERIA_IN,    homeEdOrgs));
                                or.addCriteria(new NeutralCriteria("roles",                   NeutralCriteria.CRITERIA_IN,    ROLES_SEA_OR_REALM_ADMIN));
                                filters.add(or);

                                if (SecurityUtil.hasRight(Right.EDORG_DELEGATE)) {
                                    List<String> delegatedLEAStateIds = delegationUtil.getSecurityEventDelegateStateIds();

                                    if (delegatedLEAStateIds.size() > 0) {
                                        info(delegatedLEAStateIds + " have delegated SecurityEvents access to SEA!");
                                        NeutralQuery delegateOr = new NeutralQuery();
                                        delegateOr.addCriteria(new NeutralCriteria("targetEdOrg",  NeutralCriteria.CRITERIA_IN,    delegatedLEAStateIds));
                                        delegateOr.addCriteria(new NeutralCriteria("roles",        NeutralCriteria.CRITERIA_IN,    ROLES_LEA_ADMIN));
                                        filters.add(delegateOr);
                                    } else {
                                        info("Could not find any delegated edOrgs for SecurityEvents!");
                                    }
                                }
                            }
                            if (roles.contains(RoleInitializer.LEA_ADMINISTRATOR) && homeEdOrgs.size() > 0) {
                                NeutralQuery or = new NeutralQuery();
                                or.addCriteria(new NeutralCriteria("targetEdOrg",  NeutralCriteria.CRITERIA_IN,    homeEdOrgs));
                                or.addCriteria(new NeutralCriteria("roles",         NeutralCriteria.CRITERIA_IN,    ROLES_LEA_ADMIN));
                                filters.add(or);
                            }
                        } else {
                            warn("Could not find edOrgs for SecurityEvents!");
                        }
                    }
                } else {
                    warn("Could not find roles for SecurityEvents!");
                }
            } else {
                warn("Could not find principal for SecurityEvents!");
            }
        } else {
            warn("Could not find auth for SecurityEvents!");
        }
        return filters;
    }
}
