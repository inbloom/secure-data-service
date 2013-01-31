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

package org.slc.sli.api.resources.generic;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.generic.util.ChangedUriInfo;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;

/**
 * Ok, copy and paste inheritance, but the four and three part handlers are riddled with anonymous
 * classes and call-back driven design, none of which is re-usable Using handlers directly would
 * impose considerable overhead
 *
 * @author dkornishev
 */
@Component
@Scope("request")
public class SixPartResource extends GenericResource {

    @Autowired
    private FourPartResource four;

    @GET
    public Response get(@Context UriInfo uriInfo) {
        String ids = locateIds(uriInfo, ResourceTemplate.SIX_PART);
        List<String> segments = extractSegments(uriInfo.getPathSegments(), Arrays.asList(0, 4, 5, 6));
        segments.add(2, ids);
        String newUri = String.format("/rest/%s/%s/%s/%s/%s", segments.toArray());
        String queryString = getEncodedQueryParameters(uriInfo.getRequestUri().getQuery());
        info("Executing: " + newUri + queryString);
        Response res = four.get(new ChangedUriInfo(newUri + queryString, uriInfo), ids);
        return res;
    }
}
