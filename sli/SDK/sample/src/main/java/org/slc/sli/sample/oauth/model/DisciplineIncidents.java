package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.client.constants.ResourceNames;

/**
 * Sample domain wrapper.
 */
public class DisciplineIncidents {
    
    private static final Logger LOG = LoggerFactory.getLogger(DisciplineIncidents.class);
    
    public static Map<String, String> getInfo(BasicClient client) throws IOException {
        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.read(collection, ResourceNames.DISCIPLINE_INCIDENTS, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        Map<String, String> toReturn = new HashMap<String, String>();
        for (Entity disciplineIncident : collection) {
            String id = (String) disciplineIncident.getData().get("incidentIdentifier");
            String time = (String) disciplineIncident.getData().get("incidentTime");
            toReturn.put(id, "date " + ", " + time);
        }
        
        return toReturn;
    }
    
}
