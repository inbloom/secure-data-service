package org.slc.sli.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.ContextResolver;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Parser for sli resources
 * 
 * @author nbrown
 * 
 */
@Component
public class SliParser {
    private static final Logger LOG = LoggerFactory.getLogger(SliParser.class);
    
    @Autowired
    private ContextResolver<ObjectMapper> contextResolver;
    
    public <T> T parse(String source, Format format, Class<T> resourceType) {
        switch (format) {
            case JSON:
                return parseJSON(source, resourceType);
            default:
                LOG.warn("Unsupported format type " + format);
                return null;
        }
    }
    
    public <T> List<T> parseList(String source, Format format, Class<T> resourceType) {
        switch (format) {
            case JSON:
                return parseJSONList(source, resourceType);
            default:
                LOG.warn("Unsupported format type " + format);
                return null;
        }
    }
    
    public <T> T parseJSON(String source, Class<T> resourceType) {
        try {
            return contextResolver.getContext(ObjectMapper.class).readValue(source, resourceType);
        } catch (JsonParseException e) {
            LOG.error("Error parsing json " + source, e);
        } catch (JsonMappingException e) {
            LOG.error("Error mapping json " + source + " to a " + resourceType, e);
        } catch (IOException e) {
            LOG.error("Error, Jackson threw an IO exception while reading a string", e);
        }
        return null;
    }
    
    public <T> List<T> parseJSONList(String source, Class<T> resourceType) {
        try {
            ObjectMapper context = contextResolver.getContext(ObjectMapper.class);
            CollectionType type = context.getTypeFactory().constructCollectionType(List.class, resourceType);
            return context.readValue(source, type);
        } catch (JsonParseException e) {
            LOG.error("Error parsing json " + source, e);
        } catch (JsonMappingException e) {
            LOG.error("Error mapping json " + source + " to a " + resourceType, e);
        } catch (IOException e) {
            LOG.error("Error, Jackson threw an IO exception while reading a string", e);
        }
        return new ArrayList<T>();
    }
    
    public void setContextResolver(ContextResolver<ObjectMapper> resolver) {
        this.contextResolver = resolver;
    }
    
}
