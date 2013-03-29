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

package org.slc.sli.api.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 *
 * @author dkornishev
 *
 */
@Component
@Path("/bulk")
@Produces({ "application/x-tar" })
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);

    private static final String SAMPLED_FILE_NAME = "NY-WALTON-2013-03-19T13-02-02.tar";

    public static final String BULK_EXTRACT_FILES = "bulkExtractFiles";
    public static final String BULK_EXTRACT_FILE_PATH = "path";
    public static final String BULK_EXTRACT_DATE = "date";

    @Autowired
    private Repository<Entity> mongoEntityRepository;

    private SLIPrincipal principal;

    private void initializePrincipal() {
        this.principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Creates a streaming response for a sample data file
     * @return
     * @throws FileNotFoundException
     */
    @GET
    @Path("extract")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response get() throws FileNotFoundException {
        LOG.info("Received request to stream bulk extract...");

        Entity bulkExtractFileEntity = bulkExtractFileEntity();
        if (bulkExtractFileEntity == null) {
            // return 404 if no bulk extract support for that tenant
            LOG.info("No bulk extract support for tenant: {}", principal.getTenantId());
            return Response.status(Status.NOT_FOUND).build();
        }

        final File bulkExtractFile = getbulkExtractFile(bulkExtractFileEntity);
        if (bulkExtractFile==null || !bulkExtractFile.exists()) {
            // return 204 if the bulk extract file is missing
            LOG.info("No bulk extract file found for tenant: {}", principal.getTenantId());
            return Response.status(Status.NO_CONTENT).build();
        }

        String fileName = bulkExtractFile.getName();
        String lastModified = ((Date) bulkExtractFileEntity.getBody().get(BULK_EXTRACT_DATE)).toString();

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                int n;
                byte[] buffer = new byte[1024];
                InputStream is = new FileInputStream(bulkExtractFile);
                while ((n = is.read(buffer)) > -1) {
                    output.write(buffer, 0, n);
                }
            }
        };

        ResponseBuilder builder = Response.ok(out);
        builder.header("content-disposition", "attachment; filename = " + fileName);
        builder.header("last-modified", lastModified);
        LOG.info("Requested stream bulk extract file: {} for tenant: {}", bulkExtractFile, principal.getTenantId());
        return builder.build();
    }

    private File getbulkExtractFile(Entity bulkExtractFileEntity) {
        String fileName = (String) bulkExtractFileEntity.getBody().get(BULK_EXTRACT_FILE_PATH);
        File bulkExtractFile = new File(fileName);
        return bulkExtractFile;
    }

    private Entity bulkExtractFileEntity() {
        initializePrincipal();
        NeutralQuery tenantQuery = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, principal.getTenantId()));
        return mongoEntityRepository.findOne(BULK_EXTRACT_FILES, tenantQuery);
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

}
