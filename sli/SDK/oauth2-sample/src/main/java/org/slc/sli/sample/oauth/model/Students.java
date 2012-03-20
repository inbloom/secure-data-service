package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.Query;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Students {
    private static final Logger LOG = LoggerFactory.getLogger(Students.class);

    @SuppressWarnings("unchecked")
    public static List<String> getNames(BasicClient client) throws IOException {
        EntityCollection collection = new EntityCollection();
        try {
            client.read(collection, EntityType.STUDENTS, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred",e);
        }
        ArrayList<String> toReturn = new ArrayList<String>();
        for(Entity student: collection){
            String firstName = (String) ((Map<String, Object>) student.getData().get("name")).get("firstName");
            String lastName = (String) ((Map<String, Object>) student.getData().get("name")).get("lastSurname");
            toReturn.add(student.getData().get("name").toString());
        }
        return toReturn;
    }
    
    public static int getGrade(BasicClient client, String studentName) {
        Random r = new Random();
        return r.nextInt(100);
    }

}
