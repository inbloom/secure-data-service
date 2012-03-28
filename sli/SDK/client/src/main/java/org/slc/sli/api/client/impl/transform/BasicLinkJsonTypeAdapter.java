package org.slc.sli.api.client.impl.transform;

import java.io.IOException;
import java.net.URL;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.impl.BasicLink;

/**
 * Adapter for special handling of Entity Links. A link is a resource name and a URL. This
 * adapter marshals and unmarshals Link objects to/from JSON.
 * 
 * @author asaarela
 */
public class BasicLinkJsonTypeAdapter extends TypeAdapter<Link> {
    
    @Override
    public Link read(final JsonReader reader) throws IOException {
        
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String linkString = reader.nextString();
        String[] entries = linkString.split(",");
        for (String s : entries) {
            String[] parts = s.split(":");
            
            return new BasicLink(parts[0], new URL(parts[1]));
        }
        
        return null;
    }
    
    @Override
    public void write(final JsonWriter writer, final Link link) throws IOException {
        
        if (link == null) {
            writer.nullValue();
            return;
        }
        
        String linkString = "rel:" + link.getLinkName() + ", href:" + link.getResourceURL().toString();
        writer.value(linkString);
        
    }
    
}
