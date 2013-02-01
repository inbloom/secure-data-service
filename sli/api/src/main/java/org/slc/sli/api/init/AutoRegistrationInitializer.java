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
    @Value("${sli.autoRegisterApps}")
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
