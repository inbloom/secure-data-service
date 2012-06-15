package org.slc.sli.shtick;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
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
    public List<RestEntity> getRequest(String token, URL url) throws URISyntaxException, IOException, RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }

        final String body = client.getRequest(token, url, getMediaType());
        return deserialize(body);
    }

    @Override
    public void deleteRequest(String token, URL url) throws URISyntaxException, IOException, RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }

        client.deleteRequest(token, url, getMediaType());
    }

    @Override
    public URL postRequest(String token, final RestEntity data, final URL url) throws URISyntaxException, IOException,
            RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }

        final StringWriter sw = new StringWriter();
        mapper.writeValue(sw, data);

        return client.postRequest(token, sw.toString(), url, getMediaType());
    }

    @Override
    public void putRequest(String token, final RestEntity data, final URL url) throws URISyntaxException, IOException,
            RestException {
        if (token == null) {
            throw new NullPointerException("token");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }

        final StringWriter sw = new StringWriter();
        mapper.writeValue(sw, data);

        client.putRequest(token, sw.toString(), url, getMediaType());

    }

    private List<RestEntity> deserialize(final String body) throws IOException {
        try {
            final JsonNode element = mapper.readValue(body, JsonNode.class);
            if (element instanceof ArrayNode) {
                return mapper.readValue(element, new TypeReference<List<RestEntity>>() {
                });
            } else if (element instanceof ObjectNode) {
                List<RestEntity> list = new ArrayList<RestEntity>();
                list.add(mapper.readValue(element, RestEntity.class));
                return list;
            }
        } catch (final JsonParseException e) {
            throw new RuntimeException(e);
        }
        throw new AssertionError();
    }

    protected abstract String getMediaType();
}
