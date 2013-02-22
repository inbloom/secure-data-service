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

package org.slc.sli.api.jersey;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slc.sli.api.cache.SessionCache;
import org.slc.sli.api.criteriaGenerator.DateFilterCriteriaGenerator;
import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.pdp.EndpointMutator;
import org.slc.sli.api.translator.URITranslator;
import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.MongoStat;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Pre-request processing filter.
 * Adds security information for the user
 * Records start time of the request
 *
 * @author dkornishev
 */
@Component
public class PreProcessFilter implements ContainerRequestFilter {

    @Resource(name = "urlValidators")
    private List<URLValidator> urlValidators;

    @Autowired
    private OauthSessionManager manager;

    @Autowired
    private ContextValidator contextValidator;

    @Autowired
    private MongoStat mongoStat;

    @Resource
    private EndpointMutator mutator;

    @Autowired
    private URITranslator translator;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private DateFilterCriteriaGenerator criteriaGenerator;
    
    @Resource
    private SessionCache sessions;

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        recordStartTime(request);
        validate(request);
        populateSecurityContext(request);
//        mongoStat.clear();
        mongoStat.startRequest();

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        principal.setSubEdOrgHierarchy(edOrgHelper.getSubEdOrgHierarchy(principal.getEntity()));

        info("uri: {} -> {}", request.getBaseUri().getPath(), request.getRequestUri().getPath());
        request.getProperties().put("original-request", request.getPath());
        mutator.mutateURI(SecurityContextHolder.getContext().getAuthentication(), request);
        contextValidator.validateContextToUri(request, principal);
        translator.translate(request);
        criteriaGenerator.generate(request);
        return request;
    }

    private void populateSecurityContext(ContainerRequest request) {
        String bearer = request.getHeaderValue("Authorization");
        
		OAuth2Authentication auth = this.sessions.get(bearer);
		if (auth == null) {
			auth = manager.getAuthentication(bearer);
			this.sessions.put(bearer, auth);
		}

		SecurityContextHolder.getContext().setAuthentication(auth);
        TenantContext.setTenantId(((SLIPrincipal) auth.getPrincipal()).getTenantId());
    }

    private void recordStartTime(ContainerRequest request) {
        request.getProperties().put("startTime", System.currentTimeMillis());
    }

    /**
     * Validate the request url
     *
     * @param request
     */
    private void validate(ContainerRequest request) {
        request.getProperties().put("logIntoDb", true);

        for (URLValidator validator : urlValidators) {
            if (!validator.validate(request.getRequestUri())) {
                request.getProperties().put("logIntoDb", false);
                List<ValidationError> errors = new ArrayList<ValidationError>();
                errors.add(0, new ValidationError(ValidationError.ErrorType.INVALID_VALUE, "URL", request
                        .getRequestUri().toString(), null));
                throw new EntityValidationException("", "", errors);
            }
        }
    }

}
