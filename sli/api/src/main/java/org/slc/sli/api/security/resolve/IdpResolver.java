package org.slc.sli.api.security.resolve;

import java.util.Set;

import org.slc.sli.api.representation.EntityBody;

/**
 * Provides idp id given provided realm information
 * 
 * @author dkornishev
 * 
 */
public interface IdpResolver {
    public Set<EntityBody> getRealms();    
    public String getSsoInitUrl(String realmId);    
}
