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

package org.slc.sli.api.util;

import org.slc.sli.api.resources.security.ApplicationResource;
import org.slc.sli.api.exceptions.APIAccessDeniedException;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Holder for request information utilities.
 *
 * @author ss
 */
public class SessionUtil {

    private static final String [] ADMIN_REQUESTS = {"/apps", "/applicationAuthorizations", "/applicationAuthorization", "/customRole", "/users", "/adminDelegation", "/provision", "/tenants"};

    public static void checkAccess(Authentication auth, ContainerRequest request, Repository<Entity> repo) {
        
        String clientId = ((OAuth2Authentication) auth).getClientAuthentication().getClientId();

        if(isAdminApp(clientId, repo)==false&&isAdminRequest(request.getRequestUri().toString())==true) {
            throw new APIAccessDeniedException(String.format("url %s is not accessible to non-Admin Applications.", request.getRequestUri().toString()));
        }
    }
    
    // Check the is_admin field in the 'sli' db 'application' collection to determine if the application is
    // and 'admin' type application
    public  static boolean isAdminApp(String clientId, Repository<Entity> repo) {
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ApplicationResource.CLIENT_ID, "=", clientId));
        Entity app = repo.findOne(EntityNames.APPLICATION, nq);

        if (app == null) {
            return false;
        }

        return (Boolean) app.getBody().get("is_admin");
    }

    private  static boolean isAdminRequest(String request) {
        for(String rq: ADMIN_REQUESTS) {
            if(request.contains(rq)) {
                return true;
            }
        }
        return false;
    }
}