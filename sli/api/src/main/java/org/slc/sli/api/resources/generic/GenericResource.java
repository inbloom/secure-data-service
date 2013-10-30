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
package org.slc.sli.api.resources.generic;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.response.DefaultResponseBuilder;
import org.slc.sli.api.resources.generic.response.GetAllResponseBuilder;
import org.slc.sli.api.resources.generic.response.GetResponseBuilder;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.HypermediaType;

/**
 * Base resource class for all dynamic end points
 *
 * @author srupasinghe
 * @author jstokes
 * @author pghosh
 *
 */

@Scope("request")
@Consumes({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8",
        MediaType.APPLICATION_XML + ";charset=utf-8", MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON,
        MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8",
        MediaType.APPLICATION_XML + ";charset=utf-8", HypermediaType.VENDOR_SLC_XML + ";charset=utf-8" })
public abstract class GenericResource {

    @Autowired
    @Qualifier("defaultResourceService")
    protected ResourceService resourceService;

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    protected GetResponseBuilder getResponseBuilder;

    @Autowired
    protected GetAllResponseBuilder getAllResponseBuilder;

    @Autowired
    protected DefaultResponseBuilder defaultResponseBuilder;

    /**
     * @author jstokes
     */
    public static interface ResourceLogic {
        public Response run(Resource resource);
    }

    /**
     * @author jstokes
     */
    public static interface GetResourceLogic {
        public ServiceResponse run(Resource resource);
    }

    /**
     * Runs the first four path segments as a separate call to fetch ids to
     * inject into Subsequent call for URI rewrites
     *
     * @param uriInfo
     * @param template
     * @return
     */
    protected String locateIds(UriInfo uriInfo, ResourceTemplate template) {
        String id = uriInfo.getPathSegments().get(2).getPath();
        Resource resource = resourceHelper.getResourceName(uriInfo, template);
        Resource base = resourceHelper.getBaseName(uriInfo, template);
        Resource association = resourceHelper.getAssociationName(uriInfo, template);

        ServiceResponse resp = resourceService.getEntities(base, id, association, resource,
                URI.create(uriInfo.getRequestUri().getPath() + "?limit=0"));

        StringBuilder ids = new StringBuilder();

        for (Iterator<EntityBody> i = resp.getEntityBodyList().iterator(); i.hasNext();) {
            EntityBody eb = i.next();
            ids.append(eb.get("id"));
            if (i.hasNext()) {
                ids.append(",");
            }
        }

        return ids.toString();
    }

    protected List<String> extractSegments(List<PathSegment> segments, List<Integer> indices) {

        List<String> result = new ArrayList<String>();

        for (int i : indices) {
            result.add(segments.get(i).getPath());
        }

        return result;
    }

    /**
     * Encodes the decoded query parameter string.
     *
     * @param decoded
     *            String representing decoded query parameters.
     * @return String representing encoded query parameters.
     */
    protected String getEncodedQueryParameters(String decoded) {
        String queryString = null;
        if (decoded == null) {
            queryString = "";
        } else {
            try {
                queryString = "?" + URLEncoder.encode(decoded, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                error("unsupported encoding exception when parsing query parameters: {}", e);
            }
        }
        return queryString;
    }

    protected void setResourceService(final ResourceService service) {
        this.resourceService = service;
    }
}
