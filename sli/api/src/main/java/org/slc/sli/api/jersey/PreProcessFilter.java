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
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.criteriaGenerator.DateFilterCriteriaGenerator;
import org.slc.sli.api.exceptions.RequestBlockedException;
import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.pdp.EndpointMutator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.translator.URITranslator;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.validation.URLValidator;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.MongoStat;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.ws.rs.core.PathSegment;
import javax.xml.bind.DatatypeConverter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pre-request processing filter. Adds security information for the user Records
 * start time of the request
 *
 * @author dkornishev
 */
@Component
public class PreProcessFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PreProcessFilter.class);

    private static final List<String> UPDATE_DELETE_OPERATIONS = Arrays.asList(ResourceMethod.PUT.toString(),
            ResourceMethod.PATCH.toString(), ResourceMethod.DELETE.toString());
    private static final List<String> CONTEXTERS = Arrays.asList(PathConstants.STUDENT_SCHOOL_ASSOCIATIONS,
            PathConstants.STUDENT_SECTION_ASSOCIATIONS, PathConstants.STUDENT_COHORT_ASSOCIATIONS,
            PathConstants.STUDENT_PROGRAM_ASSOCIATIONS);

    public static final List<String> DATE_RESTRICTED_ENTITIES = Arrays.asList(EntityNames.STAFF_PROGRAM_ASSOCIATION, EntityNames.STAFF_COHORT_ASSOCIATION,
            EntityNames.STAFF_ED_ORG_ASSOCIATION, EntityNames.TEACHER_SECTION_ASSOCIATION, EntityNames.TEACHER_SCHOOL_ASSOCIATION);

    @Resource(name = "urlValidators")
    private List<URLValidator> urlValidators;

    @Autowired
    private OauthSessionManager manager;

    @Autowired
    private MongoStat mongoStat;

    @Resource
    private EndpointMutator mutator;

    @Autowired
    private ContextValidator contextValidator;

    @Autowired
    private URITranslator translator;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private DateFilterCriteriaGenerator criteriaGenerator;

    @Autowired
    private ResourceEndPoint resourceEndPoint;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;
    private final Pattern ID_REPLACEMENT_PATTERN = Pattern.compile("([^/]+/[^/]+/)[^/]+(/.*)");

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        recordStartTime(request);
        validate(request);
        populateSecurityContext(request);
        // mongoStat.clear();
        mongoStat.startRequest();


        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        principal.setSubEdOrgHierarchy(edOrgHelper.getStaffEdOrgsAndChildren());

        Collection<GrantedAuthority> contextRights = principal.getAllContextRights(false);
        if (contextRights.contains(Right.STAFF_CONTEXT) && contextRights.contains(Right.TEACHER_CONTEXT)) {
            SecurityUtil.setUserContext(SecurityUtil.UserContext.DUAL_CONTEXT);
        } else if (contextRights.contains(Right.STAFF_CONTEXT)) {
            SecurityUtil.setUserContext(SecurityUtil.UserContext.STAFF_CONTEXT);
        } else if (contextRights.contains(Right.TEACHER_CONTEXT)) {
            SecurityUtil.setUserContext(SecurityUtil.UserContext.TEACHER_CONTEXT);
        } else {
            SecurityUtil.setUserContext(SecurityUtil.UserContext.NO_CONTEXT);
        }

        LOG.info("uri: {} -> {}", request.getMethod(), request.getRequestUri().getPath());
        request.getProperties().put("original-request", request.getPath());
        // TODO Determine expected behavior for hosted and federated users querying resources from non-admin type apps
        // SessionUtil.checkAccess(SecurityContextHolder.getContext().getAuthentication(), request, repo);
        mutator.mutateURI(SecurityContextHolder.getContext().getAuthentication(), request);
        injectObligations(request);
        validateNotBlockGetRequest(request);
        SecurityUtil.setTransitive(ContextValidator.isTransitive(request.getPathSegments()));

        if (ResourceMethod.getWriteOps().contains(request.getMethod()) && contextValidator.isUrlBlocked(request)) {
            throw new APIAccessDeniedException(String.format("url %s is not accessible.", request.getAbsolutePath().toString()));
        }

        if (isUpdateOrDelete(request.getMethod())) {
            contextValidator.validateContextToUri(request, principal);
        }

        translator.translate(request);
        criteriaGenerator.generate(request);
        return request;
    }

    private void injectObligations(ContainerRequest request) {
        // Create obligations
        SLIPrincipal prince = SecurityUtil.getSLIPrincipal();

        if (request.getPathSegments().size() > 3) {	// not applied on two parters

            String base = request.getPathSegments().get(1).getPath();
            String assoc = request.getPathSegments().get(3).getPath();

            if (CONTEXTERS.contains(base)) {
                LOG.info("Skipping date-based obligation injection because association {} is base level URI", base);
                return;
            }

            if(base.equals(ResourceNames.PROGRAMS) || base.equals(ResourceNames.COHORTS)) {
                if(assoc.equals(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS) || assoc.equals(ResourceNames.STAFF_COHORT_ASSOCIATIONS)) {
                    prince.setStudentAccessFlag(false);
                }
            }

            if(SecurityUtil.isStudent()) {
                List<NeutralQuery> oblong = construct("endDate");

                for(String entity : DATE_RESTRICTED_ENTITIES) {
                    prince.addObligation(entity, oblong);
                }
            }

            for (PathSegment seg : request.getPathSegments()) {
                String resourceName = seg.getPath();
                if (ResourceNames.STUDENTS.equals(resourceName)) {	// once student is encountered,
                                                                   // no more obligations
                    break;
                }

                if (CONTEXTERS.contains(resourceName) && !request.getQueryParameters().containsKey("showAll")) {
                    if (ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS.equals(resourceName)) {
                        prince.addObligation(resourceName.replaceAll("s$", ""), construct("exitWithdrawDate"));
                    } else {
                        prince.addObligation(resourceName.replaceAll("s$", ""), construct("endDate"));
                    }

                    LOG.info("Injected a date-based obligation on association: {}", resourceName);
                }
            }
        }
    }

    private void populateSecurityContext(ContainerRequest request) {
        OAuth2Authentication auth = manager.getAuthentication(request.getHeaderValue("Authorization"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        TenantContext.setTenantId(((SLIPrincipal) auth.getPrincipal()).getTenantId());
    }

    /**
     * Creates a list of criteria which will be OR'ed when queries that are
     * relevant are being executed
     *
     * @param fieldName
     * @return
     */
    private List<NeutralQuery> construct(String fieldName) {
        String now = DatatypeConverter.printDate(Calendar.getInstance());

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(fieldName, NeutralCriteria.CRITERIA_GT, now));
        NeutralQuery nq2 = new NeutralQuery(new NeutralCriteria(fieldName, NeutralCriteria.CRITERIA_EXISTS, false));

        return Arrays.asList(nq, nq2);

    }

    /**
     * Returns true if the request is a write operation.
     *
     * @param request
     *            Request to be checked.
     * @return True if the request method is a PUT, PATCH, or DELETE, false
     *         otherwise.
     */
    private boolean isUpdateOrDelete(String operation) {
        return UPDATE_DELETE_OPERATIONS.contains(operation);
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

        if (m.matches()) {
            // transform requestPath from "v1.x/foo/2344,3453,5345/bar" to
            // "v1.x/foo/{id}/bar"
            requestPath = m.group(1) + PathConstants.ID_PLACEHOLDER + m.group(2);
        }

        if (this.resourceEndPoint.getBlockGetRequestEndPoints().contains(requestPath)) {
            throw new RequestBlockedException(request.getPath());
        }
    }
}
