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
import java.util.List;

import org.slc.sli.api.init.RealmInitializer;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component
public class RealmHelper {

    @Value("${sli.sandbox.enabled}")
    protected boolean isSandboxEnabled;

    @Value("${bootstrap.sandbox.realm.uniqueId}")
    private String sandboxUniqueId;

    @Autowired

    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    EdOrgHelper edorgHelper;

    public String getSandboxRealmId() {
        Entity realm = SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {

            @Override
            public Entity execute() {
                NeutralQuery realmQuery = new NeutralQuery();
                realmQuery.addCriteria(new NeutralCriteria("uniqueIdentifier", NeutralCriteria.OPERATOR_EQUAL, sandboxUniqueId));
                return repo.findOne("realm", realmQuery);
            }
        });

        if (realm != null) {
            return realm.getEntityId();
        }
        return null;
    }

    public String getAdminRealmId() {
        Entity realm = SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {

            @Override
            public Entity execute() {
                NeutralQuery realmQuery = new NeutralQuery();
                realmQuery.addCriteria(new NeutralCriteria("uniqueIdentifier", NeutralCriteria.OPERATOR_EQUAL, RealmInitializer.ADMIN_REALM_ID));
                return repo.findOne("realm", realmQuery);
            }
        });

        return realm.getEntityId();
    }

    public List<String> getPreferredLoginRealmIds(Entity userEntity) {
        //If there's a realm directly associated with your edorg, we'll require that
        List<String> toReturn = new ArrayList<String>();
        List<String> edOrgs = edorgHelper.getDirectEdOrgAssociations(userEntity);
        for (String edOrgId : edOrgs) {
            Entity edOrgEntity = repo.findById("educationOrganization", edOrgId);
            Entity realm = getRealm(edOrgEntity);
            if (realm != null) {
                toReturn.add(realm.getEntityId());
                debug("User is directly associated with realm {} through edorg {}",
                        realm.getBody().get("name"),
                        edOrgEntity.getBody().get("nameOfInstitution"));
            }
        }

        return toReturn;
    }

    /**
     * If the edorg is directly associated with a realm, return that realm's entity.
     *
     * Otherwise return null.
     * @param edOrg
     * @return
     */
    public Entity getRealm(Entity edOrg) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("edOrg", "=", edOrg.getBody().get("stateOrganizationId")));
        query.addCriteria(new NeutralCriteria("body.tenantId", "=", TenantContext.getTenantId(), false));
        return repo.findOne("realm", query);

    }


    /**
     * Determine if the user is allowed to login to the specified realm.
     *
     * The rules are as follows:
     * If the user is associated with an edorg, and that edorg is directly associated
     * with a realm, then the user is only allowed to login to that realm.
     *
     * If the user isn't directly associated with any realm, we look at their parent
     * edorgs, and the first one of those that has a valid realm associated is the
     * realm they have to log into.
     *
     * If the user doesn't have any parent edorgs, i.e. is an SEA, then we go one level
     * down to the LEAs directly under the SEA.  If any of those has a realm that the user
     * logged in through, it's valid.
     *
     * @param userEntity
     * @param realm
     * @param tenantId
     * @return
     */
    public boolean isUserAllowedLoginToRealm(Entity userEntity, Entity realm) {

        //Always allow sandbox realm
        if (realm.getBody().get("uniqueIdentifier").equals(sandboxUniqueId)) {
            return true;
        }

        //Preferred login realms are realms the user would be directly associated with
        List<String> preferredRealms = getPreferredLoginRealmIds(userEntity);
        if (preferredRealms.size() > 0) {
            return preferredRealms.contains(realm.getEntityId());
        }

        //There wasn't a preferred realm, so let's check other realms in the hierarchy
        List<String> userEdorgs = edorgHelper.getDirectEdOrgAssociations(userEntity);
        for (String id : userEdorgs) {
            Entity edorgEntity = repo.findById("educationOrganization", id);

            if (isValidForLogin(edorgEntity, realm)) {
                debug("User is allowed to login to realm {} through edorg {}",
                        realm.getBody().get("name"),
                        edorgEntity.getBody().get("nameOfInstitution"));
                return true;
            } else {
                debug("User cannot login to realm {} through edorg {}",
                        realm.getBody().get("name"),
                        edorgEntity.getBody().get("nameOfInstitution"));
            }
        }

        return false;
    }

    private boolean isValidForLogin(Entity edOrgEntity, Entity realm) {
        List<String> edOrgIds = edorgHelper.getParentEdOrgs(edOrgEntity);
        for (String parentId : edOrgIds) {
            Entity realmEnt = getRealm(repo.findById("educationOrganization", parentId));
            if (realmEnt != null) {
                return realmEnt.getEntityId().equals(realm.getEntityId());
            }
        }

        if (edOrgIds.size() == 0) { //must be an SEA
            for (String childId : edorgHelper.getChildLEAsOfEdOrg(edOrgEntity)) {
                Entity realmEnt = getRealm(repo.findById("educationOrganization", childId));
                if (realmEnt != null) {
                    if (realmEnt.getEntityId().equals(realm.getEntityId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get the ID of the realm the user is associated with.
     *
     * In the case of sandbox, this is always the sandbox realm.
     * If it's production and the user is an admin user, this is the realm
     * they can administer, not the realm they logged into.
     *
     *
     * @return the realm's mongo id, or null if a realm doesn't exist.
     */
    public String getAssociatedRealmId() {

        if (isSandboxEnabled) {
            return getSandboxRealmId();
        } else {
            NeutralQuery realmQuery = new NeutralQuery();
            String edOrg = SecurityUtil.getEdOrg();
            debug("Looking up realm for edorg {}.", edOrg);
            realmQuery.addCriteria(new NeutralCriteria("edOrg", NeutralCriteria.OPERATOR_EQUAL, edOrg));
            realmQuery.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                    .getTenantId()));
            Entity realm = repo.findOne("realm", realmQuery);
            if (realm != null) {
                return realm.getEntityId();
            }
            return null;
        }

    }

}
