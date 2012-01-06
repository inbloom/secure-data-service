package org.slc.sli.api.security.resolve.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.UserLocator;

/**
 * Attempts to locate a user in SLI mongo data-store
 * 
 * @author dkornishev
 *
 */
@Component
@Primary
public class MongoUserLocator implements UserLocator {
    
    @Override
    public SLIPrincipal locate(String realm, String externalUserId) {
        return new SLIPrincipal();  // TODO fix this to query from mongo!  Dependency on Wolverine
    }
    
}
