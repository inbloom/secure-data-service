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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.init.RealmInitializer;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 *
 *
 */
@Component
public class RealmHelper {
    public static final String USER_SESSION = "userSession";
	
    @Value("${sli.sandbox.enabled}")
    protected boolean isSandboxEnabled;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    EdOrgHelper edorgHelper;

    public String getSandboxRealmId() {
        // sandbox and admin realm are the same
        return getAdminRealmId();
    }

    public String getAdminRealmId() {
        Entity realm = SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {

            @Override
            public Entity execute() {
                NeutralQuery realmQuery = new NeutralQuery();
                realmQuery.addCriteria(new NeutralCriteria("uniqueIdentifier", NeutralCriteria.OPERATOR_EQUAL,
                        RealmInitializer.ADMIN_REALM_ID));
                return repo.findOne("realm", realmQuery);
            }
        });

        return realm.getEntityId();
    }
    
    public String getRealmEdOrg(String realmId) {
        NeutralQuery realmQuery = new NeutralQuery();
        realmQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, realmId));
        Entity realmEntity = repo.findOne("realm", realmQuery);
        if (realmEntity != null) {
        	Map<String, Object> body = realmEntity.getBody();
        	if (body != null) {
        		String stateOrgId = (String) body.get("edOrg");
        		String tenantId = (String) body.get("tenantId");
        		if (stateOrgId != null && tenantId != null) {
        			return getEdOrgFromTenantDB(tenantId, stateOrgId);
        		}
        	}
        }
        return null;
    }

    public String getEdOrgFromTenantDB(String tenantId, String stateOrgId) {
        NeutralQuery edOrgIdQuery = new NeutralQuery();
        edOrgIdQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, stateOrgId));

        // query the specified tenant db
        String originalTenantId = TenantContext.getTenantId();
        TenantContext.setTenantId(tenantId);
        Entity edOrgEntity = repo.findOne("educationOrganization", edOrgIdQuery);
        TenantContext.setTenantId(originalTenantId);
        if (edOrgEntity != null) {
        	Map<String, Object> body = edOrgEntity.getBody();
        	if (body != null) {
        		return (String) body.get("_id");
        	}
        }

    	return null;
    }
    
    public String getRealmFromSession(String sessionId) {
       NeutralQuery sessionQuery = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, sessionId));
       Entity userSession = repo.findOne(USER_SESSION, sessionQuery);
        if (userSession != null) {
        	Map<String, Object> body = userSession.getBody();
        	
        	if (body != null) {
        		Map<String, Object> principal = (Map<String, Object>) body.get("principal"); 
        		return (String) principal.get("realm");
        	}
        }
        return null;
    }

        
    public List<String> getPreferredLoginRealmIds(Entity userEntity) {
        // If there's a realm directly associated with your edorg, we'll require that
        List<String> toReturn = new ArrayList<String>();
        Set<String> edOrgs = edorgHelper.getDirectEdorgs(userEntity);
        for (String edOrgId : edOrgs) {
            Entity edOrgEntity = repo.findById("educationOrganization", edOrgId);
            Iterable<Entity> realms = getRealms(edOrgEntity);
            for (Entity realm : realms) {
                toReturn.add(realm.getEntityId());
                debug("User is directly associated with realm: {} through edorg: {}", realm.getBody().get("name"),
                        edOrgEntity.getBody().get("nameOfInstitution"));
            }
        }

        return toReturn;
    }

    /**
     * If the edorg is directly associated with a realm, return that realm's entity.
     *
     * Otherwise return null.
     *
     * @param edOrg
     * @return
     */
    public Iterable<Entity> getRealms(Entity edOrg) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("edOrg", "=", edOrg.getBody().get("stateOrganizationId")));
        query.addCriteria(new NeutralCriteria("tenantId", "=", TenantContext.getTenantId()));
        return repo.findAll(EntityNames.REALM, query);
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
     * down to the LEAs directly under the SEA. If any of those has a realm that the user
     * logged in through, it's valid.
     *
     * @param userEntity
     * @param realm
     * @param tenantId
     * @return
     */
    public boolean isUserAllowedLoginToRealm(Entity userEntity, Entity realm) {

        // Always allow sandbox realm
        if (isSandboxEnabled) {
            return true;
        }

        // Preferred login realms are realms the user would be directly associated with
        List<String> preferredRealms = getPreferredLoginRealmIds(userEntity);
        if (preferredRealms.size() > 0) {
            return preferredRealms.contains(realm.getEntityId());
        }

        // There wasn't a preferred realm, so let's check other realms in the hierarchy
        Set<String> userEdorgs = edorgHelper.getDirectEdorgs(userEntity);
        for (String id : userEdorgs) {
            Entity edorgEntity = repo.findById("educationOrganization", id);

            if (isValidForLogin(edorgEntity, realm)) {
                debug("User is allowed to login to realm: {} through edorg: {}", realm.getBody().get("name"),
                        edorgEntity.getBody().get("nameOfInstitution"));
                return true;
            } else {
                debug("User cannot login to realm: {} through edorg: {}", realm.getBody().get("name"), edorgEntity
                        .getBody().get("nameOfInstitution"));
            }
        }

        return false;
    }

    private boolean isValidForLogin(Entity edOrgEntity, Entity realm) {
        List<String> edOrgIds = edorgHelper.getParentEdOrgs(edOrgEntity);

        for (String parentId : edOrgIds) {
            Iterable<Entity> realmEnts = getRealms(repo.findById("educationOrganization", parentId));
            if (realmEnts.iterator().hasNext()) {
                for (Entity realmEnt : realmEnts) {
                    if (realmEnt.getEntityId().equals(realm.getEntityId())) {
                        return true;
                    }
                }
                return false;
            }
        }

        // must be an SEA
        if (edOrgIds.size() == 0) {
            for (String childId : edorgHelper.getChildLEAsOfEdOrg(edOrgEntity)) {
                Iterable<Entity> realmEnts = getRealms(repo.findById("educationOrganization", childId));
                for (Entity realmEnt : realmEnts) {
                    if (realmEnt.getEntityId().equals(realm.getEntityId())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get the IDs of the realms the user is associated with.
     *
     * In the case of sandbox, this is always the sandbox realm.
     * If it's production and the user is an admin user, this is the realm
     * they can administer, not the realm they logged into.
     *
     *
     * @return the realm's mongo id, or null if a realm doesn't exist.
     */
    public Set<String> getAssociatedRealmIds() {
        HashSet<String> toReturn = new HashSet<String>();
        if (isSandboxEnabled) {
            toReturn.add(getSandboxRealmId());
        } else {
            NeutralQuery realmQuery = new NeutralQuery();
            String edOrg = SecurityUtil.getEdOrg();
            debug("Looking up realms for edorg {}.", edOrg);
            realmQuery.addCriteria(new NeutralCriteria("edOrg", NeutralCriteria.OPERATOR_EQUAL, edOrg));
            realmQuery.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, SecurityUtil
                    .getTenantId()));
            Iterable<String> realmIds = repo.findAllIds("realm", realmQuery);
            for (String id : realmIds) {
                toReturn.add(id);
            }
        }
        return toReturn;

    }

}
