package org.slc.sli.sandbox.idp.saml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Handles looking up the destination from SAML metadata and posting SAMLResponses.
 * 
 * The main purpose of this class is to abstract out the httpClient code so other classes can be
 * unit tested without going insane.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class SliClient {
    
    @Value("${sli.simple-idp.response-destination}")
    String destination;
    
    private HttpClient httpclient = new DefaultHttpClient();
    
    /**
     * Retrieve the destination URI to which the SAMLResponse should be sent.
     */
    public URI findDestination() {
        return URI.create(destination);
    }
    
    /**
     * Posts a SAMLResponse to the provided uri.
     * 
     * @param destination
     * @param encodedResponse
     *            signed, base64 encoded SAMLRequest
     * @return redirect URI the API sends after processing the request
     */
    public URI postResponse(URI destination, String encodedResponse) {
        try {
            HttpPost post = new HttpPost(destination);
            UrlEncodedFormEntity data = new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("SAMLResponse",
                    encodedResponse)), "UTF-8");
            
            post.setEntity(data);
            URI redirectUri = httpclient.execute(post, new ResponseHandler<URI>() {
                
                @Override
                public URI handleResponse(HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (!(status == 307 || status == 301 || status == 302)) {
                        response.getEntity().writeTo(System.err);
                        throw new RuntimeException("Expected redirect, recieved: " + status);
                    }
                    Header location = response.getFirstHeader("Location");
                    return URI.create(location.getValue());
                }
            });
            
            return redirectUri;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void setHttpClient(HttpClient client) {
        this.httpclient = client;
    }
    
    protected void setDestination(String dest) {
        this.destination = dest;
    }
}
