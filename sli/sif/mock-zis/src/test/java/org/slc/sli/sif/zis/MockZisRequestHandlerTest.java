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
 * JUnit tests for MockZisRequestHandler
 * 
 * @author jtully
 * 
 */
public class MockZisRequestHandlerTest {
    
    @InjectMocks
    MockZisRequestHandler mockZisRequestHandler;

    @Mock
    private MockZis mockZis;
    
    private static final String MOCK_SIF_MESSAGE = "SIF_MESSAGE";
    
    private static final String MOCK_ACK = "ACK";
    
    @Before
    public void setup() {
        mockZisRequestHandler = new MockZisRequestHandler();

        MockitoAnnotations.initMocks(this);

        Mockito.when(mockZis.createAckString()).thenReturn(MOCK_ACK);
    }
    
    @Test
    public void shouldParseSifMessageOnPost() throws ServletException, IOException {
        MockHttpServletRequest req = createMockRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        mockZisRequestHandler.handleRequest(req, resp);
        
        Mockito.verify(mockZis, Mockito.times(1)).parseSIFMessage(Mockito.eq(MOCK_SIF_MESSAGE));
    }
    
    @Test
    public void shouldRespondToPostWithAck() throws ServletException, IOException {
        MockHttpServletRequest req = createMockRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        
        mockZisRequestHandler.handleRequest(req, resp);
        
        //check response is an ACK
        Assert.assertEquals("Response should be a SIF_ACK message", MOCK_ACK, resp.getContentAsString());
    }
    
    private MockHttpServletRequest createMockRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "mockZis");
        req.setContent(MOCK_SIF_MESSAGE.getBytes());
        return req;
    }
}
