package org.slc.sli.client;

import org.slc.sli.entity.School;

public class LiveAPIClient implements APIClient {

    @Override
    public School[] getSchools(String token) {
        System.err.println("Not implemented");
        return null;
    }
}
