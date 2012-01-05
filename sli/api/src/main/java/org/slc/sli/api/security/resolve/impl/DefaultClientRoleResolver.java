package org.slc.sli.api.security.resolve.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import org.slc.sli.api.security.resolve.ClientRoleResolver;

/**
 * Default converter for client roles to sli roles
 * Does absolutely nothing but give back exactly same list
 * 
 * @author dkornishev
 *
 */
@Component
public class DefaultClientRoleResolver implements ClientRoleResolver {
    
    @Override
    public List<String> resolveRoles(List<String> clientRoleNames) {
        return clientRoleNames;
    }
    
}
