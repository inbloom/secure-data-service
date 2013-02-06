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


package org.slc.sli.api.security.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * Determines which applications a given user is authorized to use based on
 * that user's ed-org.
 *
 */
@Component
public class ApplicationAuthorizationValidator {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private EdOrgHelper helper;

    /**
     * Get the list of authorized apps for the user based on the user's LEA.
     *
     * No additional filtering is done on the results. E.g. if a user is a non-admin,
     * the admin apps will still show up in the list, or if an app is disabled it will
     * still show up.
     *
     * @param principal
     *
     * @return list of app IDs, or null if it couldn't be determined
     */
    @SuppressWarnings("unchecked")
    public boolean isAuthorizedForApp(Entity app, SLIPrincipal principal) {

        if (principal.isAdminRealmAuthenticated()) {
            return isAdminVisible(app);
        } else {
            if (isAutoAuthorized(app)) {
                return true;
            } else {
                Set<String> edOrgs = helper.getDirectEdorgs();
                NeutralQuery appAuthCollQuery = new NeutralQuery();
                appAuthCollQuery.addCriteria(new NeutralCriteria("applicationId", "=", app.getEntityId()));
                appAuthCollQuery.addCriteria(new NeutralCriteria("edorgs", NeutralCriteria.CRITERIA_IN, edOrgs));
                Entity authorizedApps = repo.findOne("applicationAuthorization", appAuthCollQuery);
                if (authorizedApps != null) {
                    if (isAutoApproved(app)) {
                        return true;
                    } else {
                        //query approved edorgs
                        List<String> approvedDistricts = new ArrayList<String>((List<String>) app.getBody().get("authorized_ed_orgs"));
                        List<String> myDistricts = helper.getDistricts(principal.getEntity());
                        approvedDistricts.retainAll(myDistricts);
                        return !approvedDistricts.isEmpty();
                    }
                }
            }
        }
        return false;
    }

    private boolean isAutoApproved(Entity app) {
        Boolean value = (Boolean) app.getBody().get("allowed_for_all_edorgs");
        return value != null && value.booleanValue();
    }

    private boolean isAutoAuthorized(Entity app) {
        Boolean value = (Boolean) app.getBody().get("authorized_for_all_edorgs");
        return value != null && value.booleanValue();
    }

    private boolean isAdminVisible(Entity app) {
        Boolean value = (Boolean) app.getBody().get("admin_visible");
        return value != null && value.booleanValue();
    }

    /**
     * Return a list of edorgs authorized to use the app, or null if the application is approved for all edorgs
     * @param clientId
     * @return
     */
    public Set<String> getAuthorizingEdOrgsForApp(String clientId) {
        //This is called before the SLIPrincipal has been set, so use TenantContext to get tenant rather than SLIPrincipal on SecurityContext
        Entity app = repo.findOne("application", new NeutralQuery(new NeutralCriteria("client_id", "=", clientId)));
        
        if (isAuthorizedForAllEdorgs(app)) {
            debug("App {} is authorized for all edorgs", clientId);
            return null;
        }
        
        NeutralQuery appAuthCollQuery = new NeutralQuery(new NeutralCriteria("applicationId", "=", app.getEntityId()));
        Entity authEntry = repo.findOne("applicationAuthorization", appAuthCollQuery);
        if (authEntry != null) {
            return new HashSet<String>((Collection) authEntry.getBody().get("edorgs"));
        } else {
            return new HashSet<String>();
        }
    }

    private boolean isAuthorizedForAllEdorgs(Entity app) {
        return app.getBody().get("authorized_for_all_edorgs") != null && (Boolean) app.getBody().get("authorized_for_all_edorgs");
    }

}
