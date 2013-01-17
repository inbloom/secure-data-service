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
package org.slc.sli.api.resources.generic.service.custom;

import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.resources.security.DelegationUtil;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resource service for application
 */
@Component("applicationResourceService")
public class ApplicationResourceService extends DefaultResourceService {

    public static final String AUTHORIZED_ED_ORGS = "authorized_ed_orgs";
    private static final String CREATED_BY = "created_by";
    private static final String AUTHOR_SANDBOX_TENANT = "author_sandbox_tenant";

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    @Value("${sli.sandbox.enabled}")
    private boolean sandboxEnabled;
    
    @Autowired
    private DelegationUtil delegation;

    @Override
    protected void addAdditionalCriteria(final NeutralQuery query) {

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (SecurityUtil.hasRight(Right.DEV_APP_CRUD)) {
            if (sandboxEnabled) {
                // Sandbox developer can see all apps in their tenancy
                query.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, principal.getTenantId(), false));
            } else {
                // Prod. Developer sees all apps they own
                query.addOrQuery(new NeutralQuery(new NeutralCriteria(CREATED_BY, NeutralCriteria.OPERATOR_EQUAL, principal.getExternalId())));
                // or they see all apps created by other developers from the same sandbox tenant -- after unifying their accounts, prod app developer
                // is hosted by sandbox ldap
                String sandboxTenancy = principal.getSandboxTenant();
                if (sandboxTenancy != null && sandboxTenancy.length() > 0) {
                	query.addOrQuery(new NeutralQuery(new NeutralCriteria(AUTHOR_SANDBOX_TENANT, NeutralCriteria.OPERATOR_EQUAL, sandboxTenancy)));
                }
            }
        } else if (!SecurityUtil.hasRight(Right.SLC_APP_APPROVE)) {  //realm admin, sees apps that they are either authorized or could be authorized

            Set<String> edorgs = new HashSet<String>();
            edorgs.add(principal.getEdOrgId());
            if (SecurityUtil.getAllRights().contains(Right.EDORG_DELEGATE)) {   //Add an SEA admin's delegated LEAs
                edorgs.addAll(delegation.getAppApprovalDelegateEdOrgs());
            }
            
            //know this is ugly, but having trouble getting or queries to work
            List<String> idList = new ArrayList<String>();
            NeutralQuery newQuery = new NeutralQuery(new NeutralCriteria(AUTHORIZED_ED_ORGS, NeutralCriteria.CRITERIA_IN, edorgs));
            Iterable<String> ids = repo.findAllIds("application", newQuery);
            for (String id : ids) {
                idList.add(id);
            }

            newQuery = new NeutralQuery(0);
            newQuery.addCriteria(new NeutralCriteria("allowed_for_all_edorgs", NeutralCriteria.OPERATOR_EQUAL, true));
            newQuery.addCriteria(new NeutralCriteria("authorized_for_all_edorgs", NeutralCriteria.OPERATOR_EQUAL, false));

            ids = repo.findAllIds("application", newQuery);
            for (String id : ids) {
                idList.add(id);
            }
            query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, idList));
        } //else - operator -- sees all apps
    }
}
