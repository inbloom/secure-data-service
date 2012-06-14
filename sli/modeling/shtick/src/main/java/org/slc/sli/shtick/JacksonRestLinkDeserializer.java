package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URL;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Tell Jackson how to deserialize a Link type.
 */
public class JacksonRestLinkDeserializer extends StdDeserializer<RestLink> {

    JacksonRestLinkDeserializer() {
        super(RestLink.class);
    }

    @Override
    public RestLink deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = (ObjectNode) mapper.readTree(parser);

        JsonNode relNode = root.get("rel");
        JsonNode hrefNode = root.get("href");
        return new RestLink(relNode.asText(), new URL(hrefNode.asText()));
    }
}