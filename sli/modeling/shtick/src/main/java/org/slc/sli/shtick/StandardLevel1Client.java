package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * @author jstokes
 */
public final class StandardLevel1Client implements Level1Client {

    private final Level0Client client;
    private final ObjectMapper mapper;

    protected StandardLevel1Client(final Level0Client client) {
        if (client == null) {
            throw new NullPointerException("client");
        }

        this.client = client;
        this.mapper = new ObjectMapper();
    }

    public StandardLevel1Client() {
        this(new StandardLevel0Client());
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

    @Override
    public void deleteRequest(String token, URL url) throws URISyntaxException, IOException, SLIDataStoreException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }

        final Response response = client.deleteRequest(token, url, MediaType.APPLICATION_JSON);

        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new SLIDataStoreException("Delete of entity failed: " + url.toString());
        }
    }

    private List<Entity> deserialize(final Response response) throws IOException, SLIDataStoreException {
        try {
            final JsonNode element = mapper.readValue(response.readEntity(String.class), JsonNode.class);
            if (element instanceof ArrayNode) {
                return mapper.readValue(element, new TypeReference<List<GenericEntity>>() {
                });
            } else if (element instanceof ObjectNode) {
                List<Entity> list = new ArrayList<Entity>();
                list.add(mapper.readValue(element, GenericEntity.class));
                return list;
            }
        } catch (final JsonParseException e) {
            throw new SLIDataStoreException(e);
        }
        throw new SLIDataStoreException("Parsed object was not Array or Object");
    }

}
