package org.slc.sli.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.slc.sli.domain.School;
import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Implementation of the {@link SchoolRepositoryCustom} interface. This will provide customized
 * Persistence
 * method compare to default CRUD methods supported by JPA Repository
 * 
 * @author Dong Liu dliu@wgen.net
 * 
 */

@Component
@Transactional
public class SchoolRepositoryCustomImpl implements SchoolRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(SchoolRepositoryCustomImpl.class);

    @PersistenceContext(unitName = "dalPersistence")
    EntityManager em;

    @Override
    public void deleteWithAssoc(int schoolId) {
        LOG.info("process delete with association!");
        School school = em.find(School.class, schoolId);
        LOG.info("find the school with id {}", school.getSchoolId());
        em.remove(school);
        LOG.info("deleted the school with id {}", school.getSchoolId());
        List<StudentSchoolAssociation> list = getAssocList(schoolId);
        for (StudentSchoolAssociation assoc : list) {
            em.remove(assoc);
            LOG.info("deleted the association with id: {}", assoc.getAssociationId());
        }
    }

    @SuppressWarnings("unchecked")
    private List<StudentSchoolAssociation> getAssocList(int schoolId) {
        Query query = em.createQuery("from StudentSchoolAssociation assoc where assoc.schoolId=?1");
        query.setParameter(1, schoolId);
        return query.getResultList();
    }
}
