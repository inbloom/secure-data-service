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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.BasicLink;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * 
 * Tell Jackson how to deserialize the GenericEntity type.
 * 
 */
public class GenericEntityDeserializer extends StdDeserializer<GenericEntity> {
    
    public static final String ENTITY_TYPE_KEY = "entityType";
    
    public GenericEntityDeserializer() {
        super(GenericEntity.class);
    }
    
    private Map<String, Object> processObject(final ObjectNode obj) {
        Map<String, Object> rval = new HashMap<String, Object>();
        
        Iterator<String> it = obj.fieldNames();
        
        while (it.hasNext()) {
            String key = it.next();
            JsonNode node = obj.get(key);
            rval.put(key, processElement(key, node));
        }
        
        return rval;
    }
    
    private Object processElement(final String key, final JsonNode element) {
        Object rval;
        if (element instanceof ObjectNode) {
            Map<String, Object> r2 = processObject((ObjectNode) element);
            
            // convert TreeMap entries into Link instances.
            if (key.equals(Entity.LINKS_KEY)) {
                
                String refName = (String) r2.get(Link.LINK_RESOURCE_KEY);
                String hrefString = (String) r2.get(Link.LINK_HREF_KEY);
                
                try {
                    rval = new BasicLink(refName, new URL(hrefString));
                    
                } catch (MalformedURLException e) {
                    rval = r2;
                }
            } else {
                rval = r2;
            }
        } else if (element instanceof NullNode) {
            rval = null;
        } else if (element instanceof ArrayNode) {
            rval = processArray(key, (ArrayNode) element);
        } else {
            rval = processPrimitive(element);
        }
        
        return rval;
    }
    
    private List<Object> processArray(final String key, final ArrayNode asJsonArray) {
        List<Object> list = new LinkedList<Object>();
        
        Iterator<JsonNode> it = asJsonArray.elements();
        while (it.hasNext()) {
            list.add(processElement(key, it.next()));
        }
        return list;
    }
    
    private Object processPrimitive(final JsonNode prim) {
        Object val;
        
        if (prim instanceof BooleanNode) {
            val = prim.booleanValue();
        } else if (prim instanceof DoubleNode) {
            val = prim.doubleValue();
        } else if (prim instanceof IntNode) {
            val = prim.intValue();
        } else if (prim instanceof LongNode) {
            val = prim.longValue();
        } else {
            val = prim.textValue();
        }
        return val;
    }
    
    @Override
    public GenericEntity deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(parser);
        
        String entityType = null;
        
        if (root.has(ENTITY_TYPE_KEY)) {
            entityType = root.get(ENTITY_TYPE_KEY).textValue();
            root.remove(ENTITY_TYPE_KEY);
        }
        
        Map<String, Object> data = processObject(root);
        if (entityType != null) {
            return new GenericEntity(entityType, data);
        } else {
            return new GenericEntity("Generic", data);
        }
    }
}