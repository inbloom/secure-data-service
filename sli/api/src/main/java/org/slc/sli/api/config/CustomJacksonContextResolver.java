package org.slc.sli.api.config;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.springframework.stereotype.Component;

/**
 * Provides a Jackson context resolver that Jersey uses for serializing to JSON.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

@Provider
@Component
@Produces("application/json")
public class CustomJacksonContextResolver implements ContextResolver<ObjectMapper> {
    
    private ObjectMapper mapper = new ObjectMapper();
    
    public CustomJacksonContextResolver() {
        SerializationConfig serConfig = mapper.getSerializationConfig();
        serConfig.set(Feature.FAIL_ON_EMPTY_BEANS, false);
    }
    
    @Override
    public ObjectMapper getContext(Class<?> cl) {
        
        return mapper;
        
    }
}
