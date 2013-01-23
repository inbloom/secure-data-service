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
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;

import org.slc.sli.api.client.impl.GenericEntity;

/**
 * 
 * Tell Jackson how to serialize the GenericEntity type.
 * 
 * Serialized objects used in PUT and POST events contain only the Entity body element as the
 * root of the JSON. The root name and entity type are implied by the resource so they are
 * omitted in the payload.
 * 
 */
public class GenericEntitySerializer extends StdSerializer<GenericEntity> {
    
    public static final String ENTITY_BODY_KEY = "body";
    public static final String ENTITY_LINKS_KEY = "links";
    public static final String ENTITY_METADATA_KEY = "metaData";
    
    private static final StdSerializer<Object> DEFAULT = new StdKeySerializer();
    private ObjectMapper mapper = new ObjectMapper();
    
    public GenericEntitySerializer() {
        super(GenericEntity.class);
    }
    
    private JsonNode serializeObject(final Object val) {
        return serializeObject(null, val);
    }
    
    @SuppressWarnings("unchecked")
    private JsonNode serializeObject(final String key, final Object val) {
        JsonNode rval = null;
        
        if (val instanceof List) {
            rval = seralizeList(key, (List<Object>) val);
        } else if (val instanceof Map) {
            rval = serializeMap(key, (Map<String, Object>) val);
        } else {
            rval = serializePrimitive(key, val);
        }
        return rval;
    }
    
    private JsonNode seralizeList(final String key, final List<Object> val) {
        
        ObjectNode rval = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();
        
        for (Object obj : val) {
            array.add(serializeObject(obj));
        }
        
        if (key == null) {
            return array;
        }
        
        rval.put(key, array);
        return rval;
    }
    
    private JsonNode serializeMap(final String key, final Map<String, Object> val) {
        
        ObjectNode tree = mapper.createObjectNode();
        
        for (Map.Entry<String, Object> entry : val.entrySet()) {
            tree.put(entry.getKey(), serializeObject(null, entry.getValue()));
        }
        return tree;
    }
    
    private JsonNode serializePrimitive(final String key, final Object val) {
        
        JsonNode valueNode = null;
        
        if (val == null) {
            valueNode = NullNode.getInstance();
        } else if (val instanceof String) {
            valueNode = new TextNode((String) val);
        } else if (val instanceof Integer) {
            valueNode = new IntNode((Integer) val);
        } else if (val instanceof Long) {
            valueNode = new LongNode((Long) val);
        } else if (val instanceof Double || val instanceof Float) {
            valueNode = new DoubleNode((Double) val);
        } else {
            valueNode = new POJONode(val);
        }
        
        if (key == null) {
            return valueNode;
        }
        
        ObjectNode rval = mapper.createObjectNode();
        rval.put(key, valueNode);
        return rval;
    }
    
    @Override
    public void serialize(GenericEntity entity, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        
        jgen.writeStartObject();
        
        // The SLI API only supports entity body elements for PUT and POST requests. If the
        // entity data has a 'body' element, use that explicitly.
        if (entity.getData().containsKey(ENTITY_BODY_KEY)) {
            jgen.writeObject(serializeObject(entity.getData().get(ENTITY_BODY_KEY)));
            
        } else {
            for (Map.Entry<String, Object> entry : entity.getData().entrySet()) {
                if (entry.getKey().equals(ENTITY_LINKS_KEY) || entry.getKey().equals(ENTITY_METADATA_KEY)) {
                    // ignore these read-only fields.
                    continue;
                }
                jgen.writeFieldName(entry.getKey());
                jgen.writeObject(serializeObject(entry.getValue()));
            }
        }
        jgen.writeEndObject();
    }
    
    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return DEFAULT.getSchema(provider, typeHint);
    }
}
