package org.slc.sli.api.resources.url;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.client.constants.ResourceConstants;
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
    private static final Logger LOG = LoggerFactory.getLogger(AggregateURLCreator.class);

    /**
     * Returns a list of aggregate links that matches the parameters passed in
     */
    @Override
    public List<EmbeddedLink> getUrls(final UriInfo uriInfo, String userId, String userType, NeutralQuery neutralQuery) {

        List<EmbeddedLink> urls = new ArrayList<EmbeddedLink>();

        // iterate through the aggregations and build the embedded links list
        for (Entity e : repo.findAll(ResourceConstants.ENTITY_TYPE_AGGREGATION, neutralQuery)) {
            urls.add(new EmbeddedLink(ResourceConstants.LINKS, e.getType(), ResourceUtil.getURI(uriInfo,
                    ResourceConstants.ENTITY_EXPOSE_TYPE_AGGREGATIONS, e.getEntityId()).toString()));
        }

        return urls;
    }
}
