package org.slc.sli.unit.client;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.client.SDKAPIClient;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.ConfigMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for the Live API client.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class SDKAPIClientTest {

    private static final String CUSTOM_CONFIG_JSON = "{config:{" + "\"component_1\": " + "{" 
            + "\"id\" : \"component_1\", " 
            + "\"name\" : \"Component 1\", "
            + "\"type\" : \"LAYOUT\", " + "\"items\": ["
            + "{\"id\" : \"component_1_1\", \"name\": \"First Child Component\", \"type\": \"PANEL\"}, "
            + "{\"id\" : \"component_1_2\", \"name\": \"Second Child Component\", \"type\": \"PANEL\"}" + "]" + "}"
            + "}}";    

    private SDKAPIClient client;
    
    private SLIClient mockSdk;

    @Value("${api.server.url}")
    private String apiUrl;

    @Before
    public void setUp() throws Exception {
        client = new SDKAPIClient();
        mockSdk = mock(BasicClient.class);        
        client.setSdkClient(mockSdk);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        mockSdk = null;
    }

    @Test
    public void testGetEdOrgCustomData() throws Exception {

        String token = "token";
        String id = "id";
        String componentId = "component_1";
        String componentName = "Component 1";
        String componentType = "LAYOUT";
        
        SdkClientReadAnswer sdkClientReadAnswer = new SdkClientReadAnswer(CUSTOM_CONFIG_JSON);
        Mockito.doAnswer(sdkClientReadAnswer).when(mockSdk)
                .read(Mockito.anyString(), Mockito.any(List.class), Mockito.anyString(), Mockito.any(Class.class));
        
        ConfigMap configMap = client.getEdOrgCustomData(token, id);
        
        assertNotNull(configMap);
        assertEquals(1, configMap.size());
        assertEquals(componentId, configMap.getComponentConfig(componentId).getId());
        assertEquals(componentName, configMap.getComponentConfig(componentId).getName());
        assertEquals(componentType, configMap.getComponentConfig(componentId).getType().name());

    }

    @Test
    public void testPutEdOrgCustomData() throws Exception {

        String token = "token";
        String id = "id";
        String componentId = "component_1";
        String componentName = "Component 1";
        String componentType = "LAYOUT";

        Gson gson = new GsonBuilder().create();
        ConfigMap configMap = gson.fromJson(CUSTOM_CONFIG_JSON, ConfigMap.class);
        
        SdkClientCreateAnswer sdkClientCreateAnswer = new SdkClientCreateAnswer();
        Mockito.doAnswer(sdkClientCreateAnswer).when(mockSdk)
                .create(Mockito.anyString(), Mockito.anyString(), Mockito.any(Entity.class));
        
        client.putEdOrgCustomData(token, id, configMap);
        
        ConfigMap verifyConfigMap = sdkClientCreateAnswer.getConfigMap();

        assertNotNull(verifyConfigMap);
        assertEquals(1, verifyConfigMap.size());
        assertEquals(componentId, verifyConfigMap.getComponentConfig(componentId).getId());
        assertEquals(componentName, verifyConfigMap.getComponentConfig(componentId).getName());
        assertEquals(componentType, verifyConfigMap.getComponentConfig(componentId).getType().name());

    }

    private static class SdkClientReadAnswer implements Answer {

        private String json;
        private ConfigMap configMap;
        private List entityList;

        public SdkClientReadAnswer(String json) {
            this.json = json;
            Gson gson = new GsonBuilder().create();
            this.configMap = gson.fromJson(getJson(), ConfigMap.class);            
        }
        
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            entityList = (List) invocation.getArguments()[1];
            
            entityList.add(getConfigMap());
            
            return null;
        }
        
        public String getJson() {
            return json;
        }
        
        public ConfigMap getConfigMap() {            
            return configMap;
        }

        public List getEntityList() {
            return entityList;
        }
    }

    private static class SdkClientCreateAnswer implements Answer {

        private ConfigMap configMap;

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            Entity configMapEntity = (Entity) invocation.getArguments()[2];
            Map<String, Config> config = (Map<String, Config>) configMapEntity.getData().get("config");
            this.configMap = new ConfigMap();
            this.configMap.setConfig(config);
            return null;
        }

        public ConfigMap getConfigMap() {            
            return configMap;
        }

    }


}
