package org.slc.sli.repository.custom;

/**
 * Define the customized school repository interface. This will provide additional Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

public interface SchoolRepositoryCustom {
    public void deleteWithAssoc(int schoolId);
}
