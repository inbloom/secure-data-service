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


package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder;
import org.slc.sli.sandbox.idp.saml.SamlRequestDecoder.SamlRequest;
import org.slc.sli.sandbox.idp.service.AuthRequestService.Request;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthRequestServiceTest {
    
    @Mock
    SamlRequestDecoder samlDecoder;
    
    @InjectMocks
    AuthRequestService authService = new AuthRequestService();
    
    @Test
    public void testHappy() {
        SamlRequest request = Mockito.mock(SamlRequest.class);
        Mockito.when(request.getId()).thenReturn("id");
        Mockito.when(request.getIdpDestination()).thenReturn("http://destination/sp?realm=myrealm");
        
        Mockito.when(samlDecoder.decode("samlRequest")).thenReturn(request);
        
        Request processed = authService.processRequest("samlRequest", "myrealm", null);
        
        Mockito.verify(samlDecoder).decode("samlRequest");
        
        assertEquals("id", processed.getRequestId());
        assertEquals("myrealm", processed.getRealm());
        
        assertEquals(null, authService.processRequest(null, null, null));
    }
    
    @Test
    public void testForceAuthn() {
        SamlRequest request = Mockito.mock(SamlRequest.class);
        Mockito.when(request.getId()).thenReturn("id");
        Mockito.when(request.getIdpDestination()).thenReturn("http://destination/sp?realm=myrealm");
        Mockito.when(request.isForceAuthn()).thenReturn(true);
        Mockito.when(samlDecoder.decode("samlRequest")).thenReturn(request);
        
        Request processed = authService.processRequest("samlRequest", "myrealm", null);
        
        Mockito.verify(samlDecoder).decode("samlRequest");
        
        assertEquals("id", processed.getRequestId());
        assertEquals("myrealm", processed.getRealm());
        assertEquals(true, processed.isForceAuthn());
        assertEquals(null, authService.processRequest(null, null, null));
    }
}
