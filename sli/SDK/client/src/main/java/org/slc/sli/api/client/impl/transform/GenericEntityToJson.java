package org.slc.sli.api.client.impl.transform;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.slc.sli.api.client.Entity;

/**
 * 
 * Tell GSON how to marshal this type.
 * 
 */
public class GenericEntityToJson implements JsonSerializer<Entity> {
    
    @Override
    public JsonElement serialize(final Entity entity, final Type type, final JsonSerializationContext context) {
        
        JsonObject root = new JsonObject();
        
        for (Map.Entry<String, Object> entry : entity.getData().entrySet()) {
            root.add(entry.getKey(), serializeObject(entry.getValue()));
        }
        
        return root;
    }
    
    @SuppressWarnings("unchecked")
    private JsonElement serializeObject(final Object val) {
        JsonElement rval = null;
        
        if (val instanceof List) {
            rval = seralizeList((List<Object>) val);
        } else if (val instanceof Map) {
            rval = serializeMap((Map<String, Object>) val);
        } else {
            rval = serializePrimitive(val);
        }
        
        return rval;
    }
    
    private JsonElement seralizeList(final List<Object> val) {
        JsonArray rval = new JsonArray();
        
        for (Object obj : val) {
            rval.add(serializeObject(obj));
        }
        
        return rval;
    }
    
    private JsonElement serializeMap(final Map<String, Object> val) {
        JsonObject rval = new JsonObject();
        
        for (Map.Entry<String, Object> entry : val.entrySet()) {
            rval.add(entry.getKey(), serializeObject(entry.getValue()));
        }
        
        return rval;
    }
    
    private JsonPrimitive serializePrimitive(final Object val) {
        JsonPrimitive rval = null;
        if (val instanceof String) {
            rval = new JsonPrimitive((String) val);
        } else if (val instanceof Number) {
            rval = new JsonPrimitive((Number) val);
        } else if (val instanceof Character) {
            rval = new JsonPrimitive((Character) val);
        }
        return rval;
    }
}