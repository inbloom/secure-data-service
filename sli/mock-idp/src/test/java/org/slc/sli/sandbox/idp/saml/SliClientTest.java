package org.slc.sli.sandbox.idp.saml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class SliClientTest {
    
    SliClient sliClient = new SliClient();
    
    @Test
    public void testFindDestination() {
        sliClient.setDestination("http://a-destination");
        assertNotNull(sliClient.findDestination());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testPostResponse() throws IOException {
        HttpClient http = Mockito.mock(HttpClient.class);
        Mockito.when(http.execute(Mockito.any(HttpUriRequest.class), Mockito.any(ResponseHandler.class))).thenAnswer(
                new Answer<URI>() {
                    
                    @Override
                    public URI answer(InvocationOnMock invocation) throws Throwable {
                        ResponseHandler<URI> handler = (ResponseHandler<URI>) invocation.getArguments()[1];
                        HttpResponse response = Mockito.mock(HttpResponse.class);
                        StatusLine statusLine = Mockito.mock(StatusLine.class);
                        Mockito.when(statusLine.getStatusCode()).thenReturn(307);
                        Mockito.when(response.getStatusLine()).thenReturn(statusLine);
                        Mockito.when(response.getFirstHeader("Location")).thenReturn(new Header() {
                            
                            @Override
                            public String getName() {
                                return null;
                            }
                            
                            @Override
                            public String getValue() {
                                return "http://testUri";
                            }
                            
                            @Override
                            public HeaderElement[] getElements() throws ParseException {
                                return null;
                            }
                        });
                        return handler.handleResponse(response);
                    }
                });
        sliClient.setHttpClient(http);
        URI redirect = sliClient.postResponse(URI.create("http://destination"), "samlResponseXml");
        assertEquals("http://testUri", redirect.toString());
    }
}
