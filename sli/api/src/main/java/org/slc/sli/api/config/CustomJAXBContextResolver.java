package org.slc.sli.api.config;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Student;

/**
 * Custom JAXB Context Resolver that will generate JSON in using the "natural"
 * JSON configuration.
 * 
 * */
@Provider
@Component
@Produces("application/xml")
public class CustomJAXBContextResolver implements ContextResolver<Object> {
    
    private JAXBContext context;
    
    private Class<?>[] types = {Student.class};
    
    private Logger log = LoggerFactory.getLogger(ContextResolver.class);;
    
    public CustomJAXBContextResolver() throws Exception {
        context = JAXBContext.newInstance(types);
        
        log.debug("JAXB Context {}", context);
    }
    
    @SuppressWarnings("rawtypes")
    public JAXBContext getContext(Class objectType) {
        for (Class c : types) {
            if (c.equals(objectType))
                return (context);
        }
        return (null);
    }
}
