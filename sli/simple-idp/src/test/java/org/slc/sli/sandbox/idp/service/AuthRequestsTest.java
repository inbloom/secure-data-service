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
import org.slc.sli.sandbox.idp.service.AuthRequests.Request;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthRequestsTest {
    
    @Mock
    SamlRequestDecoder samlDecoder;
    
    @InjectMocks
    AuthRequests service = new AuthRequests();
    
    @Test
    public void test() {
        SamlRequest request = Mockito.mock(SamlRequest.class);
        Mockito.when(request.getId()).thenReturn("id");
        Mockito.when(request.getDestination()).thenReturn("destination");
        Mockito.when(samlDecoder.decode("samlRequest")).thenReturn(request);
        
        Request processed = service.processRequest("samlRequest", "tenant");
        
        Mockito.verify(samlDecoder).decode("samlRequest");
        
        assertEquals("id", processed.getRequestId());
        assertEquals("tenant", processed.getTenant());
        
        assertEquals(null, service.processRequest(null, null));
    }
    
}
