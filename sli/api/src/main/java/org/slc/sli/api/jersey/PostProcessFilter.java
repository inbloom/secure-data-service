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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.MongoStat;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Post Processing filter
 * Validates context to serviced request
 * Outputs time elapsed for request
 * Cleans up security context
 *
 * @author dkornishev
 *
 */
@Component
public class PostProcessFilter implements ContainerResponseFilter {

    private static final int LONG_REQUEST = 1000;

    private static final String GET = ResourceMethod.GET.toString();

    private DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
    
    @Autowired
    private ContextValidator contextValidator;

    @Autowired
    private EdOrgHelper edOrgHelper;

    @Autowired
    private SecurityCachingStrategy securityCachingStrategy;

    @Autowired
    @Qualifier("performanceRepo")
    private Repository<Entity> perfRepo;    
    
    @Value("${sli.application.buildTag}")
    private String buildTag;

    @Value("${sli.api.performance.tracking}")
    private String apiPerformanceTracking;

    @Value("${api.server.url}")
    private String apiServerUrl;

    @Autowired
    private MongoStat mongoStat;

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (isRead(request.getMethod()) && isSuccessfulRead(response.getStatus())) {
            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            principal.setSubEdOrgHierarchy(edOrgHelper.getStaffEdOrgsAndChildren());
            contextValidator.validateContextToUri(request, principal);
        }

        SecurityContextHolder.clearContext();

        if ("true".equals(apiPerformanceTracking)) {
            logApiDataToDb(request, response);
        }
        TenantContext.cleanup();
        printElapsed(request);
        expireCache();

        String queryString = "";
        if (null != request.getRequestUri().getQuery()) {
            queryString = "?" + request.getRequestUri().getQuery();
        }
        response.getHttpHeaders().add("X-RequestedPath", request.getProperties().get("requestedPath"));
        response.getHttpHeaders().add("X-ExecutedPath", request.getPath() + queryString);

        //        Map<String,Object> body = (Map<String, Object>) response.getEntity();
        //        body.put("requestedPath", request.getProperties().get("requestedPath"));
        //        body.put("executedPath", request.getPath());

        return response;
    }

    /**
     * Returns true if the request is a read operation.
     *
     * @param request
     *            Request to be checked.
     * @return True if the request method is a GET, false otherwise.
     */
    private boolean isRead(String operation) {
        return operation.equals(GET);
    }

    /**
     * Returns true if the response dictates a successful read.
     *
     * @param status
     *            Reponse status.
     * @return True if the response status is 'OK', false otherwise.
     */
    private boolean isSuccessfulRead(int status) {
        return status == Status.OK.getStatusCode();
    }

    private void printElapsed(ContainerRequest request) {
        long startTime = (Long) request.getProperties().get("startTime");
        long elapsed = System.currentTimeMillis() - startTime;

        info("{} finished in {} ms", request.getRequestUri().toString(), elapsed);

        if (elapsed > LONG_REQUEST) {
            warn("Long request: {} elapsed {}ms > {}ms", request.getRequestUri().toString(), elapsed, LONG_REQUEST);
        }
    }

    private void expireCache() {
        if (securityCachingStrategy != null) {
            securityCachingStrategy.expire();
        }
    }

    static long bucket = 0;
    private void logApiDataToDb(ContainerRequest request, ContainerResponse response) {
        long startTime = (Long) request.getProperties().get("startTime");
        long endTime = System.currentTimeMillis(); 
        long elapsed = endTime - startTime;
        long startBucket = bucket;

        Map<String, Object> body = new HashMap<String, Object>();

        //extract parameters from the request URI
        String requestURL = request.getRequestUri().toString();
        if (requestURL.contains("?")) {
            requestURL = requestURL.substring(0, requestURL.indexOf("?"));
        }
        String serverUrl = apiServerUrl + "api/rest/v1/";
        UriTemplate fourPartUri = new UriTemplate(serverUrl + "{resource}/{id}/{association}/{associateEntity}");
        UriTemplate threePartUri = new UriTemplate(serverUrl + "{resource}/{id}/{association}");
        UriTemplate twoPartUri = new UriTemplate(serverUrl + "{resource}/{id}");
        UriTemplate readAllUri = new UriTemplate(serverUrl + "{resource}");
        HashMap<String, String> uri = new HashMap<String, String>();
        boolean logIntoDb = ((Boolean) request.getProperties().get("logIntoDb")).booleanValue();
        if (!fourPartUri.match(requestURL , uri)) {
            if (!threePartUri.match(requestURL , uri)) {
                if (!twoPartUri.match(requestURL , uri)) {
                    logIntoDb = readAllUri.match(requestURL , uri);
                }
            }
        }

        if (logIntoDb) {
            String endPoint = "/" + uri.get("resource");
            if (uri.get("id") != null) {
                endPoint += "/{id}";
                if (uri.get("association") != null) {
                    endPoint += "/" + uri.get("association");
                }
                if (uri.get("associateEntity") != null) {
                    endPoint += "/" + uri.get("associateEntity");
                }
            }
            
            String reqId = request.getHeaderValue("x-request-id");
            if (null != reqId) {
                body.put("reqid", reqId); 
            }

            body.put("url", requestURL.replace(serverUrl, "/"));
            body.put("method", request.getMethod());
            body.put("entityCount", response.getHttpHeaders().get("TotalCount"));
            body.put("resource", endPoint);
            body.put("buildNumber", buildTag);
            body.put("id", uri.get("id"));
            body.put("parameters", request.getQueryParameters());
            body.put("Date", dateFormatter.print(new DateTime(System.currentTimeMillis())));
            body.put("startTime", startTime);
            // Note: Currently the start and end times are recorded in ms since the epoch. 
            //       here is how they were formatted in the past 
            //  body.put("startTime", timeFormatter.print(new DateTime(startTime)));
            //  body.put("endTime", timeFormatter.print(new DateTime(System.currentTimeMillis())));
            body.put("endTime", endTime);
            body.put("responseTime", String.valueOf(elapsed));
            body.put("dbHitCount", mongoStat.getDbHitCount());

            // break stats up into multiple 1k stat documents.
            List<String> stats = mongoStat.getStats();
            List<String> statsBucket = new ArrayList<String>();
            for (int i=0; i < stats.size(); ++i) {
                statsBucket.add(stats.get(i));
                if (((i+1) % 1000) == 0) {
                    Map<String, Object> statDoc = new HashMap<String, Object>();
                    statDoc.put("id", Long.toString(bucket++));
                    statDoc.put("stats", statsBucket);
                    perfRepo.create("apiResponseStat", statDoc, "apiResponseStat");
                    statsBucket.clear();
                }
            }

            if (!statsBucket.isEmpty()) {
                Map<String, Object> statDoc = new HashMap<String, Object>();
                statDoc.put("id", Long.toString(bucket++));
                statDoc.put("stats", statsBucket);
                perfRepo.create("apiResponseStat", statDoc, "apiResponseStat");
                statsBucket.clear();
            }

            ArrayList<String> tmp = new ArrayList<String>();
            for (long i = startBucket; i < bucket; ++i) {
                tmp.add(Long.toString(i));
            }

            body.put("stats", tmp);
            perfRepo.create("apiResponse", body, "apiResponse");
        }

    }

}
