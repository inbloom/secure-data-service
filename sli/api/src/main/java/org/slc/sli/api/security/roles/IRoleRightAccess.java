package org.slc.sli.api.security.roles;

import org.slc.sli.api.security.enums.Right;

import java.util.List;

/**
 * An interface to for creating a layer to access and manipulate role and right data.
 */
public interface IRoleRightAccess {

    /**
     * Returns a role that was found with the name specified.
     * @param name the name of the role you'd like to find (eg: Educator)
     * @return the role that is stored or null
     */
    public abstract Role findRoleByName(String name);

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
     * Returns a right object based on the name.
     *
     * Won't be implemeneted for a long time (YANGNI)
     * @param name the name of the right to find (eg: READ_GENERAL)
     * @return the right object that represents the right
     */
    public abstract Right getRightByName(String name);

    /**
     * Returns a list of all known rights
     *
     * This is an expensive operation, it must go through all roles first then get their rights out.
     *
     * @return A list of all right objects that were kept in the db
     */
    public abstract List<Right> fetchAllRights();

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
