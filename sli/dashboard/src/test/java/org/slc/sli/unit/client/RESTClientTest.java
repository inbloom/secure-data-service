package org.slc.sli.unit.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slc.sli.client.RESTClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author svankina
 *
 */
public class RESTClientTest {

    
    @Test
    public void testSessionCheck() {
        final String jsonText = "{\"name\": \"something\"}";
        RESTClient client = new RESTClient() {
            public String makeJsonRequestWHeaders(String path, String token) {
                return jsonText;
            }
        };
        
        JsonParser parser = new JsonParser();
        JsonObject obj2 = client.sessionCheck(null);
        JsonObject obj1 = parser.parse(jsonText).getAsJsonObject();
        assertEquals(obj2, obj1);
    }
    
    @Test
    public void testMakeJsonRequestWHeaders() {
        //TODO: Change the overridden function to support HTTPEntity
        RESTClient client = new RESTClient() {
            
            public HttpEntity<String> exchange(String url, HttpMethod method, HttpEntity entity, Class cl) {
                return new HttpEntity<String>("fakeResponse");
            }            
        };


        String s = client.makeJsonRequestWHeaders("http://www.google.com", "fakeToken");
        assertEquals(s, "fakeResponse");
    }
}
