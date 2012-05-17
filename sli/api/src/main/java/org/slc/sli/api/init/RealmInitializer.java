package org.slc.sli.api.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for bootstrapping the initial SLI realm that must always exist into mongo.
 *
 */
@Component
public class RealmInitializer {
    
    @Value("${bootstrap.admin.realm.name}")
    private String realmName;
    
    @Value("${bootstrap.admin.realm.tenantId}")
    private String tenantId;
    
    @Value("${bootstrap.admin.realm.idpId}")
    private String idpId;
    
    @Value("${bootstrap.admin.realm.redirectEndpoint}")
    private String redirectEndpoint;
    
    @Autowired
    private Repository<Entity> repository;
    
    protected static final String REALM_RESOURCE = "realm";
    
    //This is what we use to look up the existing admin realm.  If this changes, we might end up with extra realms
    protected static final String ADMIN_REALM_ID = "Shared Learning Infrastructure";
    
    @PostConstruct
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init() {
        Entity adminRealm = findAdminRealm();
        if (adminRealm != null) {
            updateRealmIfNecessary(adminRealm);
        } else {
            Map body = createAdminRealmBody();
            //TODO: don't hardcode realm the mongo ID.  Currently a workaround since we reference it elsewhere in fixture data, e.g. long-lived sessions
            Entity entity = new MongoEntity("realm", "5a4bfe96-1724-4565-9db1-35b3796e3ce1", body, new HashMap<String, Object>());
            repository.update("realm", entity);
        }
    }

    /**
     * We only want to update the realm if it has changed.
     * It's a bad idea to drop the admin realm without checking
     * because we could potentially have multiple API machines all
     * hitting the same mongo instance and reinitializing the realm
     * @param realm
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void updateRealmIfNecessary(Entity realm) {
        Map oldBody = realm.getBody();
        Map newBody = createAdminRealmBody();
        long oldHash = InitializerUtils.checksum(oldBody);
        long newHash = InitializerUtils.checksum(newBody);
        if (oldHash != newHash) {
            realm.getBody().clear();
            realm.getBody().putAll(newBody);
            repository.update(REALM_RESOURCE, realm);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Map createAdminRealmBody() {
        Map body = new HashMap();
        body.put("name", realmName);
        body.put("uniqueIdentifier", ADMIN_REALM_ID);
        body.put("tenantId", tenantId);
        body.put("edOrg", "fakeab32-b493-999b-a6f3-sliedorg1234");
        body.put("admin", true);
        
        Map idp = new HashMap();
        idp.put("id", idpId);
        idp.put("redirectEndpoint", redirectEndpoint);
        body.put("idp", idp);
        
        Map mappings = new HashMap();
        mappings.put("role", getMappings());
        body.put("mappings", mappings);
        
        Map saml = new HashMap();
        saml.put("field", getFields());
        body.put("saml", saml);
        return body;

    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getFields() {
        List toReturn = new ArrayList();
        toReturn.add(createField("roles", "(.+)"));
        toReturn.add(createField("tenant", "(.+)"));
        toReturn.add(createField("edOrg", "(.+)"));
        toReturn.add(createField("userId", "(.+)"));
        toReturn.add(createField("userName", "(.+)"));
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List getMappings() {
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
     * Find the admin realm.
     * @return the admin realm entity, or null if not found
     */
    private Entity findAdminRealm() {
        Entity realm = repository.findOne(REALM_RESOURCE, new NeutralQuery(
                new NeutralCriteria("uniqueIdentifier", NeutralCriteria.OPERATOR_EQUAL, ADMIN_REALM_ID)));
        return realm;
    }

}
