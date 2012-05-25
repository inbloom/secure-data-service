package org.slc.sli.api.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
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

    @Value("${bootstrap.sandbox.realm.uniqueId}")
    private String sandboxUniqueId;

    @Value("${bootstrap.sandbox.realm.name}")
    private String sandboxRealmName;

    @Value("${bootstrap.sandbox.realm.idpId}")
    private String sandboxIdpId;

    @Value("${bootstrap.sandbox.realm.redirectEndpoint}")
    private String sandboxRedirectEndpoint;

    @Value("${bootstrap.sandbox.createSandboxRealm}")
    private boolean createSandboxRealm;

    @Autowired
    private Repository<Entity> repository;

    protected static final String REALM_RESOURCE = "realm";

    //This is what we use to look up the existing admin realm.  If this changes, we might end up with extra realms
    protected static final String ADMIN_REALM_ID = "Shared Learning Infrastructure";

    @PostConstruct
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() {
        // boostrap the admin realm
        Entity existingAdminRealm = findRealm(ADMIN_REALM_ID);
        Map<String, Object> bootstrapAdminRealmBody = createAdminRealmBody();
        if (existingAdminRealm != null) {
            updateRealmIfNecessary(existingAdminRealm, bootstrapAdminRealmBody);
        } else {
            // TODO: don't hardcode realm the mongo ID. Currently a workaround since we reference it
            // elsewhere in fixture data, e.g. long-lived sessions
            Entity entity = new MongoEntity(REALM_RESOURCE, "5a4bfe96-1724-4565-9db1-35b3796e3ce1", bootstrapAdminRealmBody,
                    new HashMap<String, Object>());
            repository.update("realm", entity);
        }

        // if sandbox mode, bootstrap the sandbox realm
        if (createSandboxRealm) {
            Entity existingSandboxRealm = findRealm(sandboxUniqueId);
            Map<String, Object> bootstrapSandboxRealmBody = createSandboxRealmBody();
            if (existingSandboxRealm != null) {
                updateRealmIfNecessary(existingSandboxRealm, bootstrapSandboxRealmBody);
            } else {
                // TODO: don't hardcode realm the mongo ID. Currently a workaround since we
                // reference it elsewhere in fixture data, e.g. long-lived sessions
                Entity entity = new MongoEntity(REALM_RESOURCE, "45b01db0-1bed-6dd7-a936-09ab31bd37fe",
                        bootstrapSandboxRealmBody, new HashMap<String, Object>());
                repository.update("realm", entity);
            }
        }
    }

    /**
     * We only want to update the realm if it has changed.
     * It's a bad idea to drop the admin realm without checking
     * because we could potentially have multiple API machines all
     * hitting the same mongo instance and reinitializing the realm
     * @param existingRealm
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void updateRealmIfNecessary(Entity existingRealm, Map<String, Object> newRealmBody) {
        Map oldBody = existingRealm.getBody();
        long oldHash = InitializerUtils.checksum(oldBody);
        long newHash = InitializerUtils.checksum(newRealmBody);
        if (oldHash != newHash) {
            existingRealm.getBody().clear();
            existingRealm.getBody().putAll(newRealmBody);
            repository.update(REALM_RESOURCE, existingRealm);
        }
    }

    protected Map<String, Object> createAdminRealmBody() {
        Map<String, Object> body = createRealmBody(ADMIN_REALM_ID, adminRealmName, adminTenantId, "fakeab32-b493-999b-a6f3-sliedorg1234",
                true, adminIdpId, adminRedirectEndpoint);
        Map<String, Object> mappings = new HashMap<String, Object>();
        mappings.put("role", getAdminMappings());
        body.put("mappings", mappings);
        Map<String, Object> saml = new HashMap<String, Object>();
        saml.put("field", getAdminFields());
        body.put("saml", saml);
        return body;
    }

    protected Map<String, Object> createSandboxRealmBody() {
        Map<String, Object> body = createRealmBody(sandboxUniqueId, sandboxRealmName, null, null, false, sandboxIdpId,
                sandboxRedirectEndpoint);
        Map<String, Object> mappings = new HashMap<String, Object>();
        mappings.put("role", getSandboxMappings());
        body.put("mappings", mappings);
        Map<String, Object> saml = new HashMap<String, Object>();
        saml.put("field", getSandboxFields());
        body.put("saml", saml);
        return body;
    }

    protected Map<String, Object> createRealmBody(String unqiueId, String name, String tenantId, String edOrg,
            boolean admin, String idpId, String redirectEndpoint) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("name", name);
        body.put("uniqueIdentifier", unqiueId);
        if (tenantId != null) {
            body.put("tenantId", tenantId);
        }
        if (edOrg != null) {
            body.put("edOrg", edOrg);
        }
        body.put("admin", admin);

        Map<String, Object> idp = new HashMap<String, Object>();
        idp.put("id", idpId);
        idp.put("redirectEndpoint", redirectEndpoint);
        body.put("idp", idp);
        return body;

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getAdminFields() {
        List toReturn = new ArrayList();
        toReturn.add(createField("roles", "(.+)"));
        toReturn.add(createField("tenant", "(.+)"));
        toReturn.add(createField("edOrg", "(.+)"));
        toReturn.add(createField("userId", "(.+)"));
        toReturn.add(createField("userName", "(.+)"));
        return toReturn;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getAdminMappings() {
        List toReturn = new ArrayList();
        toReturn.add(createRoleMapping(RoleInitializer.SLI_ADMINISTRATOR));
        toReturn.add(createRoleMapping(RoleInitializer.SLC_OPERATOR));
        toReturn.add(createRoleMapping(RoleInitializer.APP_DEVELOPER));
        toReturn.add(createRoleMapping(RoleInitializer.LEA_ADMINISTRATOR));
        toReturn.add(createRoleMapping(RoleInitializer.IT_ADMINISTRATOR));
        toReturn.add(createRoleMapping(RoleInitializer.EDUCATOR));
        toReturn.add(createRoleMapping(RoleInitializer.AGGREGATE_VIEWER));
        toReturn.add(createRoleMapping(RoleInitializer.LEADER));
        toReturn.add(createRoleMapping(RoleInitializer.REALM_ADMINISTRATOR));
        toReturn.add(createRoleMapping(RoleInitializer.SEA_ADMINISTRATOR));
        toReturn.add(createRoleMapping(RoleInitializer.INGESTION_USER));
        return toReturn;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getSandboxFields() {
        List toReturn = new ArrayList();
        toReturn.add(createField("roles", "(.+)"));
        toReturn.add(createField("tenant", "(.+)"));
        toReturn.add(createField("userId", "(.+)"));
        toReturn.add(createField("userName", "(.+)"));
        return toReturn;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getSandboxMappings() {
        List toReturn = new ArrayList();
        toReturn.add(createRoleMapping(RoleInitializer.IT_ADMINISTRATOR));
        toReturn.add(createRoleMapping(RoleInitializer.EDUCATOR));
        toReturn.add(createRoleMapping(RoleInitializer.AGGREGATE_VIEWER));
        toReturn.add(createRoleMapping(RoleInitializer.LEADER));
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

    private Map<String, Object> createRoleMapping(String role) {
        Map<String, Object> toReturn = new HashMap<String, Object>();
        List<String> roles = new ArrayList<String>();
        roles.add(role);
        toReturn.put("sliRoleName", role);
        toReturn.put("clientRoleName", roles);
        return toReturn;
    }

    /**
     * Find a realm.
     *
     * @return the realm entity, or null if not found
     */
    private Entity findRealm(String realmUniqueId) {
        Entity realm = repository.findOne(REALM_RESOURCE, new NeutralQuery(new NeutralCriteria("uniqueIdentifier",
                NeutralCriteria.OPERATOR_EQUAL, realmUniqueId)));
        return realm;
    }

}
