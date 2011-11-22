package org.slc.sli.repository.custom;

/**
 * Define the customized student repository interface. This will provide additional Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

public interface StudentRepositoryCustom {
    public void deleteWithAssoc(int studentId);
}