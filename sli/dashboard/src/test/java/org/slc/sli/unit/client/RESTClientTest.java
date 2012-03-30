package org.slc.sli.unit.client;

import org.junit.Test;
import org.slc.sli.client.RESTClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RESTClientTest {

    
    @Test
    public void testSessionCheck() {
        final String jsonText = "{\"name\": \"something\"}";
        RESTClient client = new RESTClient() {
            public String makeJsonRequestWHeaders(String path, String token, boolean fullEntities) {
                return jsonText;
            }
        };
        
        JsonParser parser = new JsonParser();
        JsonObject obj2 = client.sessionCheck(null);
        JsonObject obj1 = parser.parse(jsonText).getAsJsonObject();
        assert(obj2.equals(obj1));
    }
    
    @Test
    public void testMakeJsonRequestWHeaders() {
        //TODO: Change the overridden function to support HTTPEntity
        RESTClient client = new RESTClient();
        RestTemplate template = new RestTemplate(){
            public <T> T execute(String url,
                    HttpMethod method,
                    RequestCallback requestCallback,
                    ResponseExtractor<T> responseExtractor,
                    Object... urlVariables)
         throws RestClientException {
                return null;
            }
        };
        
        client.setTemplate(template);
        String s = client.makeJsonRequestWHeaders("http://www.google.com", "fakeToken", true);
        assert(s == null);
    }
}
