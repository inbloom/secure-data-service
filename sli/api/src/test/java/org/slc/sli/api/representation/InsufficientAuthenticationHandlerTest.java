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

package org.slc.sli.api.representation;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Tests the insufficient authentication handler.
 * 
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public class InsufficientAuthenticationHandlerTest {

    private InsufficientAuthenticationHandler handler;
    
    @Test
    public void checkResponse() throws Exception {
        SecurityEventBuilder mockSecurityEventBuilder = Mockito.mock(SecurityEventBuilder.class);

        handler = new InsufficientAuthenticationHandler();
        handler.setSecurityEventBuilder(mockSecurityEventBuilder);

        SecurityEvent secEvt = new SecurityEvent();

        Mockito.when(mockSecurityEventBuilder.createSecurityEvent(Mockito.anyString(), Mockito.any(URI.class), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(secEvt);

        HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(headers.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        
        //  DONE BY WHAT IS CALLED AN "EXPERT"
        //  DO NOT TRY THIS AT HOME
        Field f = handler.getClass().getDeclaredField("headers");
        f.setAccessible(true);
        f.set(handler, headers);

        Response resp = handler.toResponse(new InsufficientAuthenticationException("Invalid Token"));
        assertTrue(resp != null);
        Object entity = resp.getEntity();
        
        // No exception has been thrown.
        assertTrue(entity != null);
        
    }
}
