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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.security.TenantResource.LandingZoneInfo;
import org.slc.sli.api.resources.security.TenantResource.TenantResourceCreationException;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * Resources available to administrative apps during the onboarding and provisioning process.
 */
@Component
@Scope("request")
@Path("/provision")
@Produces({ HypermediaType.JSON + ";charset=utf-8" })
public class OnboardingResource {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private TenantResource tenantResource;

    // Use this to check if we're in sandbox mode
    @Value("${sli.sandbox.enabled}")
    protected boolean isSandboxEnabled;

    public static final String STATE_EDUCATION_AGENCY = "State Education Agency";
    public static final String STATE_EDORG_ID = "stateOrganizationId";
    public static final String EDORG_INSTITUTION_NAME = "nameOfInstitution";
    public static final String ADDRESSES = "address";
    public static final String ADDRESS_STREET = "streetNumberName";
    public static final String ADDRESS_CITY = "city";
    public static final String ADDRESS_STATE_ABRV = "stateAbbreviation";
    public static final String ADDRESS_POSTAL_CODE = "postalCode";
    public static final String CATEGORIES = "organizationCategories";  // 'State Education Agency'
    public static final String PRELOAD_FILES_ID = "preloadFiles";

    private final String landingZoneServer;

    @Autowired
    public OnboardingResource(@Value("${sli.landingZone.server}") String landingZoneServer) {
        super();
        this.landingZoneServer = landingZoneServer;
    }

    /**
     * Provision a landing zone for the provide educational organization.
     *
     * @QueryParam stateOrganizationId -- the unique identifier for this ed org
     */
    @POST
    @RightsAllowed({Right.INGEST_DATA })
    public Response provision(Map<String, String> reqBody, @Context final UriInfo uriInfo) {
        String orgId = reqBody.get(STATE_EDORG_ID);
        if (orgId == null) {
            return Response.status(Status.BAD_REQUEST).entity("Missing required " + STATE_EDORG_ID).build();
        }
        Response r = createEdOrg(orgId);
        return r;
    }

    /**
     * Create an EdOrg if it does not exists.
     *
     * @param orgId
     *            The State Educational Organization identifier.
     * @return Response of the request as an HTTP Response.
     */
    public Response createEdOrg(final String orgId) {

        String tenantId = SecurityUtil.getTenantId();

        try {
            LandingZoneInfo landingZone = tenantResource.createLandingZone(tenantId, orgId, isSandboxEnabled);

            Map<String, String> returnObject = new HashMap<String, String>();
            returnObject.put("landingZone", landingZone.getLandingZonePath());
            returnObject.put("serverName", landingZoneServer);

            NeutralQuery tenantQuery = new NeutralQuery();
            tenantQuery.addCriteria(new NeutralCriteria("tenantId", "=", tenantId));

            String tenantUuid = null;
            Entity tenantEntity = repo.findOne("tenant", tenantQuery);
            if (tenantEntity != null) {
                tenantUuid = tenantEntity.getEntityId();
            }
            returnObject.put("tenantUuid", tenantUuid);
            return Response.status(Status.CREATED).entity(returnObject).build();
        } catch (TenantResourceCreationException trce) {
            EntityBody entityBody = new EntityBody();
            entityBody.put("message", trce.getMessage());
            return Response.status(trce.getStatus()).entity(entityBody).build();
        }
    }

}
