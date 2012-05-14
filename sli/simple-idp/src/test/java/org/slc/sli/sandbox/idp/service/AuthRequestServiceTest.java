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
        
        Request processed = authService.processRequest("samlRequest", "myrealm");
        
        Mockito.verify(samlDecoder).decode("samlRequest");
        
        assertEquals("id", processed.getRequestId());
        assertEquals("myrealm", processed.getRealm());
        
        assertEquals(null, authService.processRequest(null, null));
    }
    
    @Test
    public void testForceAuthn() {
        SamlRequest request = Mockito.mock(SamlRequest.class);
        Mockito.when(request.getId()).thenReturn("id");
        Mockito.when(request.getIdpDestination()).thenReturn("http://destination/sp?realm=myrealm");
        Mockito.when(request.isForceAuthn()).thenReturn(true);
        Mockito.when(samlDecoder.decode("samlRequest")).thenReturn(request);
        
        Request processed = authService.processRequest("samlRequest", "myrealm");
        
        Mockito.verify(samlDecoder).decode("samlRequest");
        
        assertEquals("id", processed.getRequestId());
        assertEquals("myrealm", processed.getRealm());
        assertEquals(true, processed.isForceAuthn());
        assertEquals(null, authService.processRequest(null, null));
    }
}
