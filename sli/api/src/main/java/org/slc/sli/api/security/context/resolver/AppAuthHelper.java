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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.security.ApplicationAuthorizationResource;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Contains helper methods for Application and ApplicationAuthorization access.
 *
 *
 */
@Component
public class AppAuthHelper {

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    @SuppressWarnings("rawtypes")
    public void checkApplicationAuthorization(String edorgId) {
        Entity app = getApplicationEntity();
        Map<String, Object> body = app.getBody();
        if (!body.containsKey("isBulkExtract") || (Boolean) body.get("isBulkExtract") == false) {
            throw new APIAccessDeniedException("Application is not approved for bulk extract", EntityNames.EDUCATION_ORGANIZATION, edorgId);
        }

        if (edorgId != null) {
            Entity appAuth = getApplicationAuthorizationEntity(app.getEntityId());
            if (appAuth == null
                || !((List) appAuth.getBody().get(ApplicationAuthorizationResource.EDORG_IDS))
                            .contains(edorgId)) {
                throw new APIAccessDeniedException("Application is not authorized for bulk extract", EntityNames.EDUCATION_ORGANIZATION, edorgId);
            }
        }
    }

    public String getApplicationId() {
        Entity app = getApplicationEntity();
        return app.getEntityId();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<String> getApplicationAuthorizationEdorgIds(String appId) {
        Entity appAuth = getApplicationAuthorizationEntity(appId);
        return appAuth == null ? Collections.emptyList() :
                (List) appAuth.getBody().get(ApplicationAuthorizationResource.EDORG_IDS);
    }

    private Entity getApplicationAuthorizationEntity(String appId) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ApplicationAuthorizationResource.APP_ID,
                NeutralCriteria.OPERATOR_EQUAL, appId));
        Entity appAuth = mongoEntityRepository.findOne(ApplicationAuthorizationResource.RESOURCE_NAME, query);

        return appAuth;
    }

    private Entity getApplicationEntity() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            throw new APIAccessDeniedException("Not logged in with valid oauth context", true);
        }
        final OAuth2Authentication oauth = (OAuth2Authentication) authentication;

        final String clientId = oauth.getClientAuthentication().getClientId();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("client_id", NeutralCriteria.OPERATOR_EQUAL, clientId));
        final Entity entity = mongoEntityRepository.findOne(EntityNames.APPLICATION, query);

        if (entity == null) {
            throw new APIAccessDeniedException("Could not find application with client_id=" + clientId, true);
        }

        return entity;
    }

}
