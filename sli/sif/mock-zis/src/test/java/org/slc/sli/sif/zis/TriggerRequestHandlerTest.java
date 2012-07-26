package org.slc.sli.sif.zis;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Unit tests for TriggerRequestHandler
 * 
 * @author jtully
 *
 */
public class TriggerRequestHandlerTest {
    
    @InjectMocks
    TriggerRequestHandler triggerRequestHandler;

    @Mock
    private MockZis mockZis;
    
    private static final String MOCK_SIF_MESSAGE = "SIF_MESSAGE";
    
    private static final String MOCK_ACK = "ACK";
    
    @Before
    public void setup() {
        triggerRequestHandler = new TriggerRequestHandler();

        MockitoAnnotations.initMocks(this);

        Mockito.when(mockZis.createAckString()).thenReturn(MOCK_ACK);
    }
    
    @Test
    public void shouldBroadcastMessageOnPost() throws ServletException, IOException {
        MockHttpServletRequest req = createMockRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        triggerRequestHandler.handleRequest(req, resp);
        
        Mockito.verify(mockZis, Mockito.times(1)).broadcastMessage(Mockito.eq(MOCK_SIF_MESSAGE));
    }
    
    @Test
    public void shouldRespondToPostWithAck() throws ServletException, IOException {
        MockHttpServletRequest req = createMockRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        triggerRequestHandler.handleRequest(req, resp);
        
        //check response is an ACK
        Assert.assertEquals("Response should be a SIF_ACK message", MOCK_ACK, resp.getContentAsString());
    }
    
    private MockHttpServletRequest createMockRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "mockZis");
        req.setContent(MOCK_SIF_MESSAGE.getBytes());
        return req;
    }
}
