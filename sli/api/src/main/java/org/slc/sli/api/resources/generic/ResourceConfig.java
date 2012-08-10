package org.slc.sli.api.resources.generic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/8/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class ResourceConfig {
    private Map<String, Set<String>> resourceMethods = new ConcurrentHashMap<String, Set<String>>();

    @Bean(name = "resourceSupportedMethods")
    public Map<String, Set<String>> getResourceMethods() {
        return resourceMethods;
    }

}
