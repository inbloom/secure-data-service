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

package org.slc.sli.api.security.pdp;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;

/**
 * @author dkornishev
 */
@Component
public class PolicyEnforcer {

    @Resource
    private ContextResolverStore store;

    @Resource
    private EdOrgHelper edorgHelper;

    @Resource
    private ContextInferranceHelper inferer;

    public void enforce(Authentication auth, ContainerRequest request) {

        if (request.getMethod() == "POST") {
            return;
        }

        // TODO: Need to clean up this hack before turning on the path based context resolvers
        /* (Temporarly) Allow root calls with query parameters through */
        if (null != request.getRequestUri().getQuery()) {
	        String[] queries = request.getRequestUri().getQuery().split("&");
	        for(String query : queries){
	        	if (!query.matches("(limit|offset|expandDepth|includeFields|excludeFields|sortBy|sortOrder|views|includeCustom|selector)=.+")) {
	        		return; //Escape if someone is attempting to query on entity fields
	        	}
	        }
        }

        List<PathSegment> seg = request.getPathSegments();

        SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
        if (seg.get(0).getPath().equals("v1")) {
            if (seg.size() < 3) {
                request.getProperties().put("requestedPath", request.getPath());
                String newPath = inferer.getInferredUri(seg.get(1).getPath(), user.getEntity());
                if (newPath != null) {
                    String parameters = request.getRequestUri().getQuery();
                    info("URI Rewrite from->to: {} -> {}", request.getPath(), newPath);
                    request.setUris(request.getBaseUri(), request.getBaseUriBuilder().path(seg.get(0).getPath()).path(newPath).replaceQuery(parameters).build());
                }
            }

            seg = request.getPathSegments();

            // Obtain resolver
            String entityName = seg.get(1).getPath();
            String entityId = null;
            if (seg.size() > 2) {
                entityId = seg.get(2).getPath();
            }

            List<String> ids = Collections.emptyList();

//            if ("schools".equals(entityName)) {
//                ids = edorgHelper.getAllEdOrgs(user.getEntity());
//            } else {
//                EntityContextResolver resolver = store.findResolver(user.getEntity().getType(), entityName.replaceAll("s$", ""));
//                ids = resolver.findAccessible(user.getEntity());
//            }
//
//            if (entityId != null && ids.contains(entityId)) {
//                info("Access Allowed: {}", request.getPath());
//            } else {
//                warn("Access Denied: {}", request.getPath());
//                //throw new AccessDeniedException("No context");
//            }
        }
    }

}
