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


package org.slc.sli.api.resources.security;

import java.util.*;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.roles.RightAccessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.security.oauth.ApplicationAuthorizationValidator;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Used to retrieve the list of apps that a user is allowed to use.
 *
 * @author pwolf
 *
 */
@Component
@Scope("request")
@Path("/userapps")
@Produces({ HypermediaType.JSON + ";charset=utf-8" })
public class ApprovedApplicationResource {

    @Autowired
    private RightAccessValidator rightAccessValidator;

    public static final String RESOURCE_NAME = "application";

    private static final String[] ALLOWED_ATTRIBUTES = new String[] {
        "application_url", "administration_url", "image_url", "description",
 "name", "vendor", "version", "is_admin", "behavior", "endpoints"
    };

    @Autowired
    private ApplicationAuthorizationValidator appValidator;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @GET
    @RightsAllowed(any = true)
    public Response getApplications(@DefaultValue("") @QueryParam("is_admin") String adminFilter) {
        List<EntityBody> results = new ArrayList<EntityBody>();

        NeutralQuery query = new NeutralQuery(0);

        for (Entity result : repo.findAll("application", query)) {
            if (appValidator.isAuthorizedForApp(result, SecurityUtil.getSLIPrincipal())) {
                EntityBody body = new EntityBody(result.getBody());
                if (!shouldFilterApp(result, adminFilter)) {

                    filterAttributes(body);
                    results.add(body);
                }
            }
        }
        return Response.status(Status.OK).entity(results).build();
    }

    @SuppressWarnings("unchecked")
    private boolean shouldFilterApp(Entity app, String adminFilter) {
        EntityBody body = new EntityBody(app.getBody());

        if (body.containsKey("endpoints")) {
            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) body.get("endpoints");
            filterEndpoints(app);

            //we ended up filtering out all the endpoints - no reason to display the app
            if (endpoints.size() == 0) {
                return true;
            }
        }

        boolean isAdminApp = body.containsKey("is_admin") ? Boolean.valueOf((Boolean) body.get("is_admin")) : false;

        //is_admin query param specified
        if (!adminFilter.equals("")) {
            boolean adminFilterVal = Boolean.valueOf(adminFilter);

            //non-admin app, but is_admin == true
            if (!isAdminApp && adminFilterVal) {
                return true;
            }

            //admin app, but is_admin == false
            if (isAdminApp && !adminFilterVal) {
                return true;
            }
        }

        // don't allow "installed" apps
        if (body.get("installed") == null || (Boolean) body.get("installed")) {
            return true;
        }

        return false;
    }

    private Set<String> getUsersRights(Entity app) {
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        Set<String> rights = new HashSet<String>();

        if (principal.isAdminRealmAuthenticated()) {
            for (GrantedAuthority right : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
                authorities.add(right);
            }

            //This is a fake role we use mean that a user is either an LEA admin or an SEA admin with delegated rights
            if (hasAppAuthorizationRight()) {
                rights.add("DELEGATED_ADMIN");
            }
        } else {
            // Add appropriate contextual rights for federated users
            Collection<GrantedAuthority> userAuthorities = rightAccessValidator.getContextualAuthorities(false, app, SecurityUtil.getUserContext(), true);
            authorities.addAll(userAuthorities);
        }

        // convert to String for comparison with rights in DB
        for (GrantedAuthority authority: authorities) {
            rights.add(authority.toString());
        }

        return rights;
    }

    private boolean hasAppAuthorizationRight() {
        if (SecurityUtil.hasRight(Right.EDORG_APP_AUTHZ)) {
            //edorg authz users always have the right to authorize apps
            return true;
        }
        return false;
    }

    private void filterEndpoints(Entity app) {
        List<Map<String, Object>> endpoints = Collections.EMPTY_LIST;
        EntityBody body = new EntityBody(app.getBody());
        Set<String> userRights = getUsersRights(app);

        if (body.containsKey("endpoints")) {
            endpoints = (List<Map<String, Object>>) body.get("endpoints");
        }

        for (Iterator<Map<String, Object>> i = endpoints.iterator(); i.hasNext();) {

            @SuppressWarnings("unchecked")
            List<String> reqRights = (List<String>) i.next().get("rights");

            //if no rights specified, don't filter it
            if (reqRights.size() == 0) {
                continue;
            }

            List<String> intersection = new ArrayList<String>(reqRights);
            intersection.retainAll(userRights);
            if (userRights.size() == 0 || intersection.size() == 0) {
                debug("Removing endpoint because users rights {} did not match required rights {}.", userRights, reqRights);
                i.remove();
            }
        }
    }


    /**
     * Filters out attributes we don't want ordinary users to see.
     * For example, they should never see the client_secret.
     *
     * @param result
     */
    private void filterAttributes(EntityBody result) {
        for (Iterator<Map.Entry<String, Object>> i = result.entrySet().iterator(); i.hasNext();) {
            String key = i.next().getKey();
            if (!Arrays.asList(ALLOWED_ATTRIBUTES).contains(key)) {
                i.remove();
            }
        }
    }
}
