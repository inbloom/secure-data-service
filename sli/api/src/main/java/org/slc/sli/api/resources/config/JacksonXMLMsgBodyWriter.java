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

import com.fasterxml.jackson.xml.XmlMapper;

import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.Resource;

/**
 * Custom JAXB Context Resolver that will generate XML
 * 
 * */
@SuppressWarnings("rawtypes")
@Provider
@Component
@Produces({ MediaType.APPLICATION_XML, Resource.SLC_XML_MEDIA_TYPE })
public class JacksonXMLMsgBodyWriter implements MessageBodyWriter {
    
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
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        xmlMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        xmlMapper.writeValue(entityStream, t);
    }
    
}
