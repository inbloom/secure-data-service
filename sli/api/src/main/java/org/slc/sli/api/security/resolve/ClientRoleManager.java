package org.slc.sli.api.security.resolve;

import java.util.List;

/**
 * Maps client roles to SLI roles
 * 
 * @author dkornishev
 *
 */
public interface ClientRoleManager {
    public List<String> resolveRoles(String realmId, List<String> clientRoleNames);

    public void addClientRole(String sliRoleName, String realmId, String clientRoleName);
    public void deleteClientRole(String sliRoleName, String realmId, String clientRoleName);
    public String getSliRoleName(String realmId, String clientRoleName);
}
