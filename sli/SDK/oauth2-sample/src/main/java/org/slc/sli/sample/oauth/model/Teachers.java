package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample domain wrapper.
 */
public class Teachers {
    
    private static final Logger LOG = LoggerFactory.getLogger(Teachers.class);
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getTenantIdMap(BasicClient client) throws IOException {
        EntityCollection collection = new EntityCollection();
        try {
            client.read(collection, EntityType.TEACHERS, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        Map<String, String> toReturn = new HashMap<String, String>();
        for (Entity teacher : collection) {
            String firstName = (String) ((Map<String, Object>) teacher.getData().get("name")).get("firstName");
            String lastName = (String) ((Map<String, Object>) teacher.getData().get("name")).get("lastSurname");
            String tenantId = (String) ((Map<String, Object>) teacher.getData().get("metaData")).get("tenantId");
            toReturn.put(firstName + " " + lastName, tenantId);
        }
        return toReturn;
    }
}
