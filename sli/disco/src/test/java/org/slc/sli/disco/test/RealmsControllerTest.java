package org.slc.sli.disco.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.disco.test.bootstrap.WebContextTestExecutionListener;
import org.slc.sli.disco.web.controllers.RealmsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/commonCtx.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class RealmsControllerTest {
    
    private static final String VALID_STATE    = "NC";
    private static final String VALID_REALM_ID = "Wladyslaw IV Wasa";
    private static final String VALID_SSO_INIT = "http://valid.sso.init";
    private static final String RELAY_STATE    = "http://return.me.here";
    
    @Autowired
    private RealmsController    controller;
    
    @Autowired
    @Value("${sli.security.realms.list.url}")
    private String              listUrl;
    
    @Autowired
    @Value("${sli.security.realms.ssoInit.url}")
    private String              ssoInitUrl;
    
    private RestTemplate        rest;
    
    @Before
    public void init() {
        this.rest = Mockito.mock(RestTemplate.class);
        
        ResponseEntity<String> value = new ResponseEntity<String>("[{\"id\":\"" + VALID_REALM_ID + "\",\"state\":\"" + VALID_STATE + "\"}]", HttpStatus.OK);
        Mockito.when(this.rest.getForEntity(listUrl, String.class)).thenReturn(value);
        
        ResponseEntity<String> ssoValue = new ResponseEntity<String>(VALID_SSO_INIT, HttpStatus.OK);
        Mockito.when(this.rest.getForEntity(this.ssoInitUrl, String.class, VALID_REALM_ID)).thenReturn(ssoValue);
        
        this.controller.setRest(this.rest);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testList() throws IOException {
        
        final Map<String, Object> values = new HashMap<String, Object>();
        Model model = Mockito.mock(Model.class);
        
        Mockito.when(model.addAttribute(Mockito.anyString(), Mockito.anyString())).thenAnswer(new Answer<Model>() {
            
            @Override
            public Model answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                values.put(args[0].toString(), args[1]);
                return (Model) invocation.getMock();
            }
            
        });
        
        controller.listRealms(RELAY_STATE, model);
        
        Assert.assertEquals("Realy State must match", RELAY_STATE, values.get("relayState"));
        
        Assert.assertTrue("There must be a realm entry", values.containsKey("realms"));
        Map<String, String> realms = (Map<String, String>) values.get("realms");
        Assert.assertEquals("States must equal", VALID_STATE, realms.get(VALID_REALM_ID));
    }
    
    @Test
    public void testSso() throws IOException {
        String resp = controller.ssoInit(VALID_REALM_ID, RELAY_STATE, Mockito.mock(Model.class));
        
        Assert.assertEquals("SSO Init didn't match", String.format("redirect:%s&RelayState=%s", VALID_SSO_INIT, RELAY_STATE), resp);
        
        log("SSO Tested...");
    }
    
    private static void log(Object msg) {
        System.out.println(msg.toString());
    }
    
}
