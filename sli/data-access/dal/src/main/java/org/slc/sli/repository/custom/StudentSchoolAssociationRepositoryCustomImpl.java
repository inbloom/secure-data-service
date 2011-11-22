package org.slc.sli.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Implementation of the {@link StudentSchoolAssociationRepositoryCustom} interface. This will provide customized Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 * StudentRepositoryCustom
 */

/**
 * @author dliu
 *
 */
@Component
@Transactional
public class StudentSchoolAssociationRepositoryCustomImpl implements StudentSchoolAssociationRepositoryCustom {
    
    private static final Logger LOG = LoggerFactory.getLogger(StudentRepositoryCustomImpl.class);
    
    @PersistenceContext(unitName = "dalPersistence")
    EntityManager em;

    
    @Override
    public StudentSchoolAssociation saveWithAssoc(StudentSchoolAssociation ssa) {
      
        LOG.info("process customized save with association method!");
     if (ssa != null) {
         Integer studentId = ssa.getStudentId();
         Integer schoolId = ssa.getSchoolId();
         if (checkStudent(studentId) && checkSchool(schoolId)) {
             Integer assocId = ssa.getAssociationId();
             if (assocId != null && em.find(StudentSchoolAssociation.class, assocId) != null) {
                 LOG.info("updated the existing association!");
                 return em.merge(ssa);
             } else {
                 LOG.info("saved the new association!");
                 em.persist(ssa);
                 return ssa;
             } 
         } else {
             LOG.info("student or school doesnt exist, cant save/update association!");
             throw new DataAccessException("student/school doesnt exist, cant save/update association!"){};
         }
     }
     LOG.info("association is null, cant save/update association!");
     return null;
  }
    
    
    
    /**
     * @param studentId student id for student school association
     * @return true if student exists otherwise return false
     */
    private boolean checkStudent(Integer studentId) {
        if (studentId == null)
        return false;
        else {
            Query query = em.createQuery("from Student student where student.studentId=?1");
            query.setParameter(1, studentId);
            try {
                query.getSingleResult();
            } catch (Exception e) { 
                return false;
            }
           
        }
        return true;
    }
   
    
    /**
     * @param schoolId school id for student school association
     * @return true if school exist otherwise return false
     */
    private boolean checkSchool(Integer schoolId) {
        if (schoolId == null)
        return false;
        else {
            Query query = em.createQuery("from School school where school.schoolId=?1");
            query.setParameter(1, schoolId);
            try {
                query.getSingleResult();
            } catch (Exception e) {
                return false;  
            }
            }
        return true;
    }
}
