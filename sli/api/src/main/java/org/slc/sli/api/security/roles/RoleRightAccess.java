package org.slc.sli.api.security.roles;


import java.util.List;

/**
 * An interface to for creating a layer to access and manipulate role and right data.
 */
public interface RoleRightAccess {

    /**
     * Returns a role that was found with the name specified.
     * @param name the name of the role you'd like to find (eg: Educator)
     * @return the role that is stored or null
     */
    public abstract Role findRoleByName(String name);

    /**
     * Tries to return a default role based on the name.
     * @param name the name of the role (eg: Educator)
     * @return the Role representing the default or null.
     */
    public abstract Role getDefaultRole(String name);

    /**
     * Returns a role that was found by the specified Spring name.
     * @param springName the name that spring understands this role to have (eg ROLE_EDUCATOR)
     * @return the found Role or null
     */
    public abstract Role findRoleBySpringName(String springName);

    /**
     * Returns a list of all of the roles with their rights that are in our system.
     * @return a list of roles with their rights.
     */
    public abstract List<Role> fetchAllRoles();

    /**
     * Adds a new role to the database.
     * @param role The role object to add.
     * @return a boolean to indicate success or failure.
     */
    public abstract boolean addRole(Role role);

    /**
     * Attempts to remove the specified role from the db.
     * @param role A role that was previously retrieved from the db (Needs the ID)
     * @return a boolean to indicate success or failure.
     */
    public abstract boolean deleteRole(Role role);

    /**
     * An update method for a role.
     * @param role an updated role object that was previously retrieved from the db (Needs the id).
     * @return boolean indicating success or failure.
     */
    public abstract boolean updateRole(Role role);
    
}
