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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

import org.joda.time.DateTime;
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
@Produces({ "application/x-tar"})
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);

    private static final String SAMPLED_FILE_NAME = "sample-extract.tar";

    public static final String BULK_EXTRACT_FILES = "bulkExtractFiles";
    public static final String BULK_EXTRACT_FILE_PATH = "path";
    public static final String BULK_EXTRACT_DATE = "date";

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    @Value("${sli.bulk.extract.deltasEnabled:false}")
    private boolean deltasEnabled;

    @Autowired
    private GenericToEdOrgValidator edorgValidator;

    @Autowired
    private EdOrgHelper helper;

    @Autowired
    private AppAuthHelper appAuthHelper;

    @Autowired
    private CertificateValidationHelper validator;

    private SLIPrincipal principal;

    private void initializePrincipal() {
        this.principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        return builder.build();
    }

    /**
     * Send an LEA extract
     *
     * @param lea
     *            The uuid of the lea to get the extract
     * @return
     *         A response with a lea tar file
     * @throws Exception
     *             On Error
     */
    @GET
    @Path("extract/{leaId}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getLEAExtract(@Context HttpContext context, @Context HttpServletRequest request, @PathParam("leaId") String leaId) throws Exception {

		validateRequestCertificate(request);
        if (!edorgValidator.validate(EntityNames.EDUCATION_ORGANIZATION, new HashSet<String>(Arrays.asList(leaId)))) {
            throw new AccessDeniedException("User is not authorized access this extract");
        }

        appAuthHelper.checkApplicationAuthorization(leaId);
        return getExtractResponse(context.getRequest(), null, leaId, false);
    }

    /**
     * Send the list of BE file links for all LEAs for which the calling user and application have access.
     *
     * @return A response with the complete list of BE file links for all LEAs for this user/app.
     *
     * @throws Exception On Error.
     */
    @GET
    @Path("extract/list")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getLEAList(@Context HttpServletRequest request, @Context HttpContext context) throws Exception {
        info("Received request for list of links for all LEAs for this user/app");
        validateRequestAndApplicationAuthorization(request);

        return getLEAListResponse(context);
    }

    /**
     * Creates a streaming response for the tenant data file.
     *
     * @return A response with the actual extract file
     * @throws Exception On Error
     */
    @GET
    @Path("extract/tenant")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getTenant(@Context HttpServletRequest request, @Context HttpContext context) throws Exception {
        info("Received request to stream tenant bulk extract...");
        validateRequestAndApplicationAuthorization(request);

        return getExtractResponse(context.getRequest(), null, null, false);
    }

    /**
     * Stream a delta response.
     *
     * @param date the date of the delta
     * @return A response with a delta extract file.
     * @throws Exception On Error
     */
    @GET
    @Path("extract/{leadId}/delta/{date}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getDelta(@Context HttpServletRequest request, @Context HttpContext context,
            @PathParam("leaId") String leaId, @PathParam("date") String date) throws Exception {
        if (deltasEnabled) {
            LOG.info("Retrieving delta bulk extract");
            validateRequestAndApplicationAuthorization(request);
            return getExtractResponse(context.getRequest(), date, leaId, false);
        }
        return Response.status(404).build();
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
            // return 404 if no bulk extract support for that tenant
            if (leaId != null) {
                LOG.info("No bulk extract support for lea: {}", leaId);
            } else {
                LOG.info("No bulk extract support for tenant: {}", principal.getTenantId());
            }
            return Response.status(Status.NOT_FOUND).build();
        }

        ExtractFile bulkExtractFileEntity =  new ExtractFile(entity.getBody().get(BULK_EXTRACT_DATE).toString(), entity.getBody().get(BULK_EXTRACT_FILE_PATH).toString());

        final File bulkExtractFile = bulkExtractFileEntity.getBulkExtractFile(bulkExtractFileEntity);
        if (bulkExtractFile == null || !bulkExtractFile.exists()) {
            // return 404 if the bulk extract file is missing
            if (leaId != null) {
                LOG.info("No bulk extract file found for lea: {}", leaId);
            } else {
                LOG.info("No bulk extract file found for tenant: {}", principal.getTenantId());
            }
            return Response.status(Status.NOT_FOUND).build();
        }

        return FileResource.getFileResponse(req, bulkExtractFile,
                bulkExtractFile.lastModified(), bulkExtractFileEntity.getLastModified());

    }


    /**
     * Get the LEA list response
     *
     * @param req  the http request context
     * @return the jax-rs response to send back.
     */
    Response getLEAListResponse(final HttpContext context) {

        List<String> userDistrics = retrieveUserAssociatedSLEAs();

        String appId = appAuthHelper.getApplicationId();

        List<String> appAuthorizedUserLEAs = getApplicationAuthorizedUserSLEAs(userDistrics, appId);
        if (appAuthorizedUserLEAs.size() == 0) {
            LOG.info("No authorized LEAs for application: {}", appId);
            return Response.status(Status.NOT_FOUND).build();
        }

        return assembleLEALinksResponse(context, appId, appAuthorizedUserLEAs);
    }

    /**
     * Assemble the LEA HATEOAS links response.
     *
     * @param req
     *        Original HTTP Request Context.
     * @param appId
     *        Authorized application ID.
     * @param appAuthorizedUserLEAs
     *        List of LEAs authorized to use and authorizing the specified application.
     *
     * @return the jax-rs response to send back.
     */
    Response assembleLEALinksResponse(final HttpContext context, String appId, List<String> appAuthorizedUserLEAs) {
        UriInfo uriInfo = context.getUriInfo();
        String linkBase = ResourceUtil.getURI(uriInfo).toString() + "/bulk/extract/";

        Map<String, Map<String, String>> leaFullLinks = new HashMap<String, Map<String, String>>();
        Map<String, List<Map<String, String>>> leaDeltaLinks = new HashMap<String, List<Map<String, String>>>();
        for (String leaId : appAuthorizedUserLEAs) {
            Iterable<Entity> leaFileEntities = getLEABulkExtractEntities(appId, leaId);
            if (leaFileEntities.iterator().hasNext()) {
                Map<String, String> fullLink = new HashMap<String, String>();
                List<Map<String, String>> deltaLinks = new ArrayList<Map<String, String>>();
                for (Entity leaFileEntity : leaFileEntities) {
                    Map<String, String> deltaLink = new HashMap<String, String>();
                    String timeStamp = leaFileEntity.getBody().get("date").toString();
                    if (Boolean.parseBoolean(leaFileEntity.getBody().get("isDelta").toString())) {
                        deltaLink.put("uri", linkBase + leaId + "/delta/" + timeStamp);
                        deltaLink.put("timestamp", timeStamp);
                        deltaLinks.add(deltaLink);
                    } else {  // Assume only one full extract per LEA.
                        fullLink.put("uri", linkBase + leaId);
                        fullLink.put("timestamp", leaFileEntity.getBody().get("date").toString());
                    }
                }
                leaFullLinks.put(leaId, fullLink);
                leaDeltaLinks.put(leaId, deltaLinks);
            }
        }

        if (leaFullLinks.isEmpty() && leaDeltaLinks.isEmpty()) {
            LOG.info("No LEA bulk extract files exist for application: {}", appId);
            return Response.status(Status.NOT_FOUND).build();
        }

        EntityBody list = new EntityBody();
        list.put("fullLeas", leaFullLinks);
        list.put("deltaLeas", leaDeltaLinks);
        ResponseBuilder builder = Response.ok(list);
        return builder.build();
    }

    /**
     * Get the bulk extract file
     *
     * @param deltaDate the date of the delta, or null to retrieve a full extract
     * @param appId
     * @return
     */
    private Iterable<Entity> getLEABulkExtractEntities(String appId, String leaId) {
        initializePrincipal();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                principal.getTenantId()));
        if (leaId != null && !leaId.isEmpty()) {
            query.addCriteria(new NeutralCriteria("edorg",
                    NeutralCriteria.OPERATOR_EQUAL, leaId));
        }
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
        initializePrincipal();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                principal.getTenantId()));
        if (leaId != null && !leaId.isEmpty()) {
            query.addCriteria(new NeutralCriteria("edorg",
                    NeutralCriteria.OPERATOR_EQUAL, leaId));
        }
        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));
        if (!ignoreIsDelta) {
            query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, Boolean.toString(isDelta)));
        }

        if (isDelta) {
            DateTime d = ISODateTimeFormat.dateTime().parseDateTime(deltaDate);
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

    private List<String> retrieveUserAssociatedSLEAs() throws AccessDeniedException {
        List<String> userDistrics = helper.getDistricts(principal.getEntity());
        if (userDistrics.size() == 0) {
            throw new AccessDeniedException("User is not authorized for a list of available LEAs extracts");
        }
        return userDistrics;
    }

    private List<String> getApplicationAuthorizedUserSLEAs(List<String> userDistrics, String appId) {
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
            throw new IllegalArgumentException("App must provide client side X509 Certificate");
        }

        this.validator.validateCertificate(certs[0], clientId);
    }
}
