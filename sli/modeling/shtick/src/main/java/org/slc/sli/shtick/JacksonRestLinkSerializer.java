package org.slc.sli.shtick;

import java.io.IOException;
import java.lang.reflect.Type;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.ser.std.StdKeySerializer;

/**
 *
 * Tell Jackson how to serialize a Link type.
 *
 */
public class JacksonRestLinkSerializer extends SerializerBase<RestLink> {

    public JacksonRestLinkSerializer() {
        super(RestLink.class);
    }

    private static final SerializerBase<Object> DEFAULT = new StdKeySerializer();

    @Override
    public void serialize(RestLink link, JsonGenerator jgen, SerializerProvider provider) throws IOException {

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
