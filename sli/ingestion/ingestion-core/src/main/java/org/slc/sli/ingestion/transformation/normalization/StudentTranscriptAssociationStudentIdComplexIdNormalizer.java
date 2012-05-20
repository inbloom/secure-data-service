package org.slc.sli.ingestion.transformation.normalization;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the method that should be used to resolve a studentTranscriptAssociation's
 * studentId field.
 * 
 * @author kmyers
 * 
 */
public class StudentTranscriptAssociationStudentIdComplexIdNormalizer implements ComplexIdNormalizer {
    private static final Logger LOG = LoggerFactory
            .getLogger(StudentTranscriptAssociationStudentIdComplexIdNormalizer.class);
    
    /**
     * Resolves the specified field's reference and returns the associated ID. Returns an empty
     * list if ID cannot be resolved or if this class is not aware how to resolve that field.
     * 
     * @param entity
     *            entity containing field that needs to be resolved
     * @param neutralQuery
     *            a query where "tenantId" is already specified
     * @param entityRepository
     *            access to execute query
     * @return resolved ID or an empty list
     */
    public List<String> resolveInternalId(Entity entity, NeutralQuery neutralQuery, Repository<Entity> repo)
            throws IdResolutionException {
        LOG.debug("resolving id for {}", entity);
        if (entity == null || neutralQuery == null || repo == null) {
            throw new IdResolutionException("Missing parameters when attempting to resolve complex reference",
                    "studentTranscriptAssociation.studentId", null);
        }
        
        String externalStudentAcademicRecordId = (String) entity.getBody().remove("studentAcademicRecordId");
        
        neutralQuery
                .addCriteria(new NeutralCriteria("metaData.externalId", "=", externalStudentAcademicRecordId, false));
        
        // query for the student academic record with the given external ID
        // the student ID it contains is what this method will return
        Entity studentAcademicRecord = repo.findOne("studentAcademicRecord", neutralQuery);
        
        if (studentAcademicRecord == null) {
            throw new IdResolutionException("Unable to resolve complex reference",
                    "studentTranscriptAssociation.studentId", externalStudentAcademicRecordId);
        }
        
        List<String> ids = new ArrayList<String>();
        ids.add((String) studentAcademicRecord.getBody().get("studentId"));
        return ids;
    }
}
