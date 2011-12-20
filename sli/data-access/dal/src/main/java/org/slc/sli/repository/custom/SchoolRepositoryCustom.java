package org.slc.sli.repository.custom;

/**
 * NOTE: These classes and interfaces have been deprecated and replaced with the new Entity and
 * Mongo repository classes.
 * 
 * Define the customized school repository interface. This will provide additional Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */
@Deprecated
public interface SchoolRepositoryCustom {
    public void deleteWithAssoc(int schoolId);
}
