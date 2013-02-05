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


package org.slc.sli.dashboard.web.unit.interceptor;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slc.sli.dashboard.client.RESTClient;
import org.slc.sli.dashboard.security.SLIAuthenticationEntryPoint;
import org.slc.sli.dashboard.util.SecurityUtil;
import org.slc.sli.dashboard.web.interceptor.SessionCheckInterceptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import com.google.gson.JsonObject;


/**
 * 
 * @author svankina
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SecurityUtil.class })
@ContextConfiguration(locations = { "/application-context.xml" })
public class SessionCheckInterceptorTest {

    @Test
    public void testPreHandle() throws Exception {
        SessionCheckInterceptor scInterceptor = new SessionCheckInterceptor();
        PowerMockito.mockStatic(SecurityUtil.class);
        PowerMockito.when(SecurityUtil.getToken()).thenReturn("sravan");
        MockHttpServletRequest request = new MockHttpServletRequest() {
            public HttpSession getSession() {
                return new MockHttpSession();
            }
            
            public Cookie[] getCookies() {
                Cookie c = new Cookie(SLIAuthenticationEntryPoint.DASHBOARD_COOKIE, "fake");
                return new Cookie[]{c};
            }
            
            public String getRequestURI() {
                return "fake_uri";
            }
            
        };
        
        MockHttpServletResponse response = new MockHttpServletResponse() {
            
            public void sendRedirect(String url) {
                assertEquals(url, "fake_uri");
            }
        };
        
        
        RESTClient restClient = new RESTClient() {
            public JsonObject sessionCheck(String token) {
                JsonObject json = new JsonObject();
                json.addProperty("authenticated", false);
                return json;
            }
        };
        
        scInterceptor.setRestClient(restClient);
        
        assertEquals(scInterceptor.preHandle(request, response, null), false);
    }
    
    
}
