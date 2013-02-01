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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.saml.SamlResponseComposer;
import org.slc.sli.sandbox.idp.service.AuthRequestService.Request;
import org.slc.sli.sandbox.idp.service.UserService.User;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class SamlAssertionServiceTest {
    
    @Mock
    SamlResponseComposer samlComposer;
    
    @InjectMocks
    SamlAssertionService service = new SamlAssertionService();
    
    @Test
    public void testWithRealm() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException,
            TransformerException, MarshalException, XMLSignatureException {
        
        service.setIssuerBase("http://local.slidev.org:8082/simple-idp");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        Map<String, String> attributes = new HashMap<String, String>();
        Mockito.when(
                samlComposer.componseResponse("destUri", "http://local.slidev.org:8082/simple-idp?realm=realm",
                        "request_id", "unique_id", attributes, roles)).thenReturn("samlResponse");
        
        Request request = Mockito.mock(AuthRequestService.Request.class);
        Mockito.when(request.getRequestId()).thenReturn("request_id");
        Mockito.when(request.getRealm()).thenReturn("realm");
        Mockito.when(request.getDestination()).thenReturn("destUri");
        
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserId()).thenReturn("unique_id");
        
        service.buildAssertion("unique_id", roles, attributes, request);
        
        Mockito.verify(samlComposer).componseResponse("destUri", "http://local.slidev.org:8082/simple-idp?realm=realm",
                "request_id", "unique_id", attributes, roles);
        
    }
    
    @Test
    public void testSandbox() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException,
            TransformerException, MarshalException, XMLSignatureException {
        
        service.setIssuerBase("http://local.slidev.org:8082/simple-idp");
        
        List<String> roles = Arrays.asList("role1", "role2");
        
        Map<String, String> attributes = new HashMap<String, String>();
        Mockito.when(
                samlComposer.componseResponse("destUri", "http://local.slidev.org:8082/simple-idp?",
                        "request_id", "unique_id", attributes, roles)).thenReturn("samlResponse");
        
        Request request = Mockito.mock(AuthRequestService.Request.class);
        Mockito.when(request.getRequestId()).thenReturn("request_id");
        Mockito.when(request.getRealm()).thenReturn(null);
        Mockito.when(request.getDestination()).thenReturn("destUri");
        
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserId()).thenReturn("unique_id");
        
        service.buildAssertion("unique_id", roles, attributes, request);
        
        Mockito.verify(samlComposer).componseResponse("destUri", "http://local.slidev.org:8082/simple-idp",
                "request_id", "unique_id", attributes, roles);
        
    }
}
