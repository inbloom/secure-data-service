package org.slc.sli.ingestion.transformation.normalization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Complex ID Normalizer implementation unit tests.
 *
 * @author kmyers
 *
 */
public class StudentTranscriptAssociationStudentIdComplexIdNormalizerTest {

    /* Entity having reference resolved */
    private Entity entity;
    
    /* Database query to resolve a reference */
    private NeutralQuery neutralQuery;
    
    /* Abstraction of database where data can be resolved */
    private Repository<Entity> repo;
    
    /* The normalizer being tested */
    private ComplexIdNormalizer normalizer;

    @Before
    public void setup() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentAcademicRecordId", "SAR-ID-123");

        this.entity = new MongoEntity("studentTranscriptAssociation", body);
        this.neutralQuery = new NeutralQuery();
        
        this.repo = Mockito.mock(Repository.class);
        this.normalizer = new StudentTranscriptAssociationStudentIdComplexIdNormalizer();
    }
    
    @Test(expected = IdResolutionException.class)
    public void testParameterChecking1() throws IdResolutionException {
        this.normalizer.resolveInternalId(null, this.neutralQuery, this.repo);
    }

    @Test(expected = IdResolutionException.class)
    public void testParameterChecking2() throws IdResolutionException {
        this.normalizer.resolveInternalId(this.entity, null, this.repo);
    }

    @Test(expected = IdResolutionException.class)
    public void testParameterChecking3() throws IdResolutionException {
        this.normalizer.resolveInternalId(this.entity, this.neutralQuery, null);
    }

    @Test
    public void testEntityFound() throws IdResolutionException {
        
        String resolvedStudentId = "STUDENT_ID_123";
        
        Map<String, Object> resultBody = new HashMap<String, Object>();
        resultBody.put("studentId", resolvedStudentId);
        
        Entity expectedRecord = Mockito.mock(Entity.class);
        Mockito.when(expectedRecord.getBody()).thenReturn(resultBody);

        Mockito.when(repo.findOne(Mockito.eq("studentAcademicRecord"), Mockito.any(NeutralQuery.class))).thenReturn(expectedRecord);
        
        List<String> resolvedIds = this.normalizer.resolveInternalId(entity, neutralQuery, repo);
        Assert.assertTrue(resolvedIds.size() == 1);
        Assert.assertEquals(resolvedIds.get(0), resolvedStudentId);
    }

    @Test(expected = IdResolutionException.class)
    public void testEntityNotFound() throws IdResolutionException {
        
        Mockito.when(repo.findOne(Mockito.eq("studentAcademicRecord"), Mockito.any(NeutralQuery.class))).thenReturn(null);
        
        this.normalizer.resolveInternalId(entity, neutralQuery, repo);
    }
    
}
