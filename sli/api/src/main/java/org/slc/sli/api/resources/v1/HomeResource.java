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


package org.slc.sli.api.resources.v1;

//
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.Home;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

/**
 *
 * Provides initial information for a user. This includes providing different links to self and associated
 * resources.
 *
 * @author pghosh
 *
 */
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8", MediaType.APPLICATION_XML + ";charset=utf-8" })
public class HomeResource {

    final EntityDefinitionStore entityDefs;

    @Autowired
    HomeResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }

    /**
     * Provides a set of initial information when a user logs in. This
     * includes a self link and links to resources with which the user
     * is associated.
     */
    @GET
    public Response getHomeUri(@Context final UriInfo uriInfo) {

        Home home = null;

        // get the entity ID and EntityDefinition for user
        Pair<String, EntityDefinition> pair = this.getEntityInfoForUser();
        if (pair != null) {
            String userId = pair.getLeft();
            EntityDefinition defn = pair.getRight();

            EntityBody body = new EntityBody();
            body.put("id", userId);

            // prepare a list of links with the self link
            List<EmbeddedLink> links = ResourceUtil.getLinks(this.entityDefs, defn, body, uriInfo);

            // create a final map of links to relevant links
            HashMap<String, Object> linksMap = new HashMap<String, Object>();
            linksMap.put(ResourceConstants.LINKS, links);

            // return as browser response
            home = new Home(defn.getStoredCollectionName(), linksMap);
        } else {
          throw new InsufficientAuthenticationException("No entity mapping found for user");
        }

        return Response.ok(home).build();
    }

    /**
     * Analyzes security context to get ID and EntityDefinition for user.
     *
     * @return Pair containing ID and EntityDefinition from security context
     */
    private Pair<String, EntityDefinition> getEntityInfoForUser() {
        Pair<String, EntityDefinition> pair = null;

        // get the Entity for the logged in user
        Entity entity = ResourceUtil.getSLIPrincipalFromSecurityContext().getEntity();
        if (entity != null && !entity.getEntityId().equals("-133")) {
            EntityDefinition entityDefinition = this.entityDefs.lookupByEntityType(entity.getType());
            pair = Pair.of(entity.getEntityId(), entityDefinition);
        }

        return pair;
    }
}
