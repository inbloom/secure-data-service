package org.slc.sli.api.security.context.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.init.RealmInitializer;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

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
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    public String getSandboxRealmId() {
        Entity realm = SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {

            @Override
            public Entity execute() {
                NeutralQuery realmQuery = new NeutralQuery();
                realmQuery.addCriteria(new NeutralCriteria("uniqueIdentifier", NeutralCriteria.OPERATOR_EQUAL, sandboxUniqueId));
                return repo.findOne("realm", realmQuery);
            }
        });

        return realm.getEntityId();
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
            Entity realm = repo.findOne("realm", realmQuery);
            if (realm != null) {
                return realm.getEntityId();
            }
            return null;
        }

    }

}
