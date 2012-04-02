package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
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
public class DisciplineIncidents {
    
    private static final Logger LOG = LoggerFactory.getLogger(DisciplineIncidents.class);
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getInfo(BasicClient client) throws IOException {
        EntityCollection collection = new EntityCollection();
        try {
            client.read(collection, EntityType.DISCIPLINE_INCIDENTS, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        Map<String, String> toReturn = new HashMap<String, String>();
        for (Entity disciplineIncident : collection) {
            String id = (String) disciplineIncident.getData().get("incidentIdentifier");
            String date = (String) disciplineIncident.getData().get("incidentDate");
            String time = (String) disciplineIncident.getData().get("incidentTime");
            toReturn.put(id, "date " + ", " + time);
        }

        return toReturn;
    }
    
}
