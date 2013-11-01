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
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.sun.jersey.api.Responses;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.header.reader.HttpHeaderReader;

import org.apache.commons.io.IOUtils;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A helper class to get the response from a http GET/HEAD request for a file.
 * The pre-conditions are that the requested file exists, is accessible and
 * the last modified time is known.
 * <pr>
 * The following file request headers are supported:
 * <ol>
 * <li>{@code If-Match}</li>
 * <li>{@code If-None-Match}</li>
 * <li>{@code If-Unmodified-Since}</li>
 * <li>{@code If-Modified-Since}</li>
 * <li>{@code Range}</li>
 * <li>{@code If-Range}</li>
 * </ol>
 *
 * @author slee
 *
 */
@Component
public class FileResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
    private static final String MULTIPART_BOUNDARY_SEP = "--" + MULTIPART_BOUNDARY;
    private static final String MULTIPART_BOUNDARY_END = MULTIPART_BOUNDARY_SEP + "--";
    private static final String CRLF = "\r\n";
    private static final String DATE_FORMAT = "EEE MMM d HH:mm:ss z yyyy";

    private UriInfo uri;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    /**
     * Get the file request response
     *
     * @param req
     *          The http request context
     * @param requestedFile
     *          The requested file to return
     * @param lastModifiedTime
     *          The last modified time
     * @param lastModified
     *          The last modified date string
     * @return
     *          Response with the requested file
     * @throws ParseException
     */
    public Response getFileResponse(final HttpRequestContext req,
            final File requestedFile, final long lastModifiedTime, final String lastModified, UriInfo uri) {

        this.uri = uri;

        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date httpDate = null;

        LOG.info("Retrieving bulk extract with method {}", req.getMethod());
        String fileName = requestedFile.getName();
        long fileLength = requestedFile.length();
        String eTag = fileName + "_" + fileLength + "_" + lastModified;

        /*
         * Validate request headers for caching and resume
         */
        ResponseBuilder builder = null;
        try {
            httpDate = format.parse(lastModified);

            builder = req.evaluatePreconditions(httpDate, new EntityTag(eTag));
        } catch (ParseException e) {
            LOG.error("Unable to parse bulk extract Last-Modified date into a HTTP-Date format: {}", e);
        }

        if (builder != null) {
            // evaluate fails
            logSecurityEvent("Bulk Extract request header preconditions failed");
            return builder.build();
        }

        /*
         * Validate and process range
         */
        // Prepare some variables. The full Range represents the complete file.
        final Range full = new Range(0, fileLength - 1, fileLength);
        final List<Range> ranges = new ArrayList<Range>();

        builder = processRangeHeader(req, full, ranges, fileLength, httpDate.getTime(), eTag);
        if (builder != null) {
            // validation fails
            return builder.build();
        }

        /*
         * Combine overlapped ranges
         */
        if (ranges.size() > 1) {
            combineOverlapped(ranges);
        }

        /*
         * Prepare and initialize response
         */
        boolean fullContent = ranges.isEmpty() || ranges.get(0) == full || ranges.get(0).sameValue(full);
        boolean headMethod = req.getMethod().equals("HEAD");
        builder = fullContent ? Response.ok() : Response.status(206);

        builder.header("content-disposition", "attachment; filename = " + fileName)
               .header("Accept-Ranges", "bytes")
               .header("ETag", eTag)
               .header(HttpHeaders.LAST_MODIFIED, httpDate);

        if (fullContent || ranges.size() == 1) {
            final Range r = fullContent ? full : ranges.get(0);
            return singlePartFileResponse(builder, requestedFile, r, headMethod);
        } else {

            if (headMethod) {
                builder = Responses.methodNotAllowed()
                        .header("Allow", "GET");
                return builder.build();
            }

            return multiPartsFileResponse(builder, requestedFile, ranges);
        }
    }

    /**
     * Combine overlapped and contiguous ranges
     * @param ranges The ranges to combine
     */
    private void combineOverlapped(final List<Range> ranges) {
        Collections.sort(ranges);

        for (int i=0; i<ranges.size() - 1; i++) {
           Range first = ranges.get(i);
           Range second = ranges.get(i + 1);

           if (!first.hasGap(second)) {
              first.combine(second);
              // delete second range
              ranges.remove(i + 1);
              // reset loop index
              i--;
           }
        }
    }

    /**
     * Validate and process range request.
     *
     * @param req
     *          The http request context
     * @param full
     *          The full range of the requested file
     * @param ranges
     *          The list where the processed ranges are stored
     * @param fileLength
     *          The length of the requested file
     * @param lastModifiedTime
     *          The last modified time of the file
     * @param eTag an ETag for the current state of the resource
     * @return null if the range request is valid or a ResponseBuilder set with
     * the status of 416 if the range request cannot be processed.  A returned
     * ResponseBuilder will include a Content-Range set to the file length.
     */
    private ResponseBuilder processRangeHeader(final HttpRequestContext req,
            final Range full, final List<Range> ranges,
            final long fileLength, final long lastModifiedTime, final String eTag) {

        String range = req.getHeaderValue("Range");
        if (range != null && range.length() > 0) {

            // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                logSecurityEvent("Range header doesn't match format");
               return Response.status(416)
                        .header("Content-Range", "bytes */" + fileLength);// Required in 416.
            }

            // If-Range header should either match ETag or be greater then LastModified. If not,
            // then return full file.
            String ifRange = req.getHeaderValue("If-Range");
            if (ifRange != null && !ifRange.equals(eTag)) {
                try {
                    long ifRangeTime = HttpHeaderReader.readDate(ifRange).getTime();
                    if (ifRangeTime > 0 && ifRangeTime + 1000 < lastModifiedTime) {
                        ranges.add(full);
                    }
                } catch (ParseException ignore) {
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
                        start = Math.max(0, fileLength - end);
                        end = fileLength - 1;
                    } else if (end == -1 || end > fileLength - 1) {
                        end = fileLength - 1;
                    }

                    // Check if Range is syntactically valid. If not, then return 416.
                    if (start > end) {
                        logSecurityEvent("If Range is not syntactically valid");
                        return Response.status(416)
                                .header("Content-Range", "bytes */" + fileLength);// Required in 416.
                    }

                    // Add range.
                    ranges.add(new Range(start, end, fileLength));
                }
            }
        }
        return null;
    }

    /**
     * Send a single part response for a file request
     *
     * @param builder
     *          The response builber
     * @param requestedFile
     *          The requested file
     * @param r
     *          The range corresponding the response
     * @param headMethod
     *          if this is a head request
     * @return
     *          Response with the requested range of content
     */
    private Response singlePartFileResponse(final ResponseBuilder builder,
            final File requestedFile, final Range r, boolean headMethod) {

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                RandomAccessFile raf = null;
                try {
                    raf = new RandomAccessFile(requestedFile, "r");
                    long total = sendRangeContent(raf, output, r.start, r.length);

                    Object[] obj = {
                            r,
                            new Long(total)
                    };
                    LOG.debug("singlePartFileResponse\n{}\nstream length={}", obj);
                } finally {
                    IOUtils.closeQuietly(raf);
                }
            }
        };

        builder.header("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total)
               .header("Content-Length", String.valueOf(r.length));
        if (!headMethod) {
            builder.entity(out);
            LOG.info("Retrieving bulk extract with method {}", 3);
        }
        logSecurityEvent("Successful request for singlePartFileResponse");
        return builder.build();
    }

    /**
     * Send multi parts response for a file request
     *
     * @param builder
     *          The response builber
     * @param requestedFile
     *          The requested file
     * @param ranges
     *          The ranges corresponding the response
     * @return
     *          Response with the requested ranges of content
     */
    private Response multiPartsFileResponse(final ResponseBuilder builder,
            final File requestedFile, final List<Range> ranges) {

        StreamingOutput out = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                RandomAccessFile raf = null;
                try {
                    raf = new RandomAccessFile(requestedFile, "r");
                    // Copy multi part range.
                    for (Range r : ranges) {
                        sendRangeResponse(r, raf, output);
                    }
                    output.write( (CRLF+ MULTIPART_BOUNDARY_END + CRLF).getBytes() );
                    LOG.debug(CRLF+ MULTIPART_BOUNDARY_END + CRLF);
                } finally {
                    IOUtils.closeQuietly(raf);
                }
            }

        };

        long contentLength = MULTIPART_BOUNDARY_END.length() + 4;
        for (Range r : ranges) {
            contentLength += multiPartRangeHeader(r).length();
            contentLength += r.length;
        }

        builder.entity(out)
               .header("Content-Type", "multipart/byteranges; boundary=" + MULTIPART_BOUNDARY)
               .header("Content-Length", String.valueOf(contentLength));

        logSecurityEvent("Successful request for multiPartsFileResponse");
        return builder.build();
    }

    /**
     * Send a range content
     *
     * @param raf    the file from which the content is sent
     * @param output the output to which the content is sent
     * @param start  the start position of the file
     * @param length the length of content to send
     * @return       total length sent
     * @throws IOException
     */
    private long sendRangeContent(RandomAccessFile raf, OutputStream output, long start, long length)
            throws IOException {

        raf.seek(start);
        byte[] buffer = new byte[4*1024];
        long left = length;
        long total = 0;
        int n = -1;

        while ( left > 0 &&
                (n = raf.read(buffer, 0, (int) Math.min(left, buffer.length))) > -1) {
            output.write(buffer, 0, n);
            output.flush();
            left -= n;
            total += n;
        }
        return total;
    }

    /**
     * Send a range header and its content
     *
     * @param r      the range
     * @param raf    the file from which the content is sent
     * @param output the output to which the content is sent
     * @throws IOException
     */
    private void sendRangeResponse(Range r, RandomAccessFile raf, OutputStream output) throws IOException {

        String rangeHeader = multiPartRangeHeader(r);
        output.write( rangeHeader.getBytes() );
        output.flush();
        long total = sendRangeContent(raf, output, r.start, r.length);

        Object[] obj = {
                rangeHeader,
                r,
                new Long(rangeHeader.length()),
                new Long(total)
        };
        LOG.debug("multiPartsFileResponse\n{}\n{}\nheader length={} stream length={}", obj );
    }

    /**
     * Calculate the range header within a multipart response
     *
     * @param r  the range
     * @return   the range header
     */
    private String multiPartRangeHeader(Range r) {
        StringBuilder sb = new StringBuilder();
        // output multi-part boundry separator
        sb.append( CRLF + MULTIPART_BOUNDARY_SEP + CRLF );
        // output content type and range size sub-header for this part
        sb.append( "Content-Type: application/x-tar" + CRLF );
        sb.append( "Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total+ CRLF );
        return sb.toString();
    }


    /**
     * Returns a substring of the given string value from the given begin index to the given end
     * index as a long. If the substring is empty, then -1 will be returned
     * @param value The string value to return a substring as long for.
     * @param beginIndex The begin index of the substring to be returned as long.
     * @param endIndex The end index of the substring to be returned as long.
     * @return A substring of the given string value as long or -1 if substring is empty.
     */
    private long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    /**
     * Represents a byte range from a Rnge request.
     */
    private class Range implements Comparable<Range> {
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

        public boolean sameValue(Range r) {
            if (r == null) {
                return false;
            }

            return r.start == this.start &&
                    r.end == this.end &&
                    r.length == this.length &&
                    r.total == this.total;
        }

        @Override
        public String toString() {
            Object[] arguments = {
                    Long.valueOf(start),
                    Long.valueOf(end),
                    Long.valueOf(length),
                    Long.valueOf(total)
            };
            return MessageFormat.format("Range: start={0} end={1} length={2} total={3}", arguments);
        }

        @Override
        public int compareTo(Range o) {
            return (int) (end - o.end);
        }

        /**
         * Test if the two ranges has gap, i.e.
         * not overlapped nor contiguous
         * @param o the range to test
         * @return true or false
         */
        public boolean hasGap(Range o) {
            return ((this.end+1) < o.start) || ((o.end+1) < this.start);
        }

        /**
         * Combine the range into this range
         *
         * @param o the range to combine
         */
        void combine(Range o) {
            if (hasGap(o)) {
                return;
            }

            if (this.end < o.end) {
                this.end = o.end;
            }
            if (this.start > o.start) {
                this.start = o.start;
            }
            this.length = this.end - this.start + 1;
        }
    }

    void logSecurityEvent(String message) {
        audit(securityEventBuilder.createSecurityEvent(FileResource.class.getName(),
                uri.getRequestUri(), message, true));
    }

}
