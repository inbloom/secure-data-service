/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dashboard.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Any;
import org.scribe.exceptions.OAuthException;
import org.slc.sli.dashboard.client.APIClient;
import org.slc.sli.dashboard.client.RESTClient;
import org.slc.sli.dashboard.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author ychen
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })

public class SLIAuthenticationEntryPointTest {


    @Autowired
    ApplicationContext applicationContext;


    private static HttpServletRequest request;
    private static HttpServletResponse response;

    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);

    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testIsSecuredRequest() throws Exception {
        //Test all four scenarios
        LOG.debug("[SLCAuthenticationEntryPointTest]Secure Protocol with local environment, return FALSE");
        when(request.getServerName()).thenReturn("local.slidev.org");
        when(request.isSecure()).thenReturn(true);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with local environment, return FALSE");
        when(request.getServerName()).thenReturn("local.slidev.org");
        when(request.isSecure()).thenReturn(false);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with non-local environment, return FALSE");
        when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
        when(request.isSecure()).thenReturn(false);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with local environment, return FALSE");
        when(request.getServerName()).thenReturn("local.slidev.org");
        when(request.isSecure()).thenReturn(false);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with non-local environment, return FALSE");
        when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
        when(request.isSecure()).thenReturn(false);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Secure Protocol with non-local environment, return TRUE");
        when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
        when(request.isSecure()).thenReturn(true);
        assertTrue(SLIAuthenticationEntryPoint.isSecureRequest(request));
   }
    @Test
    public void testCommenceException() throws Exception {
        HttpSession mockSession = mock(HttpSession.class);
        AuthenticationException mockAuthenticationException = mock(AuthenticationException.class);
        PropertiesDecryptor mockPropertiesDecryptor = mock(PropertiesDecryptor.class);
        APIClient mockAPIClient = mock(APIClient.class);
        RESTClient mockRESTClient = mock(RESTClient.class);
        when(request.getSession()).thenReturn(mockSession);
        when(mockPropertiesDecryptor.getDecryptedClientId()).thenReturn("mock-client-id");
        when(mockPropertiesDecryptor.getDecryptedClientSecret()).thenReturn("mock-client-secret");

        SLIAuthenticationEntryPoint sliAuthenticationEntryPoint = new SLIAuthenticationEntryPoint();
        sliAuthenticationEntryPoint.setPropDecryptor(mockPropertiesDecryptor);
        sliAuthenticationEntryPoint.setApiClient(mockAPIClient);
        sliAuthenticationEntryPoint.setRestClient(mockRESTClient);
        sliAuthenticationEntryPoint.setApiUrl("/api/mock/uri");
        sliAuthenticationEntryPoint.setCallbackUrl("http://www.local.slidev.org:8080/api/rest/mock");
        sliAuthenticationEntryPoint.commence(request,response,mockAuthenticationException);

    }
    @Test
     public void testCommence() throws Exception {
        HttpSession mockSession = mock(HttpSession.class);
        AuthenticationException mockAuthenticationException = mock(AuthenticationException.class);
        PropertiesDecryptor mockPropertiesDecryptor = mock(PropertiesDecryptor.class);
        APIClient mockAPIClient = mock(APIClient.class);
        RESTClient mockRESTClient = mock(RESTClient.class);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject)parser.parse("{\"authenticated\":true,\"edOrg\":null,\"edOrgId\":null,\"email\":\"dummy@fake.com\",\"external_id\":\"cgray\",\"full_name\":\"Mr Charles Gray\",\"granted_authorities\":[\"Destroyme\"],\"isAdminUser\":false,\"realm\":\"2013uz-d5ae70c9-55ef-11e2-b6be-68a86d3c2fde\",\"rights\":[\"READ_PUBLIC\",\"READ_GENERAL\",\"AGGREGATE_READ\"],\"sliRoles\":[\"Destroyme\"],\"tenantId\":\"wolverineil.wgen@gmail.com\",\"userType\":\"teacher\",\"user_id\":\"cgray@wolverineil.wgen@gmail.com\"}");

        when(request.getSession()).thenReturn(mockSession);
        when(request.getRemoteAddr()).thenReturn("http://local.slidev.org:8080");
        when(request.getServerName()).thenReturn("mock.slidev.org");
        when(request.isSecure()).thenReturn(true);
        when(mockPropertiesDecryptor.getDecryptedClientId()).thenReturn("mock-client-id");
        when(mockPropertiesDecryptor.getDecryptedClientSecret()).thenReturn("mock-client-secret");
        when(mockPropertiesDecryptor.encrypt(anyString())).thenReturn("MOCK-ENCRYPTED-TOKEN");
        when(mockSession.getAttribute(anyString())).thenReturn("Mock-OAUTH-TOKEN");
        when(mockRESTClient.sessionCheck(anyString())).thenReturn(jsonObject);


        SLIAuthenticationEntryPoint sliAuthenticationEntryPoint = new SLIAuthenticationEntryPoint();
        sliAuthenticationEntryPoint.setPropDecryptor(mockPropertiesDecryptor);
        sliAuthenticationEntryPoint.setApiClient(mockAPIClient);
        sliAuthenticationEntryPoint.setRestClient(mockRESTClient);
        sliAuthenticationEntryPoint.setApiUrl("/api/mock/uri");
        sliAuthenticationEntryPoint.setCallbackUrl("http://www.local.slidev.org:8080/api/rest/mock");
        sliAuthenticationEntryPoint.commence(request,response,mockAuthenticationException);

    }

}
