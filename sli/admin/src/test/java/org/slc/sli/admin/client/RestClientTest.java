package org.slc.sli.admin.client;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import com.google.gson.JsonArray;

/**
 * 
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/commonCtx.xml" })
public class RestClientTest {
    
    @Value("${roles.json}")
    private String rolesJSON;
    
    @Autowired
    protected RESTClient restClient;
    
    @Test
    public void testGetRoles() {
        RestOperations restCall = Mockito.mock(RestOperations.class);
        Mockito.when(restCall.getForObject(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(rolesJSON);
        restClient.template = restCall;
        JsonArray roles = restClient.getRoles("testtoken");
        Assert.assertNotNull("Making sure getRoles returned JsonArray", roles);
    }
    
    @Test
    public void testGetRolesBadResponse() {
        RestOperations restCall = Mockito.mock(RestOperations.class);
        Mockito.when(restCall.getForObject(Mockito.anyString(), Mockito.any(Class.class))).thenReturn("{badjson}");
        restClient.template = restCall;
        JsonArray roles = restClient.getRoles("testtoken");
        Assert.assertNull("Making sure getRoles returned null for bad JSON", roles);
    }

}
