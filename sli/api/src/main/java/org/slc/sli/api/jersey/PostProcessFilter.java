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

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.api.uri.UriTemplate;

import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


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
    
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        SecurityContextHolder.clearContext();
        logApiDataToDb(request);
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
    private void logApiDataToDb(ContainerRequest request){
        long startTime = (Long) request.getProperties().get("startTime");
        long elapsed = System.currentTimeMillis() - startTime;

        HashMap<String,Object> body = new HashMap<String, Object>();

        //extract parameters from the request URI
        String requestURL=  request.getRequestUri().toString();
        String path = request.getPath().toString();
        UriTemplate fourPartUri = new UriTemplate("http://local.slidev.org:8080/api/rest/v1/{resource}/{id}/{association}/{associateEntity}");
        UriTemplate threePartUri = new UriTemplate("http://local.slidev.org:8080/api/rest/v1/{resource}/{id}/{associateEntity}");
        UriTemplate twoPartUri = new UriTemplate("http://local.slidev.org:8080/api/rest/v1/{resource}/{id}");
        UriTemplate readAllUri = new UriTemplate("http://local.slidev.org:8080/api/rest/v1/{resource}");
        HashMap<String, String> uri = new HashMap<String, String>();

        ut.match(requestURL,m);


        //if its a "?" uri extract parameter list

//        for (String partUri : path.split("/")) {
//            System.out.println(partUri);
//        }
        body.put("url",requestURL) ;
        body.put("resource",m.get("resource"));
        body.put("id",m.get("id"));
        body.put("association",m.get("association"));
        body.put("associateEntity",m.get("associateEntity"));
        body.put("Date", dateFormatter.format(System.currentTimeMillis()));
        body.put("startTime",timeFormatter.format(new Date(startTime))) ;
        body.put("endTime",timeFormatter.format(new Date(System.currentTimeMillis())));
        body.put("responseTime(ms)", String.valueOf(elapsed));
        perfRepo.create("apiResponse",body,"apiResponse");

    }
    
}
