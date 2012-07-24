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


package org.slc.sli.api.jersey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;


/**
 * Post Processing filter
 * Outputs time elapsed for request
 * Cleans up security context
 *
 * @author dkornishev
 *
 */
@Component
public class PostProcessFilter implements ContainerResponseFilter {

    private static final int LONG_REQUEST = 1000;
    private DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSSZ");
    private DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

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

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        SecurityContextHolder.clearContext();

        if ("true".equals(apiPerformanceTracking)) {
            logApiDataToDb(request, response);
        }
        TenantContext.setTenantId(null);
        printElapsed(request);
        expireCache();

        return response;
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
    private void logApiDataToDb(ContainerRequest request, ContainerResponse response) {
        long startTime = (Long) request.getProperties().get("startTime");
        long elapsed = System.currentTimeMillis() - startTime;

        HashMap<String, Object> body = new HashMap<String, Object>();

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
//        System.out.print(serverUrl + "{resource}/{id}");
        HashMap<String, String> uri = new HashMap<String, String>();
       // MultiMap<String,String> quaryParams;
        boolean logIntoDb = true;
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

            body.put("url", requestURL.replace(serverUrl, "/"));
            body.put("method", request.getMethod());
            body.put("entityCount", response.getHttpHeaders().get("TotalCount"));
            body.put("resource", endPoint);
            body.put("buildNumber", buildTag);
            body.put("id", uri.get("id"));
            body.put("parameters", request.getQueryParameters());
            body.put("Date", dateFormatter.format(System.currentTimeMillis()));
            body.put("startTime", timeFormatter.format(new Date(startTime)));
            body.put("endTime", timeFormatter.format(new Date(System.currentTimeMillis())));
            body.put("responseTime", String.valueOf(elapsed));
            perfRepo.create("apiResponse", body, "apiResponse");
        }

    }

}
