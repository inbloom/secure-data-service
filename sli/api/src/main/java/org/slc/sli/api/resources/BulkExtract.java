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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.domain.enums.Right;
import org.springframework.stereotype.Component;

/**
 * 
 * @author dkornishev
 * 
 */
@Component
@Path("/bulk")
@Produces({ "application/x-tar" })
public class BulkExtract {

    private static final Logger LOG = Logger.getLogger(BulkExtract.class);

    private static final String FILE_NAME = "sample-extract.tar";

    /**
     * Creates a streaming response for a sample data file
     * @return
     */
    @GET
    @Path("extract")
    @RightsAllowed({ Right.BULK_EXTRACT })
    public Response get() {
        LOG.info("Received request to stream bulk extract...");
        final InputStream is = this.getClass().getResourceAsStream("/bulkExtractSampleData/" + FILE_NAME);
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
}
