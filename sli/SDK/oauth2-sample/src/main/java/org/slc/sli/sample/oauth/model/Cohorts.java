package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.common.constants.ResourceNames;

/**
 * Sample domain wrapper.
 */
public class Cohorts {

    private static final Logger LOG = LoggerFactory.getLogger(Cohorts.class);

    public static Map<String, String> getIdDesc(BasicClient client) throws IOException {
        EntityCollection collection = new EntityCollection();
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
