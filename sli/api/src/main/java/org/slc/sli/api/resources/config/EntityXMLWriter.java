package org.slc.sli.api.resources.config;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Custom Context Resolver that will generate XML for entities
 *
 * */
@SuppressWarnings("rawtypes")
@Provider
@Component
@Produces({ MediaType.APPLICATION_XML+";charset=utf-8", HypermediaType.VENDOR_SLC_XML+";charset=utf-8" })
public class EntityXMLWriter implements MessageBodyWriter<EntityResponse> {

    @Autowired
    private EntityDefinitionStore entityDefs;

    private static final String TYPE = "type";
    private static final String HREF = "href";

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type.getName().equals("org.slc.sli.api.representation.EntityResponse")) {
            return true;
        }

        return false;
    }

    @Override
    public long getSize(EntityResponse entityResponse, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(EntityResponse entityResponse, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        try {
            //write the entity
            writeEntity(entityResponse, entityStream);
        } catch (XMLStreamException e) {
            error("Could not write out to XML {}", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Serializes an entity to xml
     * @param entityResponse
     * @param entityStream
     * @throws XMLStreamException
     */
    protected void writeEntity(EntityResponse entityResponse, OutputStream entityStream) throws XMLStreamException {
        XMLStreamWriter writer = null;

        try {
            //create the factory
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            //get the stream writer
            writer = factory.createXMLStreamWriter(entityStream);

            EntityDefinition resourceDef = entityDefs.lookupByEntityType(entityResponse.getEntityCollectionName());
            String resourceName = (resourceDef != null) ? resourceDef.getResourceName() : entityResponse.getEntityCollectionName();

            //start the document
            writer.writeStartDocument();
            writer.writeStartElement(resourceName);
            //recursively add the objects
            writeToXml(entityResponse.getEntity(), entityResponse.getEntityCollectionName(), writer);
            //end the document
            writer.writeEndElement();
            writer.writeEndDocument();
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * Serializes the given object to xml
     * @param object The object to write out to
     * @param key
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeToXml(Object object, String key, XMLStreamWriter writer) throws XMLStreamException {
        if (List.class.isInstance(object)) {
            List values = (List) object;

            for (Object obj : values) {
                writer.writeStartElement(key);
                writeToXml(obj, key, writer);
                writer.writeEndElement();
            }
        } else if (Map.class.isInstance(object)) {
            Map<String, Object> map = (Map) object;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                writer.writeStartElement(entry.getKey());
                writeToXml(entry.getValue(), entry.getKey(), writer);
                writer.writeEndElement();
            }
        } else if (EmbeddedLink.class.isInstance(object)) {
            EmbeddedLink link = (EmbeddedLink) object;
            writer.writeStartElement(TYPE);
            writer.writeCharacters(link.getRel());
            writer.writeEndElement();

            writer.writeStartElement(HREF);
            writer.writeCharacters(link.getHref());
            writer.writeEndElement();
        } else {
            writer.writeCharacters(String.valueOf(object));
        }

    }
}
