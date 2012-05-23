package org.slc.sli.api.client.impl.transform;

import java.io.IOException;
import java.net.URL;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.BasicLink;

public class LinkDeserializer extends StdDeserializer<Link> {

	LinkDeserializer() {
		super(Link.class);
	}

	@Override
	public Link deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {

	    ObjectMapper mapper = (ObjectMapper) parser.getCodec();
	    ObjectNode root = (ObjectNode) mapper.readTree(parser);

	    JsonNode relNode = root.get("rel");
	    JsonNode hrefNode = root.get("href");
        return new BasicLink(relNode.asText(), new URL(hrefNode.asText()));
	}
}