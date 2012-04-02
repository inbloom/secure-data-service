package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;

/**
 * Sample domain wrapper.
 */
public class Cohorts {
    
    private static final Logger LOG = LoggerFactory.getLogger(Cohorts.class);
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getIdDesc(BasicClient client) throws IOException {
        EntityCollection collection = new EntityCollection();
        try {
            client.read(collection, EntityType.COHORTS, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        HashMap<String, String> toReturn = new HashMap<String, String>();
        for (Entity cohort : collection) {
            String id = (String) cohort.getData().get("cohortIdentifier");
            String desc = (String) cohort.getData().get("cohortDescription");
            toReturn.put(id, desc);
        }

        return toReturn;
    }
    
}
