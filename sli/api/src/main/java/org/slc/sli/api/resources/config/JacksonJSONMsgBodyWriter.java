package org.slc.sli.api.resources.config;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.representation.EntityResponse;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.Home;
import org.slc.sli.api.resources.Resource;

/**
 * Custom JAXB Context Resolver that will generate XML
 *
 * */
@SuppressWarnings("rawtypes")
@Provider
@Component
@Produces({ Resource.JSON_MEDIA_TYPE+";charset=utf-8", Resource.SLC_JSON_MEDIA_TYPE+";charset=utf-8" })
public class JacksonJSONMsgBodyWriter implements MessageBodyWriter {

    @Override
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        Object jsonBody = t;

        // check on the class type to see if we need
        // to strip off any extraneous wrapper classes
        if (type != null) {

            if (type.getName().equals("org.slc.sli.api.representation.Home")) {
                Home home = (Home) t;
                jsonBody = home.getLinksMap();
            } else if (type.getName().equals("org.slc.sli.api.representation.EntityResponse")) {
                EntityResponse entityReponse = (EntityResponse) t;
                jsonBody = entityReponse.getEntity();
            }

        }

        new ObjectMapper().writeValue(entityStream, jsonBody);
    }
}
