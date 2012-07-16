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
 * Tell Jackson how to serialize a Link type.
 * 
 * Intentionally package-protected.
 */
final class JacksonRestLinkSerializer extends SerializerBase<Link> {
    
    public JacksonRestLinkSerializer() {
        super(Link.class);
    }
    
    private static final SerializerBase<Object> DEFAULT = new StdKeySerializer();
    
    @Override
    public void serialize(final Link link, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {
        
        if (link == null) {
            jgen.writeNull();
            return;
        }
        
        String linkString = "{\"" + Constants.LINK_RESOURCE_KEY + "\":\"" + link.getLinkName() + "\",\""
                + Constants.LINK_HREF_KEY + "\":\"" + link.getResourceURL().toString() + "\"}";
        jgen.writeRaw(linkString);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        return DEFAULT.getSchema(provider, typeHint);
    }
    
}
