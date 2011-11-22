package org.slc.sli.cuketests.util;

import javax.ws.rs.ext.ContextResolver;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

public class JerseyClientFactory {
    
    public static class CustomJacksonContextResolver implements ContextResolver<ObjectMapper> {
        
        private ObjectMapper mapper = new ObjectMapper();
        
        public CustomJacksonContextResolver() {
        }
        
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
    
    public static Client createClient() {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        return Client.create(cc);
    }
}
