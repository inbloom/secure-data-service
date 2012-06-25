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


package org.slc.sli.api.resources.url;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.client.constants.ResourceConstants;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;


/**
 * Creates URL sets for aggregations.
 * Return URL format /aggregations/{JUUID}
 *
 * @author srupasinghe
 *
 */
public class AggregateURLCreator extends URLCreator {

    /**
     * Returns a list of aggregate links that matches the parameters passed in
     */
    @Override
    public List<EmbeddedLink> getUrls(final UriInfo uriInfo, String userId, String userType, NeutralQuery neutralQuery) {

        List<EmbeddedLink> urls = new ArrayList<EmbeddedLink>();

        // iterate through the aggregations and build the embedded links list
//        BasicService.addDefaultQueryParams(neutralQuery, ResourceConstants.ENTITY_TYPE_AGGREGATION);
        for (Entity e : repo.findAll(ResourceConstants.ENTITY_TYPE_AGGREGATION, neutralQuery)) {
            urls.add(new EmbeddedLink(ResourceConstants.LINKS, e.getType(), ResourceUtil.getURI(uriInfo,
                    ResourceConstants.ENTITY_EXPOSE_TYPE_AGGREGATIONS, e.getEntityId()).toString()));
        }

        return urls;
    }
}
