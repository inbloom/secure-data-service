package org.slc.sli.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.slc.sli.domain.School;
import org.slc.sli.repository.custom.SchoolRepositoryCustom;

/**
 * NOTE: These classes and interfaces have been deprecated and replaced with the new Entity and
 * Mongo repository classes.
 * 
 * Old repository for schools
 * 
 * @deprecated
 */
@Repository
@Deprecated
public interface SchoolRepository extends PagingAndSortingRepository<School, Integer>, SchoolRepositoryCustom {
    
    public Iterable<School> findByStateOrganizationId(String stateOrganizationId);
}
