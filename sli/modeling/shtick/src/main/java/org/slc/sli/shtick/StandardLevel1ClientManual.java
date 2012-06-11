package org.slc.sli.shtick;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.api.client.Entity;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author jstokes
 */
public class StandardLevel1ClientManual implements Level1ClientManual {

    private Level0ClientManual level0ClientManual;
    private ObjectMapper mapper;

    public StandardLevel1ClientManual(Level0ClientManual level0ClientManual) {
        this.level0ClientManual = level0ClientManual;
        this.mapper = new ObjectMapper();
    }

    @Override
    public List<Entity> getRequest(String token, URL url) throws URISyntaxException, IOException, SLIDataStoreException {
        Response response = level0ClientManual.getRequest(token, url);
        return deserialize(response);
    }

    private List<Entity> deserialize(Response response) throws IOException, SLIDataStoreException {
        try {
            JsonNode element = mapper.readValue(response.readEntity(String.class), JsonNode.class);
            if (element instanceof ArrayNode) {
                return mapper.readValue(element, new TypeReference() { });
            } else if (element instanceof ObjectNode) {
                return Arrays.asList(mapper.readValue(element, Entity.class));
            }
        } catch (JsonParseException e) {
            throw new SLIDataStoreException(e);
        }

        throw new SLIDataStoreException();
    }

}
