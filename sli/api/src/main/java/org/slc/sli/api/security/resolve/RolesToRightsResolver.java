package org.slc.sli.api.security.resolve;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author dkornishev
 * 
 */
public interface RolesToRightsResolver {
    public Set<GrantedAuthority> resolveRoles(List<String> roleNames);
}
