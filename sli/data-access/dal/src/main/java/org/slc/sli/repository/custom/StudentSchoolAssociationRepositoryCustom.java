package org.slc.sli.repository.custom;

import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Define the customized student school association repository interface. This will provide additional Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

public interface StudentSchoolAssociationRepositoryCustom {
    
    
    /**
     * @param ssa StudentSchoolAssociation 
     * @return StudentSchoolAssociation if student and school related to this association exist, otherwise throw DataAccessException
     */
    public StudentSchoolAssociation saveWithAssoc(StudentSchoolAssociation ssa);

}
