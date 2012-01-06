package org.slc.sli.api.security.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.resolve.impl.MongoUserLocator;

/**
 * 
 * @author dkornishev
 * 
 */
@Configuration
public class Mockery {
    
    public static final String VALID_REALM       = "realm->valid";
    public static final String VALID_USER_ID     = "id->valid";
    public static final String INVALID_USER_ID   = "~id->invalid";
    public static final String VALID_INTERNAL_ID = "id->internal->valid";
    public static final String INVALID_INTERNAL_ID = "~id->internal->invalid";
    
    @Bean(name = "mockUserLocator")
    public UserLocator mockUserLocator() {
        UserLocator locator = new MongoUserLocator();
        SLIPrincipal user = new SLIPrincipal();
        user.setId(VALID_INTERNAL_ID);
        
        return locator;
    }
}
