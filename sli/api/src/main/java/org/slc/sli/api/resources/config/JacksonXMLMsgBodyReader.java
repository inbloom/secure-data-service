package org.slc.sli.api.resources.config;

import com.fasterxml.jackson.xml.XmlMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * XML Deserializer
 *
 * @author srupasinghe
 *
 */
@Provider
@Component
@Consumes({ MediaType.APPLICATION_XML+";charset=utf-8", Resource.SLC_XML_MEDIA_TYPE+";charset=utf-8" })
public class JacksonXMLMsgBodyReader implements MessageBodyReader<EntityBody> {


    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public EntityBody readFrom(Class<EntityBody> type, Type genericType, Annotation[] annotations,
                                   MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                                   InputStream entityStream) throws IOException, WebApplicationException {
        EntityBody body = null;

        if (entityStream != null) {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            xmlMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
            body = xmlMapper.readValue(entityStream, type);
        } else {
            body = new EntityBody();
        }

        return body;
    }
}
