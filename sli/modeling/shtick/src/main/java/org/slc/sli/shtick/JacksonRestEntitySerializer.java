package org.slc.sli.shtick;

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

/**
 *
 * Tell Jackson how to serialize the GenericEntity type.
 *
 * Serialized objects used in PUT and POST events contain only the Entity body element as the
 * root of the JSON. The root name and entity type are implied by the resource so they are
 * omitted in the payload.
 *
 * Intentionally package-protected.
 */
final class JacksonRestEntitySerializer extends SerializerBase<Entity> {

    private static final SerializerBase<Object> DEFAULT = new StdKeySerializer();
    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonRestEntitySerializer() {
        super(Entity.class);
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

        final ObjectNode tree = mapper.createObjectNode();
        for (final Map.Entry<String, Object> entry : val.entrySet()) {
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
    public void serialize(Entity entity, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        // The SLI API only supports entity body elements for PUT and POST requests. If the
        // entity data has a 'body' element, use that explicitly.
        if (entity.getData().containsKey(Constants.ENTITY_BODY_KEY)) {
            jgen.writeObject(serializeObject(entity.getData().get(Constants.ENTITY_BODY_KEY)));

        } else {
            for (Map.Entry<String, Object> entry : entity.getData().entrySet()) {
                if (entry.getKey().equals(Constants.ENTITY_LINKS_KEY)
                        || entry.getKey().equals(Constants.ENTITY_METADATA_KEY)) {
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
