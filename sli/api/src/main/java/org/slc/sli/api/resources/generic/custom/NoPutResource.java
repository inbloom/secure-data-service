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
package org.slc.sli.api.resources.generic.custom;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.DefaultResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author ben morgan
 *
 * This endpoint disallows PUT for entities mapped to it and returns a 405
 */
@Component
@Scope("request")
@Path("/attendances")
public class NoPutResource extends DefaultResource {

    @Override
    @PUT
    @Path("{id}")
    public Response put(@PathParam("id") final String id,
                        final EntityBody entityBody,
                        @Context final UriInfo uriInfo) {

        return Response.status(405).build();

    }

}
