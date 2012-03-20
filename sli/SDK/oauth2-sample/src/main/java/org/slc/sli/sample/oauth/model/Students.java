package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.Query;
import org.slc.sli.api.client.impl.BasicClient;

public class Students {
    
    public static List<String> getNames(BasicClient client) throws IOException {
        EntityCollection entities = new EntityCollection();
        try {
            client.read(entities, EntityType.STUDENTS, new Query() {
                
                @Override
                public Map<String, Object> getParameters() {
                    return new HashMap<String, Object>();
                }
                
                @Override
                public boolean targets() {
                    return false;
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> toReturn = new ArrayList<String>();
        for (Entity e : entities) {
            @SuppressWarnings("unchecked")
            Map<String, ?> name = (Map<String, ?>) e.getData().get("name");
            toReturn.add(name.get("firstName") + " " + name.get("lastSurname"));
        }
        return toReturn;
    }
    
    public static int getGrade(BasicClient client, String studentName) {
        Random r = new Random();
        return r.nextInt(100);
    }
    
}
