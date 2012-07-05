package org.slc.sli.shtick;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author jstokes
 */
public abstract class AbstractLevel1Client implements Level1Client {

    private final Level0Client client;
    private final ObjectMapper mapper;

    protected AbstractLevel1Client(final Level0Client client, final ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public List<Entity> get(final String token, final URI uri) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (uri == null) {
            throw new NullPointerException("uri");
        }

        final String body = client.get(token, uri, getMediaType());
        return deserialize(body);
    }

    @Override
    public void delete(String token, URI uri) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (uri == null) {
            throw new NullPointerException("uri");
        }

        client.delete(token, uri, getMediaType());
    }

    @Override
    public URI post(String token, final Entity data, final URI uri) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }

        final StringWriter sw = new StringWriter();
        mapper.writeValue(sw, data);

        return client.post(token, sw.toString(), uri, getMediaType());
    }

    @Override
    public void put(String token, final Entity data, final URI uri) throws IOException, StatusCodeException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }

        final StringWriter sw = new StringWriter();
        mapper.writeValue(sw, data);

        client.put(token, sw.toString(), uri, getMediaType());

    }

    private List<Entity> deserialize(final String body) throws IOException {
        try {
            final JsonNode element = mapper.readValue(body, JsonNode.class);
            if (element instanceof ArrayNode) {
                return mapper.readValue(element, new TypeReference<List<Entity>>() {
                });
            } else if (element instanceof ObjectNode) {
                List<Entity> list = new ArrayList<Entity>();
                list.add(mapper.readValue(element, Entity.class));
                return list;
            }
        } catch (final JsonParseException e) {
            throw new RuntimeException(e);
        }
        throw new AssertionError();
    }

    protected abstract String getMediaType();
}
