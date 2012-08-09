/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.client.impl.transform;

import java.io.IOException;
import java.net.URL;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.node.ObjectNode;

import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.BasicLink;

/**
 * 
 * Tell Jackson how to deserialize a Link type.
 * 
 */
public class LinkDeserializer extends StdDeserializer<Link> {
    
    LinkDeserializer() {
        super(Link.class);
    }
    
    @Override
    public Link deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = (ObjectNode) mapper.readTree(parser);
        
        JsonNode relNode = root.get("rel");
        JsonNode hrefNode = root.get("href");
        return new BasicLink(relNode.asText(), new URL(hrefNode.asText()));
    }
}