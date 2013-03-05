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

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.criteriaGenerator.DateFilterCriteriaGenerator;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.pdp.EndpointMutator;
import org.slc.sli.api.service.EntityNotFoundException;
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
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pre-request processing filter. Adds security information for the user Records start time of the request
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

    @Autowired
    private ResourceEndPoint resourceEndPoint;

    private final Pattern ID_REPLACEMENT_PATTERN = Pattern.compile("([^/]+/[^/]+/)[^/]+(/.*)");

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        recordStartTime(request);
        validate(request);
        validateNotBlockGetRequest(request);
        populateSecurityContext(request);
        // mongoStat.clear();
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
        OAuth2Authentication auth = manager.getAuthentication(request.getHeaderValue("Authorization"));
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
                errors.add(0, new ValidationError(ValidationError.ErrorType.INVALID_VALUE, "URL", request.getRequestUri().toString(), null));
                throw new EntityValidationException("", "", errors);
            }
        }
    }

    /**
     * Validate the request URL is not blocked
     *
     * @param request
     */
    private void validateNotBlockGetRequest(ContainerRequest request) {
        if (!request.getMethod().equals(RequestMethod.GET.name())) {
            return;
        }

        String requestPath = request.getPath();
        Matcher m = ID_REPLACEMENT_PATTERN.matcher(requestPath);

        if (m.find()){
            // transform requestPath from "v1.x/foo/2344,3453,5345/bar" to "v1.x/foo/{id}/bar"
            requestPath = m.group(1) + PathConstants.ID_PLACEHOLDER + m.group(2);
        }

        if (this.resourceEndPoint.getBlockGetRequestEndPoints().contains(requestPath)) {
            throw new EntityNotFoundException(request.getPath());
        }
    }

}
