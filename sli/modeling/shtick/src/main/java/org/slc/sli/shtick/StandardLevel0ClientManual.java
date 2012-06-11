package org.slc.sli.shtick;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author jstokes
 */
public class StandardLevel0ClientManual implements Level0ClientManual {

    private Client jerseyClient;

    public StandardLevel0ClientManual() {
        this.jerseyClient = ClientFactory.newClient();
    }

    @Override
    public Response getRequest(String token, URL url) throws URISyntaxException {
        final Invocation.Builder builder;
        builder = jerseyClient.target(url.toURI()).request(MediaType.APPLICATION_JSON);
        builder.header("Authorization", String.format("Bearer %s", token));

        return builder.buildGet().invoke();
    }
}
