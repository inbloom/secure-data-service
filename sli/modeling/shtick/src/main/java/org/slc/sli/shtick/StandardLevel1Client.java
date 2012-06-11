package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

import org.slc.sli.api.client.Entity;

/**
 * @author jstokes
 */
public final class StandardLevel1Client implements Level1Client {

    private final Level0Client client;
    private final ObjectMapper mapper;

    public StandardLevel1Client(final Level0Client client) {
        if (client == null) {
            throw new NullPointerException("client");
        }
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    @Override
    public List<Entity> getRequest(final String token, final URL url) throws URISyntaxException, IOException,
            SLIDataStoreException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        final Response response = client.getRequest(token, url, MediaType.APPLICATION_JSON);
        return deserialize(response);
    }

    private List<Entity> deserialize(final Response response) throws IOException, SLIDataStoreException {
        try {
            final JsonNode element = mapper.readValue(response.readEntity(String.class), JsonNode.class);
            if (element instanceof ArrayNode) {
                return mapper.readValue(element, new TypeReference<List<Entity>>() {
                });
            } else if (element instanceof ObjectNode) {
                return Arrays.asList(mapper.readValue(element, Entity.class));
            }
        } catch (final JsonParseException e) {
            throw new SLIDataStoreException(e);
        }
        throw new SLIDataStoreException("Parsed object was not Array or Object");
    }

}
