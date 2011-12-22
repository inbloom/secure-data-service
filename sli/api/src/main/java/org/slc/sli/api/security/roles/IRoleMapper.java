package org.slc.sli.api.security.roles;

import org.springframework.security.core.authority.GrantedAuthorityImpl;
import java.util.List;

/**
 * Simple interface for role mapping. Will need to grow and change as we better understand the domain of configurable
 * roles.
 */

public interface IRoleMapper {

    public List<GrantedAuthorityImpl> buildMappedRoles();
}
