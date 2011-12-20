package org.slc.sli.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * NOTE: These classes and interfaces have been deprecated and replaced with the new Entity and
 * Mongo repository classes.
 * 
 * Implementation of the {@link StudentRepositoryCustom} interface. This will provide customized
 * Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 *         StudentRepositoryCustom
 */
@Deprecated
@Component
@Transactional
public class StudentRepositoryCustomImpl implements StudentRepositoryCustom {
    
    private static final Logger LOG = LoggerFactory.getLogger(StudentRepositoryCustomImpl.class);
    
    @PersistenceContext(unitName = "dalPersistence")
    EntityManager em;
    
    @Override
    public void deleteWithAssoc(int studentId) {
        LOG.info("process delete with association!");
        Student student = em.find(Student.class, studentId);
        LOG.info("find the student with id {}", student.getStudentId());
        em.remove(student);
        LOG.info("deleted the student with id {}", student.getStudentId());
        List<StudentSchoolAssociation> list = getAssocList(studentId);
        for (StudentSchoolAssociation assoc : list) {
            em.remove(assoc);
            LOG.info("deleted the association with id: {}", assoc.getAssociationId());
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<StudentSchoolAssociation> getAssocList(int studentId) {
        Query query = em.createQuery("from StudentSchoolAssociation assoc where assoc.studentId=?1");
        query.setParameter(1, studentId);
        return query.getResultList();
    }
}
