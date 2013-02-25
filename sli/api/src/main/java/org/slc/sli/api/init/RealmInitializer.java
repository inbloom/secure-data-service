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

package org.slc.sli.api.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Class for bootstrapping the initial SLI realm that must always exist into mongo.
 *
 */
@Component
public class RealmInitializer {

    @Value("${bootstrap.admin.realm.name}")
    private String adminRealmName;

    @Value("${bootstrap.admin.realm.tenantId}")
    private String adminTenantId;

    @Value("${bootstrap.admin.realm.idpId}")
    private String adminIdpId;

    @Value("${bootstrap.admin.realm.redirectEndpoint}")
    private String adminRedirectEndpoint;

    @Value("${bootstrap.developer.realm.name}")
    private String devRealmName;

    @Value("${bootstrap.developer.realm.uniqueId}")
    private String devUniqueId;

    @Value("${bootstrap.developer.realm.idpId}")
    private String devIdpId;

    @Value("${bootstrap.developer.realm.redirectEndpoint}")
    private String devRedirectEndpoint;

    @Value("${sli.sandbox.enabled}")
    private boolean isSandbox;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repository;

    protected static final String REALM_RESOURCE = "realm";

    // This is what we use to look up the existing admin realm. If this changes, we might end up
    // with extra realms
    public static final String ADMIN_REALM_ID = "Shared Learning Collaborative";

    @PostConstruct
    public void bootstrap() {
        // boostrap the admin realm
        Map<String, Object> bootstrapAdminRealmBody = createAdminRealmBody();
        createOrUpdateRealm(ADMIN_REALM_ID, bootstrapAdminRealmBody);

        if (!isSandbox) {
            Map<String, Object> bootstrapDeveloperRealmBody = createDeveloperRealmBody();
            createOrUpdateRealm(devUniqueId, bootstrapDeveloperRealmBody);
        }
    }

    private void createOrUpdateRealm(String realmId, Map<String, Object> realmEntity) {
        Entity existingRealm = findRealm(realmId);
        if (existingRealm != null) {
            info("{} realm already exists --> updating if necessary", realmId);
            updateRealmIfNecessary(existingRealm, realmEntity);
        } else {
            info("Creating {} realm.", realmId);
            repository.create(REALM_RESOURCE, realmEntity);
        }

    }

    /**
     * We only want to update the realm if it has changed.
     * It's a bad idea to drop the admin realm without checking
     * because we could potentially have multiple API machines all
     * hitting the same mongo instance and reinitializing the realm
     *
     * @param existingRealm
     */
    @SuppressWarnings({ "rawtypes" })
    private void updateRealmIfNecessary(Entity existingRealm, Map<String, Object> newRealmBody) {
        Map oldBody = existingRealm.getBody();
        long oldHash = InitializerUtils.checksum(oldBody);
        long newHash = InitializerUtils.checksum(newRealmBody);
        if (oldHash != newHash) {
            existingRealm.getBody().clear();
            existingRealm.getBody().putAll(newRealmBody);
            if (repository.update(REALM_RESOURCE, existingRealm)) {
                info("Successfully updated realm: {}", new Object[] { newRealmBody.get("name") });
            } else {
                warn("Failed to update realm: {}", new Object[] { newRealmBody.get("name") });
            }
        } else {
            info("No need to update realm: {}", new Object[] { existingRealm.getBody().get("name") });
        }
    }

    protected Map<String, Object> createAdminRealmBody() {
        Map<String, Object> body = createRealmBody(ADMIN_REALM_ID, adminRealmName, adminTenantId,
                "fakeab32-b493-999b-a6f3-sliedorg1234", true, false, adminIdpId, adminRedirectEndpoint);

        return insertSaml(body, true, false);
    }

    protected Map<String, Object> createDeveloperRealmBody() {
        Map<String, Object> body = createRealmBody(devUniqueId, devRealmName, "", null, false, true, devIdpId,
                devRedirectEndpoint);

        return insertSaml(body, false, true);
    }

    private Map<String, Object> insertSaml(Map<String, Object> body, boolean isAdminRealm, boolean isDeveloperRealm) {
        Map<String, Object> saml = new HashMap<String, Object>();
        saml.put("field", getFields(isAdminRealm, isDeveloperRealm));
        body.put("saml", saml);

        return body;
    }

    private Map<String, Object> createRealmBody(String uniqueId, String name, String tenantId, String edOrg,
            boolean admin, boolean developer, String idpId, String redirectEndpoint) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("name", name);
        body.put("uniqueIdentifier", uniqueId);
        if (tenantId != null) {
            body.put("tenantId", tenantId);
        }
        if (edOrg != null) {
            body.put("edOrg", edOrg);
        }
        body.put("admin", admin);
        body.put("developer", developer);

        Map<String, Object> idp = new HashMap<String, Object>();
        idp.put("id", idpId);
        idp.put("redirectEndpoint", redirectEndpoint);
        body.put("idp", idp);
        return body;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getFields(boolean isAdminRealm, boolean isDeveloperRealm) {
        List toReturn = new ArrayList();
        toReturn.add(createField("roles", "(.+)"));
        toReturn.add(createField("tenant", "(.+)"));
        toReturn.add(createField("userId", "(.+)"));
        toReturn.add(createField("userName", "(.+)"));
        toReturn.add(createField("userType", "(.+)"));
        toReturn.add(createField("isAdmin", "(.+)"));
        toReturn.add(createField("mail", "(.+)"));

        if (isDeveloperRealm || isAdminRealm) {
            toReturn.add(createField("givenName", "(.+)"));
            toReturn.add(createField("sn", "(.+)"));
            toReturn.add(createField("vendor", "(.+)"));
        }

        if (isAdminRealm) {
            toReturn.add(createField("edOrg", "(.+)"));
        }

        return toReturn;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map createField(String name, String transform) {
        Map toReturn = new HashMap();
        toReturn.put("clientName", name);
        toReturn.put("sliName", name);
        toReturn.put("transform", transform);
        return toReturn;
    }

    /**
     * Find a realm.
     *
     * @return the realm entity, or null if not found
     */
    private Entity findRealm(String realmUniqueId) {
        return repository.findOne(REALM_RESOURCE, new NeutralQuery(new NeutralCriteria("uniqueIdentifier",
                NeutralCriteria.OPERATOR_EQUAL, realmUniqueId)));
    }
}
