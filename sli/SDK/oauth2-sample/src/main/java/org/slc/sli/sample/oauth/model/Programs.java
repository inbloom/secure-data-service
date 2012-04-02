package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
public class Programs {
    
    private static final Logger LOG = LoggerFactory.getLogger(Programs.class);
    
    @SuppressWarnings("unchecked")
    public static List<String> getIds(BasicClient client) throws IOException {
        EntityCollection collection = new EntityCollection();
        try {
            client.read(collection, EntityType.PROGRAMS, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        List<String> toReturn = new ArrayList<String>();
        for (Entity program : collection) {
            String id = (String) program.getData().get("programId");
            toReturn.add(id);
        }

        return toReturn;
    }
    
}
