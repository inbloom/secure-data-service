package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample domain wrapper.
 */
public class Cohorts {
    
    private static final Logger LOG = LoggerFactory.getLogger(Cohorts.class);
    
    public static Map<String, String> getIdDesc(BasicClient client) throws IOException {
        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.read(collection, ResourceNames.COHORTS, BasicQuery.EMPTY_QUERY);
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
