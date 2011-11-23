package org.slc.sli.repository;

import org.slc.sli.domain.Student;
import org.slc.sli.repository.custom.StudentRepositoryCustom;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


/**
 * Provides a repository interface for basic CRUD operations on Students.  Uses Spring Data component scan to generate
 * an instance of this at runtime.
 * @author Sean Melody <smelody@wgen.net>
 *
 */

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, Integer>, StudentRepositoryCustom {

    public Iterable<Student> findByStudentSchoolId(String studentSchoolId);
}
