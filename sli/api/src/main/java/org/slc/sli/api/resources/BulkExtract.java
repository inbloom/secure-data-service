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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.common.constants.EntityNames;
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
@Produces({ "application/x-tar" })
public class BulkExtract {

    private static final Logger LOG = LoggerFactory.getLogger(BulkExtract.class);
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
    private static final String MULTIPART_BOUNDRY_SEP = "--" + MULTIPART_BOUNDARY;
    private static final String MULTIPART_BOUNDRY_END = MULTIPART_BOUNDRY_SEP + "--";

    private static final String SAMPLED_FILE_NAME = "sample-extract.tar";

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
    public Response get(@Context HttpHeaders headers) throws Exception {
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
    public Response getTenant(@Context HttpHeaders headers) throws Exception {
        info("Received request to stream tenant bulk extract...");
        checkApplicationAuthorization(null);

        return getExtractResponse(headers, null);
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
    public Response getDelta(@Context HttpHeaders headers, @PathParam("date") String date) throws Exception {
        LOG.info("Retrieving delta bulk extract");
        return getExtractResponse(headers, date);
    }

    /**
     * Get the bulk extract response
     *
     * @param headers
     *          The http request headers
     * @param deltaDate
     *            the date of the delta, or null to get the full extract
     * @return the jax-rs response to send back.
     * @throws Exception
     */
    Response getExtractResponse(final HttpHeaders headers, final String deltaDate) throws Exception {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Entity application = getApplication(auth);

        String appId = application.getEntityId();

        ExtractFile bulkExtractFileEntity = getBulkExtractFile(deltaDate, appId);

        if (bulkExtractFileEntity == null) {
            // return 404 if no bulk extract support for that tenant
            LOG.info("No bulk extract support for tenant: {}", principal.getTenantId());
            return Response.status(Status.NOT_FOUND).build();
        }

        final File bulkExtractFile = bulkExtractFileEntity.getBulkExtractFile(bulkExtractFileEntity);
        if (bulkExtractFile == null || !bulkExtractFile.exists()) {
            // return 404 if the bulk extract file is missing
            LOG.info("No bulk extract file found for tenant: {}", principal.getTenantId());
            return Response.status(Status.NOT_FOUND).build();
        }

        return getExtractResponse(headers, bulkExtractFile, bulkExtractFile.lastModified(),
                bulkExtractFileEntity.getLastModified());

    }

    /**
     * Get the bulk extract response
     *
     * @param headers
     *          The http request headers
     * @param bulkExtractFile
     *          The bulk extract file to return
     * @param fileName
     *          The name for a bulk extract file
     * @param lastModified
     *          The last modified date time
     * @return
     *          Response with the bulk extract file
     * @throws ParseException
     */
    private Response getExtractResponse(final HttpHeaders headers,
            final File bulkExtractFile, final long lastModifiedTime, final String lastModified) {

        String fileName = bulkExtractFile.getName();
        long fileLength = bulkExtractFile.length();
        String eTag = fileName + "_" + fileLength + "_" + lastModified;

        /*
         * Validate request headers for caching
         */
        String ifNoneMatch = getRequestHeader(headers, "If-None-Match");
        if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
            // If-None-Match header should contain "*" or ETag. If so, then return 304.
            ResponseBuilder builder = Response.status(Status.NOT_MODIFIED)
                    .header("ETag", eTag);
            return builder.build();
        }

        // If-Modified-Since header is ignored if any If-None-Match header is specified.
        String ifModifiedSince = getRequestHeader(headers, "If-Modified-Since");
        long ifModifiedSinceTime = ifModifiedSince==null ? 0 :
            ISODateTimeFormat.basicDate().parseDateTime(ifModifiedSince).getMillis();
        if (ifNoneMatch == null && ifModifiedSinceTime > 0 && ifModifiedSinceTime + 1000 > lastModifiedTime) {
            // If-Modified-Since header should be greater than LastModified. If so, then return 304.
            ResponseBuilder builder = Response.status(Status.NOT_MODIFIED)
                    .header("ETag", eTag);
            return builder.build();
        }

        /*
         * Validate request headers for resume
         */
        String ifMatch = getRequestHeader(headers, "If-Match");
        if (ifMatch != null && !matches(ifMatch, eTag)) {
            // If-Match header should contain "*" or ETag. If not, then return 412.
            return Response.status(Status.PRECONDITION_FAILED).build();
        }

        String ifUnmodifiedSince = getRequestHeader(headers, "If-Unmodified-Since");
        long ifUnmodifiedSinceTime = ifUnmodifiedSince==null ? 0 :
            ISODateTimeFormat.basicDate().parseDateTime(ifUnmodifiedSince).getMillis();
        if (ifUnmodifiedSinceTime > 0 && ifUnmodifiedSinceTime + 1000 <= lastModifiedTime) {
            // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
            return Response.status(Status.PRECONDITION_FAILED).build();
        }

        /*
         * Validate and process range
         */
        // Prepare some variables. The full Range represents the complete file.
        final Range full = new Range(0, fileLength - 1, fileLength);
        List<Range> ranges = new ArrayList<Range>();

        // Validate and process Range and If-Range headers.
        String range = getRequestHeader(headers, "Range");
        if (range != null) {

            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                ResponseBuilder builder = Response.status(416)
                        .header("Content-Range", "bytes */" + fileLength);// Required in 416.
                return builder.build();
            }

            // If-Range header should either match ETag or be greater then LastModified. If not,
            // then return full file.
            String ifRange = getRequestHeader(headers, "If-Range");
            if (ifRange != null && !ifRange.equals(eTag)) {
                long ifRangeTime = ISODateTimeFormat.basicDate().parseDateTime(ifRange).getMillis();
                if (ifRangeTime > 0 && ifRangeTime + 1000 < lastModifiedTime) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte range.
            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    // Assuming a file with fileLength of 100, the following examples returns bytes at:
                    // 50-80 (50 to 80), 40- (40 to fileLength=100), -20 (fileLength-20=80 to fileLength=100).
                    long start = sublong(part, 0, part.indexOf("-"));
                    long end = sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = fileLength - end;
                        end = fileLength - 1;
                    } else if (end == -1 || end > fileLength - 1) {
                        end = fileLength - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        ResponseBuilder builder = Response.status(416)
                                .header("Content-Range", "bytes */" + fileLength);// Required in 416.
                        return builder.build();
                    }

                    // Add range.
                    ranges.add(new Range(start, end, fileLength));
                }
            }
        }

        /*
         * Prepare and initialize response
         */
        boolean fullContent = ranges.isEmpty() || ranges.get(0) == full;
        ResponseBuilder builder = fullContent ? Response.ok() : Response.status(206);

        builder.header("content-disposition", "attachment; filename = " + fileName)
               .header("Accept-Ranges", "bytes")
               .header("ETag", eTag)
               .header(HttpHeaders.LAST_MODIFIED, lastModified);

        if (fullContent || ranges.size() == 1) {
            final Range r = fullContent ? full : ranges.get(0);
            return singlePartExtractResponse(builder, bulkExtractFile, r);
        } else {
            return multiPartsExtractResponse(builder, bulkExtractFile, ranges);
        }
    }

    private Response singlePartExtractResponse(final ResponseBuilder builder,
            final File bulkExtractFile, final Range r) {

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                InputStream input = null;
                try {
                    input = new FileInputStream(bulkExtractFile);
                    IOUtils.copyLarge(input, output, r.start, r.length);
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }
        };

        builder.entity(out)
               .header("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total)
               .header("Content-Length", String.valueOf(r.length));
        return builder.build();
    }

    private Response multiPartsExtractResponse(final ResponseBuilder builder,
            final File bulkExtractFile, final List<Range> ranges) {

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                InputStream input = null;
                try {
                    input = new FileInputStream(bulkExtractFile);
                    // Copy multi part range.
                    for (Range r : ranges) {
                        output.write( "\r\n".getBytes() );
                        output.write( (MULTIPART_BOUNDRY_SEP + "\r\n").getBytes() );
                        output.write( ("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total+ "\r\n").getBytes() );
                        IOUtils.copyLarge(input, output, r.start, r.length);
                    }
                    output.write( "\r\n".getBytes() );
                    output.write( (MULTIPART_BOUNDRY_END + "\r\n").getBytes() );
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }
        };

        builder.entity(out)
               .header("Content-Type", "multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
        return builder.build();
    }

    private String getRequestHeader(HttpHeaders headers, String name) {
        List<String> list = headers==null ? null : headers.getRequestHeader(name);
        return list==null || list.isEmpty() ? null : list.get(0);
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
    private ExtractFile getBulkExtractFile(String deltaDate, String appId) {
        boolean isDelta = deltaDate != null;
        initializePrincipal();
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL,
                principal.getTenantId()));
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
    private void checkApplicationAuthorization(Set<String> edorgsForExtract) throws AccessDeniedException {
        OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Entity app = getApplication(auth);
        Map<String, Object> body = app.getBody();
        if (!body.containsKey("isBulkExtract") || (Boolean) body.get("isBulkExtract") == false) {
            throw new AccessDeniedException("Application is not approved for bulk extract");
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
     * Returns true if the given match header matches the given value.
     * @param matchHeader The match header.
     * @param toMatch The value to be matched.
     * @return True if the given match header matches the given value.
     */
    private static boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
            || Arrays.binarySearch(matchValues, "*") > -1;
    }

    /**
     * Returns a substring of the given string value from the given begin index to the given end
     * index as a long. If the substring is empty, then -1 will be returned
     * @param value The string value to return a substring as long for.
     * @param beginIndex The begin index of the substring to be returned as long.
     * @param endIndex The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring is empty.
     */
    private static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    /**
     * Represents a byte range.
     */
    private class Range {
        long start;
        long end;
        long length;
        long total;

        /**
         * Construct a byte range.
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

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

}
