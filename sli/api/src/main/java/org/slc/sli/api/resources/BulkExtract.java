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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slc.sli.api.resources.security.ApplicationAuthorizationResource;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.validator.GenericToEdOrgValidator;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

/**
 * The Bulk Extract Endpoints.
 *
 * @author dkornishev
 *
 */
@Component
@Path("/bulk")
@Produces({ "application/x-tar" })
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);

    private static final String SAMPLED_FILE_NAME = "sample-extract.tar";

    public static final String BULK_EXTRACT_FILES = "bulkExtractFiles";
    public static final String BULK_EXTRACT_FILE_PATH = "path";
    public static final String BULK_EXTRACT_DATE = "date";

    @Autowired
    private Repository<Entity> mongoEntityRepository;
    
    @Autowired
    private GenericToEdOrgValidator edorgValidator;

    private SLIPrincipal principal;

    private void initializePrincipal() {
        this.principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Creates a streaming response for the sample data file.
     *
     * @return
     *          A response with the sample extract file
     * @throws Exception
     *          On Error
     */
    @GET
    @Path("extract")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response get(@Context HttpContext req) throws Exception {
        LOG.info("Received request to stream sample bulk extract...");

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
    public Response getLEAExtract(@Context HttpContext context, @PathParam("leaId") String leaId) throws Exception {
        LOG.info("Retrieving delta bulk extract");
        if (!edorgValidator.validate(EntityNames.EDUCATION_ORGANIZATION, new HashSet<String>(Arrays.asList(leaId)))) {
            throw new AccessDeniedException("User is not authorized access this extract");
        }
        checkApplicationAuthorization(leaId);
        return getExtractResponse(context.getRequest(), null, leaId);
    }

    /**
     * Creates a streaming response for the tenant data file.
     *
     * @return
     *          A response with the actual extract file
     * @throws Exception
     *          On Error
     */
    @GET
    @Path("extract/tenant")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getTenant(@Context HttpContext context) throws Exception {
        info("Received request to stream tenant bulk extract...");
        checkApplicationAuthorization(null);

        return getExtractResponse(context.getRequest(), null, null);
    }

    /**
     * Stream a delta response.
     *
     * @param date
     *            the date of the delta
     * @return
     *          A response with a delta extract file.
     * @throws Exception
     *          On Error
     */
    @GET
    @Path("deltas/{date}")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response getDelta(@Context HttpContext context, @PathParam("date") String date) throws Exception {
        LOG.info("Retrieving delta bulk extract");
        return getExtractResponse(context.getRequest(), date, null);
    }

    /**
     * Get the bulk extract response
     *
     * @param headers
     *          The http request headers
     * @param deltaDate
     *            the date of the delta, or null to get the full extract
     * @return the jax-rs response to send back.
     */
    Response getExtractResponse(final HttpRequestContext req, final String deltaDate, final String leaId) {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Entity application = getApplication(auth);

        String appId = application.getEntityId();

        ExtractFile bulkExtractFileEntity = getBulkExtractFile(deltaDate, appId, leaId);

        if (bulkExtractFileEntity == null) {
            // return 404 if no bulk extract support for that tenant
            if (leaId != null) {
                LOG.info("No bulk extract support for lea: {}", leaId);
            } else {
                LOG.info("No bulk extract support for tenant: {}", principal.getTenantId());
            }
            return Response.status(Status.NOT_FOUND).build();
        }

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

    private Entity getApplication(Authentication authentication) {
        if (!(authentication instanceof OAuth2Authentication)) {
            throw new AccessDeniedException("Not logged in with valid oauth context");
        }
        final OAuth2Authentication oauth = (OAuth2Authentication) authentication;

        final String clientId = oauth.getClientAuthentication().getClientId();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("client_id", NeutralCriteria.OPERATOR_EQUAL,
                clientId));
        final Entity entity = mongoEntityRepository.findOne(EntityNames.APPLICATION, query);

        if(entity == null) {
            throw new AccessDeniedException("Could not find application with client_id=" + clientId);
        } else if (entity.getBody().get("public_key") == null) {
          throw new AccessDeniedException("Missing public_key attribute on application entity. client_id=" + clientId);
      }
        return entity;
    }

    /**
     * Get the bulk extract file
     *
     * @param deltaDate
     *            the date of the delta, or null to retrieve a full extract
     * @param appId
     * @return
     */
    private ExtractFile getBulkExtractFile(String deltaDate, String appId, String leaId) {
        boolean isDelta = deltaDate != null;
        initializePrincipal();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                principal.getTenantId()));
        if (leaId != null && !leaId.isEmpty()) {
            query.addCriteria(new NeutralCriteria("edOrgId", NeutralCriteria.OPERATOR_EQUAL, leaId));
        }
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, Boolean.toString(isDelta)));
        query.addCriteria(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL, appId));

        if (isDelta) {
            DateTime d = ISODateTimeFormat.basicDate().parseDateTime(deltaDate);
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.CRITERIA_GTE, d.toDate()));
            query.addCriteria(new NeutralCriteria("date", NeutralCriteria.CRITERIA_LT, d.plusDays(1).toDate()));
        }
        debug("Bulk Extract query is {}", query);
        Entity entity = mongoEntityRepository.findOne(BULK_EXTRACT_FILES, query);
        if (entity == null) {
        	debug("Could not find a bulk extract entity");
            return null;
        }
        return new ExtractFile(entity.getBody().get(BULK_EXTRACT_DATE).toString(), entity.getBody()
                .get(BULK_EXTRACT_FILE_PATH).toString());
    }


    /**
     * @throws AccessDeniedException
     *             if the application is not BEEP enabled
     */
    private void checkApplicationAuthorization(String edorgsForExtract) throws AccessDeniedException {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Entity app = getApplication(auth);
        Map<String, Object> body = app.getBody();
        if (!body.containsKey("isBulkExtract") || (Boolean) body.get("isBulkExtract") == false) {
            throw new AccessDeniedException("Application is not approved for bulk extract");
        }
        if (edorgsForExtract != null) {
            NeutralQuery query = new NeutralQuery(new NeutralCriteria("applicationId", NeutralCriteria.OPERATOR_EQUAL,
                    app.getEntityId()));
            Entity appAuth = mongoEntityRepository.findOne(ApplicationAuthorizationResource.RESOURCE_NAME, query);
            if (appAuth == null || !((List) appAuth.getBody().get("edOrgs")).contains(edorgsForExtract)) {
                throw new AccessDeniedException("Application is not authorized for bulk extract");
            }
        }
    }

    /**
     * @return the mongoEntityRepository
     */
    public Repository<Entity> getMongoEntityRepository() {
        return mongoEntityRepository;
    }

    /**
     * @param mongoEntityRepository
     *            the mongoEntityRepository to set
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
            debug("The file is "  + fileName + " and lastModified is " + lastModified);
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

}
