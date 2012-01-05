package org.slc.sli.api.security.resolve;

import java.util.List;

/**
 * Maps client roles to SLI roles
 * 
 * @author dkornishev
 *
 */
public interface ClientRoleResolver {
    public List<String> resolveRoles(List<String> clientRoleNames);
}
