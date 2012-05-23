package org.slc.sli.api.client.impl.transform;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.ser.std.StdKeySerializer;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.node.LongNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.POJONode;
import org.codehaus.jackson.node.TextNode;

import org.slc.sli.api.client.impl.GenericEntity;

/**
 * 
 * Tell Jackson how to serialize the GenericEntity type.
 * 
 */
public class GenericEntitySerializer extends SerializerBase<GenericEntity> {
    
    private static final SerializerBase<Object> DEFAULT = new StdKeySerializer();
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
        
        if (val instanceof List)
            rval = seralizeList(key, (List<Object>) val);
        else if (val instanceof Map)
            rval = serializeMap(key, (Map<String, Object>) val);
        else
            rval = serializePrimitive(key, val);
        return rval;
    }
    
    private JsonNode seralizeList(final String key, final List<Object> val) {
        
        ObjectNode rval = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();
        
        for (Object obj : val)
            array.add(serializeObject(obj));
        
        if (key == null)
            return array;
        
        rval.put(key, array);
        return rval;
    }
    
    private JsonNode serializeMap(final String key, final Map<String, Object> val) {
        
        ObjectNode tree = mapper.createObjectNode();
        
        for (Map.Entry<String, Object> entry : val.entrySet())
            tree.put(entry.getKey(), serializeObject(null, entry.getValue()));
        return tree;
    }
    
    private JsonNode serializePrimitive(final String key, final Object val) {
        
        JsonNode valueNode = null;
        
        if (val == null)
            valueNode = NullNode.getInstance();
        else if (val instanceof String)
            valueNode = new TextNode((String) val);
        else if (val instanceof Long)
            valueNode = new LongNode((Long) val);
        else if (val instanceof Integer)
            valueNode = new IntNode((Integer) val);
        else if (val instanceof Float)
            valueNode = new DoubleNode((Float) val);
        else if (val instanceof Double)
            valueNode = new DoubleNode((Double) val);
        else
            valueNode = new POJONode(val);
        
        if (key == null)
            return valueNode;
        
        ObjectNode rval = mapper.createObjectNode();
        rval.put(key, valueNode);
        return rval;
    }
    
    @Override
    public void serialize(GenericEntity entity, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        
        jgen.writeStartObject();
        jgen.writeStringField("type", entity.getEntityType());
        for (Map.Entry<String, Object> entry : entity.getData().entrySet()) {
            jgen.writeFieldName(entry.getKey());
            jgen.writeTree(serializeObject(entry.getKey(), entry.getValue()));
        }
        jgen.writeEndObject();
    }
    
    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        return DEFAULT.getSchema(provider, typeHint);
    }
    
}
