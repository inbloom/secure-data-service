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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.resources.v1.HypermediaType;

/**
 * Support Resource
 * This resource provides the basic contact information (E-Mail) of who to contact
 * for support issues.
 */
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class SupportResource {

    @Value("${sli.support.email}")
    private String email;

    /**
     * Returns a simple json object with the email of who to contact for support
     *
     * @return {email: someone@someone.com}
     */
    @GET
    @Path("email")
    public Object getEmail() {
        if (!isAuthenticated(SecurityContextHolder.getContext())) {
            throw new InsufficientAuthenticationException("User must be logged in");
        }
        Map<String, String> emailMap = new HashMap<String, String>();
        emailMap.put("email", email);
        return emailMap;
    }

    private boolean isAuthenticated(SecurityContext securityContext) {
        return securityContext.getAuthentication().isAuthenticated();
    }

}
