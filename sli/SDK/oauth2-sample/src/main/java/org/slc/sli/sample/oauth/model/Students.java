package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slc.sli.api.client.impl.BasicClient;

public class Students {

    public static List<String> getNames(BasicClient client) throws IOException {
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
    
    public static int getGrade(BasicClient client, String studentName) {
        Random r = new Random();
        return r.nextInt(100);
    }

}
