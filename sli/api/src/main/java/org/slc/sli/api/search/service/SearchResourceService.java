package org.slc.sli.api.search.service;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class SearchResourceService {

    @Autowired
    private ResourceHelper resourceHelper;

    public ServiceResponse getSearchHits(Resource resource, URI queryUri) {

        // build neutral query
        NeutralQuery neutralQuery = new NeutralQuery();

        // add query criteria
        String queryString = queryUri.getQuery();
        for (String criteriaString : queryString.split("&")) {

            NeutralCriteria criteria = new NeutralCriteria(criteriaString);
            if (criteria.getKey().equals("limit")) {
                neutralQuery.setLimit(new Integer(criteria.getValue().toString()));
            } else if (criteria.getKey().equals("offset")) {
                neutralQuery.setOffset(new Integer(criteria.getValue().toString()));
            } else {
                neutralQuery.addCriteria(criteria);
            }
        }

        // Call BasicService to query the elastic search repo
        final EntityDefinition definition = resourceHelper.getEntityDefinition(resource);
        Iterable<EntityBody> entityBodies = definition.getService().list(neutralQuery);

        // return results
        List<EntityBody> retList = (List<EntityBody>) entityBodies;
        return new ServiceResponse(retList, retList.size());
    }

}
