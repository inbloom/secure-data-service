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
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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
        body.put("url",request.getRequestUri().toString()) ;
        body.put("startTime",formatter.format(new Date(startTime))) ;
        body.put("endTime",formatter.format(new Date(System.currentTimeMillis())));
        body.put("responseTime", elapsed);
        perfRepo.create("apiResponse",body,"apiResponse");

    }
    
}
