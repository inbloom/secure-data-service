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

package org.slc.sli.api.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.AppAuthHelper;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.GenericToEdOrgValidator;
import org.slc.sli.api.security.service.AuditLogger;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.encrypt.security.CertificateValidationHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.NeutralQuery.SortOrder;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * The Bulk Extract Endpoints.
 *
 * @author dkornishev
 *
 */
@Component
@Path("/bulk")
@Produces({"application/x-tar", MediaType.APPLICATION_JSON + "; charset=utf-8"})
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);

    private static final String SAMPLED_FILE_NAME = "sample-extract.tar";

    public static final String BULK_EXTRACT_FILES = "bulkExtractFiles";
    public static final String BULK_EXTRACT_FILE_PATH = "path";
    public static final String BULK_EXTRACT_DATE = "date";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    @Value("${sli.bulk.extract.deltasEnabled:true}")
    private boolean deltasEnabled;

    @Autowired
    private GenericToEdOrgValidator edorgValidator;

    @Autowired
    private EdOrgHelper helper;

    @Autowired
    private AppAuthHelper appAuthHelper;

    @Autowired
    private CertificateValidationHelper validator;

    @Context
    private UriInfo uri;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Autowired
    private AuditLogger auditLogger;

    @Autowired
    private FileResource fileResource;

    private SLIPrincipal getPrincipal() {
        return (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Creates a streaming response for the sample data file.
     *
     * @param request - HTTP servlet request for public bulk extract file
     *
     * @return A response with the sample extract file
     *
     * @throws Exception On Error
     */
    @GET
    @Path("extract")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response get(@Context HttpServletRequest request) throws Exception {
        LOG.info("Received request to stream sample bulk extract...");

        logSecurityEvent("Received request to stream sample bulk extract");

        validateRequestCertificate(request);

        final InputStream is = this.getClass().getResourceAsStream("/bulkExtractSampleData/" + SAMPLED_FILE_NAME);

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                int n;
                byte[] buffer = new byte[1024];
                while ((n = is.read(buffer)) > -1) {
                    output.write(buffer, 0, n);
                }
            }
        };

        ResponseBuilder builder = Response.ok(out);
        builder.header("content-disposition", "attachment; filename = " + SAMPLED_FILE_NAME);
        builder.header("last-modified", "Not Specified");
        logSecurityEvent("Successful request to stream sample bulk extract");
        return builder.build();
    }

    /**
     * Send an edOrg private data extract.
     *
     * @param context - HTTP context of request
     * @param request - HTTP servlet request for public bulk extract file
     * @param edOrgId - The uuid of the edOrg to get the extract
     *
     * @return - A response with an edOrg tar file
     */
    @GET
    @Path("extract/{edOrgId}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getEdOrgExtract(@Context HttpContext context, @Context HttpServletRequest request, @PathParam("edOrgId") String edOrgId) {
        logSecurityEvent("Received request to stream Edorg data");

        if (edOrgId == null || edOrgId.isEmpty()) {
            logSecurityEvent("Failed request to stream edOrg data, missing edOrgId");
            throw new IllegalArgumentException("edOrgId cannot be missing");
        }

        validateRequestCertificate(request);
        validateCanAccessEdOrgExtract(edOrgId);

        return getEdOrgExtractResponse(context.getRequest(), edOrgId, null);
    }

    /**
     * Send a tenant public data full extract.
     *
     * @param context - HTTP context of request
     * @param request - HTTP servlet request for public bulk extract file
     *
     * @return - A response with a public extract tar file
     */
    @GET
    @Path("extract/public")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getPublicExtract(@Context HttpContext context, @Context HttpServletRequest request) {
        logSecurityEvent("Received request to stream public data");

        validateRequestCertificate(request);

        return getPublicExtractResponse(context.getRequest(), null);
    }

    /**
     * Send the list of BE file links for all edOrgs and public data for which the calling user and application have access.
     *
     * @param context - HTTP context of request
     * @param request - HTTP servlet request for public bulk extract file
     *
     * @return A response with the complete list of BE file links for all edOrgs and public data for this user/app.
     *
     * @throws Exception On Error.
     */
    @GET
    @Path("extract/list")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getBulkExtractList(@Context HttpServletRequest request, @Context HttpContext context) throws Exception {
        LOG.info("Received request for list of links for all edOrgs and public data for this user/app");
        logSecurityEvent("Received request for list of links for all edOrgs and public data for this user/app");
        validateRequestAndApplicationAuthorization(request);

        logSecurityEvent("Successful request for list of links for all edOrgs and public data for this user/app");
        return getPublicAndEdOrgListResponse(context);
    }


    /**
     * Stream a delta response.
     *
     * @param context - HTTP context of request
     * @param request - HTTP servlet request for public bulk extract file
     * @param date the date of the delta
     * @param edOrgId the uuid of the edOrg to get delta extract for
     *
     * @return A response with a delta extract file.
     */
    @GET
    @Path("extract/{edOrgId}/delta/{date}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getDelta(@Context HttpServletRequest request, @Context HttpContext context,
                             @PathParam("edOrgId") String edOrgId, @PathParam("date") String date) {
        logSecurityEvent("Received request to stream Edorg delta bulk extract data");
        if (deltasEnabled) {
            LOG.info("Retrieving delta bulk extract for {}, at date {}", edOrgId, date);
            if (edOrgId == null || edOrgId.isEmpty()) {
                logSecurityEvent("Failed delta request, missing edOrgId");
                throw new IllegalArgumentException("edOrgId cannot be missing");
            }
            if (date == null || date.isEmpty()) {
                logSecurityEvent("Failed delta request, missing date");
                throw new IllegalArgumentException("date cannot be missing");
            }

            validateRequestCertificate(request);

            validateCanAccessEdOrgExtract(edOrgId);

            return getEdOrgExtractResponse(context.getRequest(), edOrgId, date);

        }
        logSecurityEvent("Failed request for Edorg delta bulk extract data");
        return Response.status(404).build();
    }

    /**
     * Stream a delta public extract response.
     *
     * @param context - HTTP context of request
     * @param request - HTTP servlet request for public bulk extract file
     * @param date the date of the delta
     *
     * @return A response with a delta extract file.
     */
    @GET
    @Path("extract/public/delta/{date}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getPublicDelta(@Context HttpServletRequest request, @Context HttpContext context,
                              @PathParam("date") String date) {
        logSecurityEvent("Received request to stream public delta bulk extract data");
        if (deltasEnabled) {
            LOG.info("Retrieving delta public bulk extract at date {}", date);

            if (date == null || date.isEmpty()) {
                logSecurityEvent("Failed delta request, missing date");
                throw new IllegalArgumentException("date cannot be missing");
            }

            validateRequestCertificate(request);

            return getPublicExtractResponse(context.getRequest(), date);

        }
        logSecurityEvent("Failed request for Edorg delta bulk extract data");
        return Response.status(404).build();
    }

    /**
     * Validate if the user can access an Ed Org extract
     *
     * @param edOrgId the edOrg id
     */
    private void validateCanAccessEdOrgExtract(String edOrgId) {
            if (edorgValidator.validate(EntityNames.EDUCATION_ORGANIZATION, new HashSet<String>(Arrays.asList(edOrgId))).isEmpty()) {
                throw new APIAccessDeniedException("User is not authorized to access this extract", EntityNames.EDUCATION_ORGANIZATION, edOrgId);
            }
        appAuthHelper.checkApplicationAuthorization(edOrgId);
    }

    /**
     * Get the bulk extract response
     *
     * @param req          the http request context
     * @param deltaDate    the date of the delta, or null to get the full extract
     * @param edOrgId      the Ed Org id (if private extract)
     * @param isPublicData indicates if the extract is for public data
     * @return the jax-rs response to send back.
     */
    Response getEdOrgExtractResponse(final HttpRequestContext req, final String edOrgId, final String deltaDate) {
        ExtractFile ef = getEdOrgExtractFile(edOrgId, deltaDate);

        if (ef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return fileResource.getFileResponse(req, ef, ef.getLastModified());
    }

    /**
     * Get the bulk extract response
     *
     * @param req          the http request context
     * @param deltaDate    the date of the delta, or null to get the full extract
     * @param edOrgId      the Ed Org id (if private extract)
     * @param isPublicData indicates if the extract is for public data
     * @return the jax-rs response to send back.
     */
    Response getPublicExtractResponse(final HttpRequestContext req, final String deltaDate) {
        ExtractFile ef = getPublicExtractFile(deltaDate);

        if (ef == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return fileResource.getFileResponse(req, ef, ef.getLastModified());
    }

    private ExtractFile getEdOrgExtractFile(final String edOrgId, final String deltaDate) {
        ExtractFile ef = null;

        Entity efEntity = getEdOrgExtractFileEntity(appAuthHelper.getApplicationId(), edOrgId, deltaDate);

        if (efEntity == null) {
            logSecurityEvent("No bulk extract support for : " + edOrgId);
            LOG.info("No bulk extract support for : {}", edOrgId);
        } else {
            ef = getExtractFile(efEntity);

            if (ef == null) {
                logSecurityEvent("No bulk extract file found for : " + edOrgId);
                LOG.info("No bulk extract file found for : {}", edOrgId);
            }
        }

        return ef;
    }

    private ExtractFile getPublicExtractFile(final String deltaDate) {
        ExtractFile ef = null;

        Entity efEntity = getPublicExtractFileEntity(appAuthHelper.getApplicationId(), deltaDate);

        if (efEntity == null) {
            String tenant = getPrincipal().getTenantId();

            logSecurityEvent("No public bulk extract support for : " + tenant);
            LOG.info("No public bulk extract support for : {}", tenant);
        } else {
            ef = getExtractFile(efEntity);

            if (ef == null) {
                String tenant = getPrincipal().getTenantId();

                logSecurityEvent("No public bulk extract file found for : " + tenant);
                LOG.info("No public bulk extract file found for : {}", tenant);
            }
        }

        return ef;
    }

    private ExtractFile getExtractFile(Entity efEntity) {
        ExtractFile bulkExtractFileEntity = new ExtractFile((String) efEntity.getBody().get(BULK_EXTRACT_FILE_PATH), (Date) efEntity.getBody().get(BULK_EXTRACT_DATE));

        if (bulkExtractFileEntity.exists()) {
            return bulkExtractFileEntity;
        }

        return null;
    }

    /**
     * Get the edOrg and public list response.
     *
     * @param context - the http request context
     * @return - the jax-rs response to send back
     */
    Response getPublicAndEdOrgListResponse(final HttpContext context) {

        List<String> userEdOrgs = retrieveUserAssociatedEdOrgs();

        String appId = appAuthHelper.getApplicationId();

        List<String> appAuthorizedUserEdOrgs = getApplicationAuthorizedUserEdOrgs(userEdOrgs, appId);
        if (appAuthorizedUserEdOrgs.size() == 0) {
            logSecurityEvent("No authorized EdOrgs for application:" + appId);
            LOG.info("No authorized EdOrgs for application: {}", appId);
            return Response.status(Status.NOT_FOUND).build();
        }

        List<String> authorizedUserSEdOrgs = new LinkedList<String>();
        authorizedUserSEdOrgs.addAll(appAuthorizedUserEdOrgs);

        logSecurityEvent("Successfully retrieved edOrgs and public list for " + appId);
        return assembleLinksResponse(context, appId, authorizedUserSEdOrgs);
    }

    /**
     * Assemble the edOrgs and public HATEOAS links response.
     *
     * @param context
     *        Original HTTP Request Context.
     * @param appId
     *        Authorized application ID.
     * @param authorizedUserEdOrgs
     *        List of edOrgs authorized to use and authorizing the specified application.
     *
     * @return the jax-rs response to send back.
     */
    private Response assembleLinksResponse(final HttpContext context, final String appId, final List<String> authorizedUserEdOrgs) {
        EntityBody list = assembleLinks(context, appId, authorizedUserEdOrgs);

        ResponseBuilder builder = Response.ok(list);
        builder.header("content-type", MediaType.APPLICATION_JSON + "; charset=utf-8");

        return builder.build();
    }

    /**
     * Assemble the edOrg and public HATEOAS links entity body.
     *
     * @param context
     *        Original HTTP Request Context.
     * @param appId
     *        Authorized application ID.
     * @param authorizedUserEdOrgs
     *        List of edOrgs authorized to use and authorizing the specified application.
     *
     * @return the jax-rs response to send back.
     */
    private EntityBody assembleLinks(final HttpContext context, final String appId, final List<String> authorizedUserEdOrgs) {
        EntityBody list = new EntityBody();

        UriInfo uriInfo = context.getUriInfo();
        String linkBase = ResourceUtil.getURI(uriInfo, ResourceUtil.getApiVersion(uriInfo)).toString() + "/bulk/extract/";
        String publicDataLinkBase = linkBase + "public";
        Map<String, Map<String, String>> edOrgFullLinks = new HashMap<String, Map<String, String>>();
        Map<String, Set<Map<String, String>>> edOrgDeltaLinks = new HashMap<String, Set<Map<String, String>>>();
        Map<String, Map<String, String>> publicFullLinks = new HashMap<String, Map<String, String>>();
        Map<String, Set<Map<String, String>>> publicDeltaLinks = new HashMap<String, Set<Map<String, String>>>();

        for (String edOrgId : authorizedUserEdOrgs) {
            Map<String, String> fullLink = new HashMap<String, String>();
            Set<Map<String, String>> deltaLinks = newDeltaLinkSet();
            Iterable<Entity> edOrgFileEntities = getEdOrgBulkExtractEntities(appId, edOrgId, false);

            if (edOrgFileEntities.iterator().hasNext()) {
                addLinks(linkBase + edOrgId, edOrgFileEntities, fullLink, deltaLinks);
                if (!fullLink.isEmpty()) {
                    edOrgFullLinks.put(edOrgId, fullLink);
                }
                if (!deltaLinks.isEmpty()) {
                    edOrgDeltaLinks.put(edOrgId, deltaLinks);
                }
            }
        }

        list.put("fullEdOrgs", edOrgFullLinks);
        list.put("deltaEdOrgs", edOrgDeltaLinks);

        Iterable<Entity> publicFileEntities = getEdOrgBulkExtractEntities(appId, null, true);
        Map<String, String> fullLink = new HashMap<String, String>();
        Set<Map<String, String>> deltaLinks = newDeltaLinkSet();
        if(publicFileEntities.iterator().hasNext()) {
            addLinks(publicDataLinkBase, publicFileEntities, fullLink, deltaLinks);
            if (!fullLink.isEmpty()) {
                publicFullLinks.put(getPrincipal().getTenantId(), fullLink);
            }
            if (!deltaLinks.isEmpty()) {
                publicDeltaLinks.put(getPrincipal().getTenantId(), deltaLinks);
            }
        }
        list.put("fullPublic", publicFullLinks);
        list.put("deltaPublic", publicDeltaLinks);
        return list;
    }

    /**
     * Create the delta links list for an edOrg or public extract.
     *
     * return - Empty set to hold delta links for an edOrg or public extract, sorted in reverse chronological order.
     */
    private Set<Map<String, String>> newDeltaLinkSet() {
        return new TreeSet<Map<String, String>>(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> link1, Map<String, String> link2) {
                return DATE_TIME_FORMATTER.parseDateTime(link2.get("timestamp"))
                    .compareTo(DATE_TIME_FORMATTER.parseDateTime(link1.get("timestamp")));
            }
        });
    }

    /**
     * Create the full and delta links for each specified edOrg or public extract.
     *
     * @param linkBase - Base name of edOrg or public links.
     * @param bulkExtractFileEntities - All eligible bulk extract file entities.
     * @param fullLink - EdOrg or public full link.
     * @param deltaLinks - Set of edOrg or public delta links.
     */
    private void addLinks(final String linkBase, final Iterable<Entity> bulkExtractFileEntities,
            final Map<String, String> fullLink, Set<Map<String, String>> deltaLinks) {
        for (Entity bulkExtractFileEntity : bulkExtractFileEntities) {
            Map<String, String> deltaLink = new HashMap<String, String>();
            String timeStamp = getTimestamp(bulkExtractFileEntity);
            if (Boolean.TRUE.equals(bulkExtractFileEntity.getBody().get("isDelta"))) {
                deltaLink.put("uri", linkBase + "/delta/" + timeStamp);
                deltaLink.put("timestamp", timeStamp);
                deltaLinks.add(deltaLink);
            } else {  // Assume only one full extract per edOrg.
                fullLink.put("uri", linkBase);
                fullLink.put("timestamp", timeStamp);
            }
        }
    }

    /**
     * Get timestamp string from bulk extract file entity.
     *
     * @param bulkExtractFileEntity - Bulk extract entity.
     *
     * @return - Bulk extract timestamp in ISO8601 format.
     */
    private String getTimestamp(final Entity bulkExtractFileEntity) {
        Date date = (Date)bulkExtractFileEntity.getBody().get("date");
        return DATE_TIME_FORMATTER.print(new DateTime(date));
    }

    /**
     * Get all the bulk extract entities for a particular App and EdOrg.
     *
     * @param appId -  The application id
     * @param edOrgId - The EdOrg id.
     *
     * @return - All bulk extract entities for the App and EdOrg
     */
    private Iterable<Entity> getEdOrgBulkExtractEntities(String appId, String edOrgId, boolean isPublicData) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                getPrincipal().getTenantId()));

        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));
        query.addCriteria(new NeutralCriteria("isPublicData", NeutralCriteria.OPERATOR_EQUAL, isPublicData));

        if(!isPublicData) {
            query.addCriteria(new NeutralCriteria("edorg", NeutralCriteria.OPERATOR_EQUAL, edOrgId));
        }

        query.setSortBy("date");
        query.setSortOrder(SortOrder.ascending);
        LOG.debug("Bulk Extract query is {}", query);
        Iterable<Entity> entities = mongoEntityRepository.findAll(BULK_EXTRACT_FILES, query);
        if (!entities.iterator().hasNext()) {
            LOG.debug("Could not find any bulk extract entities");
        }
        return entities;
    }

    /**
     * Get the bulk extract file
     *
     * @param appId
     * @param edOrgId
     * @param deltaDate the date of the delta, or null to retrieve a full extract
     * @return
     */
    private Entity getEdOrgExtractFileEntity(String appId, String edOrgId, String deltaDate) {
        boolean isDelta = deltaDate != null;
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                getPrincipal().getTenantId()));
        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));
        query.addCriteria(new NeutralCriteria("edorg", NeutralCriteria.OPERATOR_EQUAL, edOrgId));
        query.addCriteria(new NeutralCriteria("isPublicData", NeutralCriteria.OPERATOR_EQUAL, false));
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, isDelta));

        if (isDelta) {
            DateTime d = DATE_TIME_FORMATTER.parseDateTime(deltaDate);
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.OPERATOR_EQUAL, d.toDate()));
        }

        LOG.debug("Bulk Extract query is {}", query);
        Entity entity = mongoEntityRepository.findOne(BULK_EXTRACT_FILES, query);
        if (entity == null) {
            LOG.debug("Could not find a bulk extract entity");
        }
        return entity;
    }

    /**
     * Get the bulk extract file
     *
     * @param appId
     * @param deltaDate the date of the delta, or null to retrieve a full extract
     * @return
     */
    private Entity getPublicExtractFileEntity(String appId, String deltaDate) {
        boolean isDelta = deltaDate != null;
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                getPrincipal().getTenantId()));

        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));
        query.addCriteria(new NeutralCriteria("isPublicData", NeutralCriteria.OPERATOR_EQUAL, true));
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, isDelta));

        if (isDelta) {
            DateTime d = DATE_TIME_FORMATTER.parseDateTime(deltaDate);
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.OPERATOR_EQUAL, d.toDate()));
        }

        LOG.debug("Bulk Extract query is {}", query);
        Entity entity = mongoEntityRepository.findOne(BULK_EXTRACT_FILES, query);
        if (entity == null) {
            LOG.debug("Could not find a bulk extract entity");
        }
        return entity;
    }

    /**
     * @throws AccessDeniedException if the application is not BEEP enabled
     */
    private void validateRequestAndApplicationAuthorization(HttpServletRequest request) throws AccessDeniedException {
        validateRequestCertificate(request);
        appAuthHelper.checkApplicationAuthorization(null);
    }

    private List<String> retrieveUserAssociatedEdOrgs() throws AccessDeniedException {

        List<String> userEdOrgs = helper.getUserEdorgs(getPrincipal().getEntity());
        if (userEdOrgs.size() == 0) {
            throw new APIAccessDeniedException("User is not authorized for a list of available EdOrgs extracts", userEdOrgs);
        }
        return userEdOrgs;
    }


    private List<String> getApplicationAuthorizedUserEdOrgs(List<String> userEdOrgs, String appId) {
        List<String> appAuthorizedEdorgIds = appAuthHelper.getApplicationAuthorizationEdorgIds(appId);
        appAuthorizedEdorgIds.retainAll(userEdOrgs);
        return appAuthorizedEdorgIds;
    }

    /**
     * Getter for our mongo entity repository.
     *
     * @return the mongoEntityRepository.
     */
    public Repository<Entity> getMongoEntityRepository() {
        return mongoEntityRepository;
    }

    /**
     * Setter for our mongo entity repository.
     *
     * @param mongoEntityRepository the mongoEntityRepository to set.
     */
    public void setMongoEntityRepository(Repository<Entity> mongoEntityRepository) {
        this.mongoEntityRepository = mongoEntityRepository;
    }

    /**
     * Information about the file to extract
     *
     * @author nbrown
     *
     */
    private class ExtractFile extends File {
        private static final long serialVersionUID = 1L;

        private final Date lastModified;

        public ExtractFile(String fileName, Date lastModified) {
            super(fileName);
            this.lastModified = lastModified;
            LOG.debug("The file is {} and lastModified is {}", fileName, lastModified);
            LOG.debug("Length of bulk extract file is {}", length());
        }

        public Date getLastModified() {
            return lastModified;
        }
    }

    /**
     * Setter for our edorg validator.
     *
     * @param validator - EdOrg validator
     */
    public void setEdorgValidator(GenericToEdOrgValidator validator) {
        this.edorgValidator = validator;
    }

    private void validateRequestCertificate(HttpServletRequest request) {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        String clientId = auth.getClientAuthentication().getClientId();

        if (null == certs || certs.length < 1) {
            logSecurityEvent("App must provide client side X509 Certificate");
            throw new IllegalArgumentException("App must provide client side X509 Certificate");
        }

        try {
            this.validator.validateCertificate(certs[0], clientId);
        } catch (AccessDeniedException e) {
            throw new APIAccessDeniedException("Invalid certificate", e);
        }
    }

    /**
     * Setter for our file resource.
     *
     * @param fileResource - File resource
     */
    public void setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
    }


    void logSecurityEvent(String message) {
        auditLogger.audit(securityEventBuilder.createSecurityEvent(BulkExtract.class.getName(),
                uri.getRequestUri(), message, true));
    }

}
