package org.slc.sli.api.init;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slc.sli.api.resources.security.ApplicationResource;

/**
 * Used to auto approve registration requests that are still pending if autoRegisterApps is enabled.
 * 
 * This would only be the case if the the property was disabled, and the operator decided
 * to enable it and restart the API.
 * 
 * Implementation note:  I wanted to just put this in the ApplicationResource, but
 * the PostConstruct annotation in web resources doesn't get invoked until the resource
 * is called for the first time.
 *
 */
@Component
public class AutoRegistrationInitializer {
    
    @Autowired
    @Value("${sli.sandbox.autoRegisterApps}")
    private boolean autoRegister;
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;
    
    @PostConstruct
    public void autoRegisterPendingAppsOnStartup() {
        if (autoRegister) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria(ApplicationResource.REGISTRATION + "." + ApplicationResource.STATUS, "=", "PENDING"));
            
            for (Entity app : repo.findAll(ApplicationResource.RESOURCE_NAME, query)) {
                info("Auto approving registration for application {} from {}.", 
                        app.getBody().get("name"), 
                        app.getBody().get("vendor"));
                
                @SuppressWarnings("unchecked")
                Map<String, Object> registration = (Map<String, Object>) app.getBody().get("registration");
                registration.put(ApplicationResource.APPROVAL_DATE, System.currentTimeMillis());
                registration.put(ApplicationResource.STATUS, "APPROVED");
                repo.update(ApplicationResource.RESOURCE_NAME, app);
            }
        }
    }

}
