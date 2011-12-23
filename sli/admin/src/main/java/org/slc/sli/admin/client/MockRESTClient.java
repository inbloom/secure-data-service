package org.slc.sli.admin.client;

import java.io.FileReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * 
 * @author pwolf
 *
 */
public class MockRESTClient implements RESTClient {
    
    public JsonArray getRoles(String token) {
        JsonParser parser = new JsonParser();
        if (token == null) {
            token = "default";
        }
        String filename = "src/test/resources/mock_data/" + token + "/roles.json";
        try {
            return (JsonArray) parser.parse(new FileReader(filename));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
