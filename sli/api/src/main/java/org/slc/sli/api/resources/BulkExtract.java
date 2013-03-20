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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
@Produces({ "application/zip" })
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);

    private static final String FILE_NAME = "NY-WALTON-2013-03-08.zip";

    public static final String BULK_EXTRACT_FILES = "bulkExtractFiles";
    public static final String BULK_EXTRACT_FILE_PATH = "path";

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

        File bulkExtractFile = null;
        Entity bulkExtractFileEntity = bulkExtractFileEntity();
        if (bulkExtractFileEntity != null) {
            bulkExtractFile = getbulkExtractFile(bulkExtractFileEntity);
            LOG.info("Requested stream bulk extract file: {}", bulkExtractFile);
        }

        final InputStream is = bulkExtractFile==null || !bulkExtractFile.exists() ?
                this.getClass().getResourceAsStream("/bulkExtractSampleData/" + FILE_NAME) :
                new FileInputStream(bulkExtractFile);

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

        return Response.ok(out).header("content-disposition", "attachment; filename = " + FILE_NAME).build();
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
}
