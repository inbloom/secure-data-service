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
import org.slc.sli.api.security.context.APIAccessDeniedException;
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
import org.slc.sli.api.security.context.resolver.AppAuthHelper;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.validator.GenericToEdOrgValidator;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.encrypt.security.CertificateValidationHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
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
    private FileResource fileResource;

    private SLIPrincipal getPrincipal() {
        return (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Creates a streaming response for the sample data file.
     *
     * @return A response with the sample extract file
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
     * Send an LEA extract or a SEA public data extract
     *
     * @param edOrgId
     *            The uuid of the lea/sea to get the extract
     * @return
     *         A response with a lea/sea tar file
     * @throws Exception
     *             On Error
     */
    @GET
    @Path("extract/{edOrgId}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getEdOrgExtract(@Context HttpContext context, @Context HttpServletRequest request, @PathParam("edOrgId") String edOrgId) {

        logSecurityEvent("Received request to stream Edorg data");
        if (edOrgId == null || edOrgId.isEmpty()) {
            logSecurityEvent("Failed request to stream SEA public data, missing edOrgId");
            throw new IllegalArgumentException("edOrgId cannot be missing");
        }
        validateRequestCertificate(request);

        boolean isPublicData = false;
        Entity entity = helper.byId(edOrgId);

        if (helper.isSEA(entity)) {
            isPublicData = true;
            canAccessSEAExtract(entity);
        } else {
        	canAccessEdOrgExtract(edOrgId);
        }

        return getExtractResponse(context.getRequest(), null, edOrgId, isPublicData);
    }

    /**
     * Send the list of BE file links for all LEAs for which the calling user and application have access.
     *
     * @return A response with the complete list of BE file links for all LEAs/SEAs for this user/app.
     *
     * @throws Exception On Error.
     */
    @GET
    @Path("extract/list")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getSEAOrLEAList(@Context HttpServletRequest request, @Context HttpContext context) throws Exception {
        info("Received request for list of links for all SEAs and LEAs for this user/app");
        logSecurityEvent("Received request for list of links for all SEAs and LEAs for this user/app");
        validateRequestAndApplicationAuthorization(request);

        logSecurityEvent("Successful request for list of links for all SEAs and LEAs for this user/app");
        return getSLEAListResponse(context);
    }


    /**
     * Stream a delta response.
     *
     * @param date the date of the delta
     * @param edOrgId the uuid of the lea/sea to get delta extract for
     * @return A response with a delta extract file.
     * @throws Exception On Error
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
                logSecurityEvent("Failed delta request, missing leadId");
                throw new IllegalArgumentException("leaId cannot be missing");
            }
            if (date == null || date.isEmpty()) {
                logSecurityEvent("Failed delta request, missing date");
                throw new IllegalArgumentException("date cannot be missing");
            }

            validateRequestCertificate(request);

            boolean isPublicData = false;
            Entity entity = helper.byId(edOrgId);

            if (helper.isSEA(entity)) {
                isPublicData = true;
                canAccessSEAExtract(entity);
            } else {
            	canAccessEdOrgExtract(edOrgId);
            }

            return getExtractResponse(context.getRequest(), date, edOrgId, isPublicData);

        }
        logSecurityEvent("Failed request for Edorg delta bulk extract data");
        return Response.status(404).build();
    }

    /**
     * Validate if the user can access SEA extract
     *
     * @param seaEntity the SEA Entity
     */
    void canAccessSEAExtract(final Entity seaEntity) {

        boolean approvedLEAExists = false;
        for (String leaId : helper.getDirectChildLEAsOfEdOrg(seaEntity)) {
            LOG.debug("Checking lea: {} for sea: {}", leaId, seaEntity.getEntityId());
                try {
                    canAccessLEAExtract(leaId);
                    approvedLEAExists = true;
                    break;
                } catch (AccessDeniedException e) {
                    approvedLEAExists = false;
                }
        }
        if (!approvedLEAExists) {
            throw new APIAccessDeniedException("User is not authorized to access SEA public extract", EntityNames.EDUCATION_ORGANIZATION, seaEntity);
        }
    }

    /**
     * Validate if the user can access LEA extract
     *
     * @param leaId the LEA id
     */
    void canAccessLEAExtract(String leaId) {
            if (edorgValidator.validate(EntityNames.EDUCATION_ORGANIZATION, new HashSet<String>(Arrays.asList(leaId))).isEmpty()) {
                throw new APIAccessDeniedException("User is not authorized to access this extract", EntityNames.EDUCATION_ORGANIZATION, leaId);
            }
        appAuthHelper.checkApplicationAuthorization(leaId);
    }

    /**
     * Validate if the user can access an Ed Org extract
     *
     * @param leaId the LEA id
     */
    void canAccessEdOrgExtract(String edOrgId) {
            if (!edorgValidator.validate(EntityNames.EDUCATION_ORGANIZATION, new HashSet<String>(Arrays.asList(edOrgId)))) {
                throw new APIAccessDeniedException("User is not authorized to access this extract", EntityNames.EDUCATION_ORGANIZATION, edOrgId);
            }
        appAuthHelper.checkApplicationAuthorization(edOrgId);
    }
    
    /**
     * Get the bulk extract response
     *
     * @param req       the http request context
     * @param deltaDate the date of the delta, or null to get the full extract
     * @param leaId     the LEA id
     * @param isPublicData indicates if the extract is for public data
     * @return the jax-rs response to send back.
     */
    Response getExtractResponse(final HttpRequestContext req, final String deltaDate, final String leaId, boolean isPublicData) {

        String appId = appAuthHelper.getApplicationId();

        Entity entity = getBulkExtractFileEntity(deltaDate, appId, leaId, false, isPublicData);

        if (entity == null) {
            logSecurityEvent("No bulk extract support for : " + leaId);
            LOG.info("No bulk extract support for : {}", leaId);
            return Response.status(Status.NOT_FOUND).build();
        }

        ExtractFile bulkExtractFileEntity =  new ExtractFile(entity.getBody().get(BULK_EXTRACT_DATE).toString(), entity.getBody().get(BULK_EXTRACT_FILE_PATH).toString());

        final File bulkExtractFile = bulkExtractFileEntity.getBulkExtractFile(bulkExtractFileEntity);
        if (!bulkExtractFile.exists()) {
            logSecurityEvent("No bulk extract support for : " + leaId);
            LOG.info("No bulk extract file found for : {}", leaId);
            return Response.status(Status.NOT_FOUND).build();
        }

        return fileResource.getFileResponse(req, bulkExtractFile,
                bulkExtractFile.lastModified(), bulkExtractFileEntity.getLastModified(), uri);

    }

    /**
     * Get the SEA/LEA list response.
     *
     * @param context - the http request context
     * @return - the jax-rs response to send back
     */
    Response getSLEAListResponse(final HttpContext context) {

        List<String> userDistricts = retrieveUserAssociatedLEAs();

        String appId = appAuthHelper.getApplicationId();

        List<String> appAuthorizedUserLEAs = getApplicationAuthorizedUserLEAs(userDistricts, appId);
        if (appAuthorizedUserLEAs.size() == 0) {
            logSecurityEvent("No authorized LEAs for application:" + appId);
            LOG.info("No authorized LEAs for application: {}", appId);
            return Response.status(Status.NOT_FOUND).build();
        }

        List<String> authorizedUserSLEAs = new LinkedList<String>();
        authorizedUserSLEAs.addAll(appAuthorizedUserLEAs);
        Entity lea = helper.byId(appAuthorizedUserLEAs.get(0));  // First LEA is as good as any.
        String seaId = helper.getSEAOfEdOrg(lea);
        if (seaId != null) {
            authorizedUserSLEAs.add(seaId);
        }

        logSecurityEvent("Successfully retrieved SEA/LEA list for " + appId);
        return assembleSLEALinksResponse(context, appId, authorizedUserSLEAs);
    }

    /**
     * Assemble the SEA/LEA HATEOAS links response.
     *
     * @param context
     *        Original HTTP Request Context.
     * @param appId
     *        Authorized application ID.
     * @param authorizedUserSLEAs
     *        List of SEAs and LEAs authorized to use and authorizing the specified application.
     *
     * @return the jax-rs response to send back.
     */
    @SuppressWarnings("unchecked")
    private Response assembleSLEALinksResponse(final HttpContext context, final String appId, final List<String> authorizedUserSLEAs) {
        EntityBody list = assembleSLEALinks(context, appId, authorizedUserSLEAs);

        ResponseBuilder builder = Response.ok(list);
        builder.header("content-type", MediaType.APPLICATION_JSON + "; charset=utf-8");

        return builder.build();
    }

    /**
     * Assemble the SEA/LEA HATEOAS links entity body.
     *
     * @param context
     *        Original HTTP Request Context.
     * @param appId
     *        Authorized application ID.
     * @param authorizedUserSLEAs
     *        List of SEAs and LEAs authorized to use and authorizing the specified application.
     *
     * @return the jax-rs response to send back.
     */
    private EntityBody assembleSLEALinks(final HttpContext context, final String appId, final List<String> authorizedUserSLEAs) {
        EntityBody list = new EntityBody();

        UriInfo uriInfo = context.getUriInfo();
        String linkBase = ResourceUtil.getURI(uriInfo, ResourceUtil.getApiVersion(uriInfo)).toString() + "/bulk/extract/";
        Map<String, Map<String, String>> leaFullLinks = new HashMap<String, Map<String, String>>();
        Map<String, Set<Map<String, String>>> leaDeltaLinks = new HashMap<String, Set<Map<String, String>>>();
        Map<String, Map<String, String>> seaFullLinks = new HashMap<String, Map<String, String>>();
        Map<String, Set<Map<String, String>>> seaDeltaLinks = new HashMap<String, Set<Map<String, String>>>();

        for (String edOrgId : authorizedUserSLEAs) {
            Map<String, String> fullLink = new HashMap<String, String>();
            Set<Map<String, String>> deltaLinks = newDeltaLinkSet();
            Iterable<Entity> edOrgFileEntities = getEdOrgBulkExtractEntities(appId, edOrgId);
            if (edOrgFileEntities.iterator().hasNext()) {
                addLinks(linkBase + edOrgId, edOrgFileEntities, fullLink, deltaLinks);
                Entity entity = helper.byId(edOrgId);
                if (!fullLink.isEmpty()) {
                    if (helper.isSEA(entity)) {
                        seaFullLinks.put(edOrgId, fullLink);
                    } else {
                        leaFullLinks.put(edOrgId, fullLink);
                    }
                }
                if (!deltaLinks.isEmpty()) {
                    if (helper.isSEA(entity)) {
                        seaDeltaLinks.put(edOrgId, deltaLinks);
                    } else {
                        leaDeltaLinks.put(edOrgId, deltaLinks);
                    }
                }
            }
        }

        list.put("fullSea", seaFullLinks);
        list.put("deltaSea", seaDeltaLinks);
        list.put("fullLeas", leaFullLinks);
        list.put("deltaLeas", leaDeltaLinks);

        return list;
    }

    /**
     * Create the delta links list for an SEA/LEA.
     *
     * return - Empty set to hold delta links for an LEA, sorted in reverse chronological order.
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
     * Create the full and delta links for each specified SEA or LEA.
     *
     * @param linkBase - Base name of SEA or LEA links.
     * @param edOrgFileEntities - All eligible SEA or LEA entities.
     * @param fullLink - SEA or LEA full link.
     * @param deltaLinks - Set of SEA or LEA delta links.
     */
    private void addLinks(final String linkBase, final Iterable<Entity> edOrgFileEntities,
            final Map<String, String> fullLink, Set<Map<String, String>> deltaLinks) {
        for (Entity edOrgFileEntity : edOrgFileEntities) {
            Map<String, String> deltaLink = new HashMap<String, String>();
            String timeStamp = getTimestamp(edOrgFileEntity);
            if (Boolean.TRUE.equals(edOrgFileEntity.getBody().get("isDelta"))) {
                deltaLink.put("uri", linkBase + "/delta/" + timeStamp);
                deltaLink.put("timestamp", timeStamp);
                deltaLinks.add(deltaLink);
            } else {  // Assume only one full extract per SEA or LEA.
                fullLink.put("uri", linkBase);
                fullLink.put("timestamp", timeStamp);
            }
        }
    }

    /**
     * Get timestamp string from LEA entity.
     *
     * @param leaFileEntity - LEA entity.
     *
     * @return - LEA extract timestamp in ISO8601 format.
     */
    private String getTimestamp(final Entity leaFileEntity) {
        Date date = (Date)leaFileEntity.getBody().get("date");
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
    private Iterable<Entity> getEdOrgBulkExtractEntities(String appId, String edOrgId) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                getPrincipal().getTenantId()));
        query.addCriteria(new NeutralCriteria("edorg", NeutralCriteria.OPERATOR_EQUAL, edOrgId));
        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));
        debug("Bulk Extract query is {}", query);
        Iterable<Entity> entities = mongoEntityRepository.findAll(BULK_EXTRACT_FILES, query);
        if (!entities.iterator().hasNext()) {
            debug("Could not find any bulk extract entities");
        }
        return entities;
    }

    /**
     * Get the bulk extract file
     *
     * @param deltaDate the date of the delta, or null to retrieve a full extract
     * @param appId
     * @return
     */
    private Entity getBulkExtractFileEntity(String deltaDate, String appId, String leaId, boolean ignoreIsDelta, boolean isPublicData) {
        boolean isDelta = deltaDate != null;
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                getPrincipal().getTenantId()));
        if (leaId != null && !leaId.isEmpty()) {
            query.addCriteria(new NeutralCriteria("edorg",
                    NeutralCriteria.OPERATOR_EQUAL, leaId));
        }
        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));
        if (!ignoreIsDelta) {
            query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, isDelta));
        }

        if (isDelta) {
            DateTime d = DATE_TIME_FORMATTER.parseDateTime(deltaDate);
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.OPERATOR_EQUAL, d.toDate()));
        }

        query.addCriteria(new NeutralCriteria("isPublicData", NeutralCriteria.OPERATOR_EQUAL, isPublicData));
        debug("Bulk Extract query is {}", query);
        Entity entity = mongoEntityRepository.findOne(BULK_EXTRACT_FILES, query);
        if (entity == null) {
            debug("Could not find a bulk extract entity");
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

    private List<String> retrieveUserAssociatedLEAs() throws AccessDeniedException {
        List<String> userDistricts = helper.getDistricts(getPrincipal().getEntity());
        if (userDistricts.size() == 0) {
            throw new APIAccessDeniedException("User is not authorized for a list of available SEA/LEA extracts", userDistricts);
        }
        return userDistricts;
    }

    private List<String> getApplicationAuthorizedUserLEAs(List<String> userDistrics, String appId) {
        List<String> appAuthorizedEdorgIds = appAuthHelper.getApplicationAuthorizationEdorgIds(appId);
        appAuthorizedEdorgIds.retainAll(userDistrics);
        return appAuthorizedEdorgIds;
    }

    /**
     * @return the mongoEntityRepository
     */
    public Repository<Entity> getMongoEntityRepository() {
        return mongoEntityRepository;
    }

    /**
     * @param mongoEntityRepository the mongoEntityRepository to set
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
    private class ExtractFile {
        private final String lastModified;
        private final String fileName;

        public ExtractFile(String lastModified, String fileName) {
            super();
            this.lastModified = lastModified;
            this.fileName = fileName;
            debug("The file is " + fileName + " and lastModified is " + lastModified);
        }

        public String getLastModified() {
            return lastModified;
        }

        public File getBulkExtractFile(ExtractFile bulkExtractFileEntity) {
            File bulkExtractFile = new File(fileName);
            debug("Length of bulk extract file is " + bulkExtractFile.length());
            return bulkExtractFile;
        }

    }

    /**
     * Setter for our edorg validator
     *
     * @param validator
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

    public void setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
    }


    void logSecurityEvent(String message) {
        audit(securityEventBuilder.createSecurityEvent(BulkExtract.class.getName(),
                uri.getRequestUri(), message, true));
    }

}
