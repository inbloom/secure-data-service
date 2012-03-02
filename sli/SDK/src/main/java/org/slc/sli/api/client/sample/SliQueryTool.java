package org.slc.sli.api.client.sample;

import java.util.List;
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
    public static void main(final String[] args) throws Exception {
        
        if (args.length != 2) {
            System.err.println("Usage: SliQueryTool <session token id> <teacher id>");
            return;
        }
        
        // for now, just hard code everything
        
        // Connect to local host.
        SLIClient client = BasicClient.Builder.create().user("linda.kim").password(args[0]).build();
        
        EntityCollection collection = client.read(EntityType.STUDENT_SECTION_ASSOCIATIONS, args[1],
                BasicQuery.EMPTY_QUERY);
        
        for (int i = 0; i < collection.size(); ++i) {
            
            Entity entityEntry = collection.get(i);
            System.err.println("EntityType:" + entityEntry.getEntityType().getEntityType());
            
            System.err.println("Links:");
            List<Link> links = entityEntry.getLinks();
            
            if (links != null) {
                for (Link linkEntry : links) {
                    System.err.println("\trel:" + linkEntry.getLinkName() + " href:" + linkEntry.getResourceURL());
                }
            }
            
            System.err.println("\nProperties:");
            printMapOfMaps(entityEntry.getData());
        }
    }
    
    private static void printMapOfMaps(final Map<String, Object> map) {
        
        System.err.print("\n{");
        
        if (map == null) {
            System.err.println("}\n");
            return;
        }
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            
            if (entry.getKey().equals(Entity.LINKS_KEY)) {
                continue;
            }
            System.err.print("\t" + entry.getKey() + "=");
            
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tmp = (Map<String, Object>) obj;
                printMapOfMaps(tmp);
            }
            System.err.print(obj.toString() + "\n ");
        }
        System.err.println("}\n");
    }
}
