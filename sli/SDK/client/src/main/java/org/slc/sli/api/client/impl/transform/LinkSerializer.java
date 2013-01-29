/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import java.lang.reflect.Type;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.ser.std.StdKeySerializer;

import org.slc.sli.api.client.Link;

/**
 * 
 * Tell Jackson how to serialize a Link type.
 * 
 */
public class LinkSerializer extends SerializerBase<Link> {
    
    public LinkSerializer() {
        super(Link.class);
    }
    
    private static final SerializerBase<Object> DEFAULT = new StdKeySerializer();
    
    @Override
    public void serialize(Link link, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        
        if (link == null) {
            jgen.writeNull();
            return;
        }
        
        String linkString = "{\"rel\":\"" + link.getLinkName() + "\",\"href\":\"" + link.getResourceURL().toString()
                + "\"}";
        jgen.writeRaw(linkString);
    }
    
    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return DEFAULT.getSchema(provider, typeHint);
    }
    
}
