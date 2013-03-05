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
package org.slc.sli.api.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.SecuritySessionResource;
import org.slc.sli.api.resources.SupportResource;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.security.SamlFederationResource;
import org.slc.sli.api.resources.v1.HomeResource;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.enums.Right;

/**
 * At startup, this filter factory assigns a {@link RightCheckResourceFilter} for
 * every resource method containing the {@link RightsAllowed} annotation.
 *
 * When the resource method is called, the corresponding filter verifies that the
 * user is authenticated and has the rights specified in the annotation.
 *
 * If no rights are specified, but any=true is used with the annotation,
 * then we simply check that the user is authenticated without verifying specific rights.
 *
 */
@Component
public class RightCheckFilterFactory implements ResourceFilterFactory {

    @Autowired
    SecurityEventBuilder builder;

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        List<ResourceFilter> rolesFilters = new ArrayList<ResourceFilter>();

        RightsAllowed check = am.getAnnotation(RightsAllowed.class);
        if (check != null) {
            rolesFilters.add(new RightCheckResourceFilter(check.value(), am.getResource()));
            if (check.value().length == 0 && !check.any()) {
                warn("Class {} should be specifying any=true in the {} annotation.", am.getResource().getClass().getName(), RightsAllowed.class.getSimpleName());
            }

        } else {
            logNoFilterMessage(am);
        }
        return rolesFilters;

    }

    //These are all known endpoints that don't need any extra right checks
    private static final List<String> LOG_EXCLUDE_LIST = Arrays.asList(
            HomeResource.class.getName(),
            SupportResource.class.getName(),
            SamlFederationResource.class.getName(),
            SecuritySessionResource.class.getName()
            );

    private void logNoFilterMessage(AbstractMethod am) {

        Class<?> resourceClass = am.getResource().getResourceClass();

        if (LOG_EXCLUDE_LIST.contains(resourceClass.getName())) {
            return;
        }

        //Don't worry about non-sli classes
        if (!resourceClass.getPackage().getName().startsWith("org.slc.sli")) {
            return;
        }

        //Don't worry about the dynamic endpoints
        if (GenericResource.class.isAssignableFrom(resourceClass)) {
            return;
        }
        debug("No RightsAllowed specified for {} of {}.",
                am.getMethod().getName(),
                am.getResource().getResourceClass().getName());
    }

    protected class RightCheckResourceFilter implements ResourceFilter, ContainerRequestFilter {

        private List<Right> rightList;

        AbstractResource resource;

        public RightCheckResourceFilter(Right[] rights, AbstractResource resource) {
            this.resource = resource;
            rightList = new ArrayList<Right>(rights.length);
            for (Right right : rights) {
                rightList.add(right);
            }
        }


        @Override
        public ContainerRequestFilter getRequestFilter() {
            return this;
        }

        @Override
        public ContainerResponseFilter getResponseFilter() {
            return null;
        }

        /**
         * Enforces the rights.
         *
         * Causes a InsufficientAuthenticationException (401) if the user isn't authenticated
         * Causes an {@link AccessDeniedException} if the user doesn't have the necessary rights,
         * and a security event is logged.
         */
        @Override
        public ContainerRequest filter(ContainerRequest request) {

            SecurityUtil.ensureAuthenticated();

            //If annotation user uses @RightsAllowed(any=true),
            //we just check that the user is authenticated and nothing else
            if (rightList.size() == 0) {
                return request;
            }

            for (Right right : rightList) {
                if (SecurityUtil.hasRight(right)) {
                    debug("User has needed right {} to access {}.", right, request.getPath());
                    return request;
                }
            }


            throw new AccessDeniedException("Failed to access URL because of insufficient rights.");
        }


    }


}
