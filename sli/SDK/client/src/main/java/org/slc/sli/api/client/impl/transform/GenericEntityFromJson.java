package org.slc.sli.api.client.impl.transform;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.BasicLink;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 *
 * Tell GSON how to de-marshal the GenericEntity type.
 *
 */
public class GenericEntityFromJson implements JsonDeserializer<Entity> {

    private static final String ENTITY_TYPE_KEY = "entityType";

    @Override
    public Entity deserialize(final JsonElement element, final Type type,
            final JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = element.getAsJsonObject();
        String entityType = null;

        if (obj.has(ENTITY_TYPE_KEY)) {
            entityType = obj.get(ENTITY_TYPE_KEY).getAsString();
        }

        Map<String, Object> data = processObject(obj.getAsJsonObject());

        if (entityType != null) {
            return new GenericEntity(entityType, data);
        } else {
            return new GenericEntity("Generic", data);
        }
    }

    private Map<String, Object> processObject(final JsonObject obj) {
        Map<String, Object> rval = new HashMap<String, Object>();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            rval.put(entry.getKey(), processElement(entry.getKey(), entry.getValue()));
        }
        return rval;
    }

    private Object processElement(final String key, final JsonElement element) {
        Object rval = null;
        if (element instanceof JsonObject) {
            Map<String, Object> r2 = processObject(element.getAsJsonObject());

            // convert hashmap entries into Link instances.
            if (key.equals(Entity.LINKS_KEY)) {
                rval = new LinkedList<Link>();

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
        } else if (element instanceof JsonPrimitive) {
            rval = processPrimitive(element.getAsJsonPrimitive());

        } else if (element instanceof JsonNull) {
            rval = null;

        } else if (element instanceof JsonArray) {
            rval = processArray(key, element.getAsJsonArray());

        }
        return rval;
    }

    private List<Object> processArray(final String key, final JsonArray asJsonArray) {
        List<Object> list = new LinkedList<Object>();

        for (JsonElement entry : asJsonArray) {
            list.add(processElement(key, entry));
        }

        return list;
    }

    private Object processPrimitive(final JsonPrimitive prim) {
        Object val;
        if (prim.isBoolean()) {
            val = prim.getAsBoolean();
        } else if (prim.isNumber()) {
            val = prim.getAsNumber();
        } else {
            val = prim.getAsString();
        }
        return val;
    }

}