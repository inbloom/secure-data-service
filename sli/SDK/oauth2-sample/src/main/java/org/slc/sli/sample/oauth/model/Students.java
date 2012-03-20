package org.slc.sli.sample.oauth.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slc.sli.api.client.impl.BasicClient;

/**
 * Sample domain wrapper.
 */
public class Students {
    
    @SuppressWarnings("javadoc")
    public static List<String> getNames(final BasicClient client) {
        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.add("Bob Marley");
        toReturn.add("Willy Wonka");
        toReturn.add("Homer Simpson");
        toReturn.add("Juan Valdez");
        toReturn.add("Gary Coleman");
        toReturn.add("Mick Jagger");
        toReturn.add("Austin Powers");
        toReturn.add("Peter Pan");
        return toReturn;
    }
    
    @SuppressWarnings("javadoc")
    public static int getGrade(BasicClient client, String studentName) {
        Random r = new Random();
        return r.nextInt(100);
    }
    
}
