package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.client.constants.ResourceNames;

/**
 * Sample domain wrapper.
 */
public class Programs {
    
    private static final Logger LOG = LoggerFactory.getLogger(Programs.class);
    
    public static List<String> getIds(BasicClient client) throws IOException {
        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.read(collection, ResourceNames.PROGRAMS, BasicQuery.EMPTY_QUERY);
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
