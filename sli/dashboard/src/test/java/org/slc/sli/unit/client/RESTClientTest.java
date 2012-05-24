package org.slc.sli.unit.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.client.RESTClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

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
            
            public HttpEntity<String> exchange(RestTemplate template, String url, HttpMethod method, HttpEntity entity, Class cl) {
                return new HttpEntity<String>("fakeResponse");
            }            
        };


        String s = client.makeJsonRequestWHeaders("http://www.google.com", "fakeToken");
        assertEquals(s, "fakeResponse");
    }

    @Test
    public void testPutJsonRequestWHeaders() {
        
        String path = "http://local.slidev.org";
        String token = "token";
        String id = "id";
        String json = "{" + "\"component_1\": " + "{" + "\"id\" : \"component_1\", " + "\"name\" : \"Component 1\", "
                + "\"type\" : \"LAYOUT\", " + "\"items\": ["
                + "{\"id\" : \"component_1_1\", \"name\": \"First Child Component\", \"type\": \"PANEL\"}, "
                + "{\"id\" : \"component_1_2\", \"name\": \"Second Child Component\", \"type\": \"PANEL\"}" + "]" + "}"
                + "}";
        
        RESTClient client = new RESTClient();
        RestTemplate mockTemplate = mock(RestTemplate.class);
        client.setTemplate(mockTemplate);
        RestTemplateAnswer restTemplateAnswer = new RestTemplateAnswer();
        Mockito.doAnswer(restTemplateAnswer).when(mockTemplate).put(Mockito.anyString(), Mockito.anyObject());
        client.putJsonRequestWHeaders(path, token, json);
        String customJson = restTemplateAnswer.getJson();
        
        assertNotNull(customJson);
        assertEquals(json, customJson);
        
    }
    
    private static class RestTemplateAnswer implements Answer {
        
        private String json;
        
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            HttpEntity httpEntity = (HttpEntity) invocation.getArguments()[1];
            json = (String) httpEntity.getBody();
            return null;
        }
        
        public String getJson() {
            return json;
        }
    }
    
}
