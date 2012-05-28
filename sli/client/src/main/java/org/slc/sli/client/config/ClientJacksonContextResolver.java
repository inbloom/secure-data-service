package org.slc.sli.client.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Provides a Jackson context resolver that Jersey uses for deserializing from
 * JSON.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

@Provider
@Component
@Consumes("application/json")
@Produces("application/json")
public class ClientJacksonContextResolver implements ContextResolver<ObjectMapper> {
    private ObjectMapper mapper;
    
    public ClientJacksonContextResolver() {
        mapper = new ObjectMapper();
    }
    
    @Override
    public ObjectMapper getContext(Class<?> cl) {
        return mapper;
    }
}
