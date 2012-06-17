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
 *
 * Intentionally package-protected.
 */
final class JacksonRestLinkDeserializer extends StdDeserializer<Link> {

    JacksonRestLinkDeserializer() {
        super(Link.class);
    }

    @Override
    public Link deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {

        final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        final ObjectNode root = (ObjectNode) mapper.readTree(parser);

        final JsonNode relNode = root.get(Constants.LINK_RESOURCE_KEY);
        final JsonNode hrefNode = root.get(Constants.LINK_HREF_KEY);
        return new Link(relNode.asText(), new URL(hrefNode.asText()));
    }
}