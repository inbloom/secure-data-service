package org.slc.sli.api.security.resolve;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author dkornishev
 * 
 */
public interface RolesToRightsResolver {
    public List<GrantedAuthority> resolveRoles(List<String> roleNames);
}
