package org.slc.sli.api.client.sample;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;

/**
 * A sample application that utilizes the SLI SDK to connect to a SLI API ReSTful server,
 * logs the user into their IDP realm, and executes a query against the API.
 * 
 * @author asaarela
 */
public class SliQueryTool {
    
    /**
     * @param args
     */
    public static void main(final String[] args) throws MalformedURLException, URISyntaxException {
        
        if (args.length != 2) {
            System.err.println("Usage: SliQueryTool <session token id> <teacher id>");
            return;
        }
        
        // for now, just hard code everything
        
        // Connect to local host.
        SLIClient client = BasicClient.Builder.create().user("Demo").password(args[0]).build();
        
        EntityCollection collection = client.read(EntityType.TEACHERS, args[1], BasicQuery.EMPTY_QUERY);
        
        for (int i = 0; i < collection.size(); ++i) {
            
            Entity entityEntry = collection.get(i);
            System.err.println("EntityType:" + entityEntry.getEntityType().getURL());
            
            for (Map.Entry<String, Link> linkEntry : entityEntry.getLinks().entrySet()) {
                System.err.println("\t" + linkEntry.getKey() + ":" + linkEntry.getValue().getResourceURL());
            }
            
            printMapOfMaps(entityEntry.getData());
        }
    }
    
    private static void printMapOfMaps(final Map<String, Object> map) {
        
        System.err.print("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.err.print(entry.getKey());
            
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tmp = (Map<String, Object>) obj;
                printMapOfMaps(tmp);
            }
            System.err.print(obj.toString());
        }
        System.err.println("}");
    }
}
