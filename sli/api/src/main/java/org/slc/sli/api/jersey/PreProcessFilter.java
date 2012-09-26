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


package org.slc.sli.api.jersey;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.dal.MongoStat;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

/**
 * Pre-request processing filter.
 * Adds security information for the user
 * Records start time of the request
 *
 * @author dkornishev
 *
 */
@Component
public class PreProcessFilter implements ContainerRequestFilter {

    @Resource(name = "urlValidators")
    private List<URLValidator> urlValidators;

    @Autowired
    private OauthSessionManager manager;

    @Autowired
    private MongoStat mongoStat;

    @Value("${api.server.url}")
    private String apiServerUrl;

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        recordStartTime(request);
        validate(request);
        populateSecurityContext(request);
        mongoStat.clear();
        forwardSearch(request);
        return request;
    }

    private void populateSecurityContext(ContainerRequest request) {
        OAuth2Authentication auth = manager.getAuthentication(request.getHeaderValue("Authorization"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        TenantContext.setTenantId(((SLIPrincipal) auth.getPrincipal()).getTenantId());
    }

    private void recordStartTime(ContainerRequest request) {
        request.getProperties().put("startTime", System.currentTimeMillis());
    }

    private void forwardSearch(ContainerRequest request) {
        String requestURL = request.getRequestUri().toString();
        if (requestURL.contains("?")) {
            requestURL = requestURL.substring(0, requestURL.indexOf("?"));
        }
        String serverUrl = apiServerUrl + "api/rest/v1/";
        UriTemplate readAllUri = new UriTemplate(serverUrl + "{resource}");
        HashMap<String, String> uri = new HashMap<String, String>();
        if (readAllUri.match(requestURL , uri)) {
            String entity = uri.get("resource");
            if (!"search".equals(entity)) {
              UriBuilder builder =  UriBuilder.fromUri(request.getRequestUri().toString().replace(entity, "search"));
              builder.queryParam("_type", entity);
              request.setUris(request.getBaseUri(), builder.build());
            }
        }
    }

    /**
     * Validate the request url
     * @param request
     */
    private void validate(ContainerRequest request) {
        request.getProperties().put("logIntoDb", true );

        for (URLValidator validator : urlValidators) {
            if (!validator.validate(request.getRequestUri())) {
                request.getProperties().put("logIntoDb", false );
                List<ValidationError> errors = new ArrayList<ValidationError>();
                errors.add(0, new ValidationError(ValidationError.ErrorType.INVALID_VALUE, "URL", request.getRequestUri().toString(), null));
                throw new EntityValidationException("", "", errors);
            }
        }
    }


}
