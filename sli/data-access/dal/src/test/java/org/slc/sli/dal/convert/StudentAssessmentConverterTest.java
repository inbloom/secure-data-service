package org.slc.sli.dal.convert;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

public class StudentAssessmentConverterTest {
    
    @InjectMocks
    StudentAssessmentConverter saConverter = new StudentAssessmentConverter();

    @Mock
    INaturalKeyExtractor naturalKeyExtractor;
    
    @Before
    public void setup() {
        naturalKeyExtractor = Mockito.mock(NaturalKeyExtractor.class);
        MockitoAnnotations.initMocks(this);
    }
    
    /*
     * subdocs in embedded data
     */
    private Entity createUpConvertEntity() {
        String entityType = "studentAssessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        List<Entity> embedded = new ArrayList<Entity>();
        Map<String, Object> item2 = new HashMap<String, Object>();
        item2.put("studentAssessmentId", "ID");
        item2.put("assessmentItemId", "IDChildID");
        item2.put("abc", "somevalue");
        embedded.add(new MongoEntity("studentAssessmentItem", "IDChildID", item2, null));
        embeddedData.put("studentAssessmentItem", embedded);

        return new MongoEntity(entityType, entityId, body, metaData, null, null, embeddedData, null);
    }
    
    /*
     * subdocs in parent entity body
     */
    private Entity createDownConvertEntity() {
        String entityType = "studentAssessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        
        List<Map<String, Object>> inBodydocs = new ArrayList<Map<String, Object>>();
        Map<String, Object> inBodydoc = new HashMap<String, Object>();
        inBodydoc.put("studentAssessmentId", "ID");
        inBodydoc.put("assessmentItemId", "IDChildID");
        inBodydoc.put("abc", "somevalue");
        inBodydocs.add(inBodydoc);
        body.put("studentAssessmentItems", inBodydocs);
        
        return new MongoEntity(entityType, entityId, body, null);
    }

    @Test
    public void upconvertNoAssessmentShouldRemoveInvalidReference() {
        List<Entity> entity = Arrays.asList(createUpConvertEntity());
        assertNotNull(entity.get(0).getEmbeddedData().get("studentAssessmentItem"));
        saConverter.subdocToBodyField(entity);
        assertNull(entity.get(0).getEmbeddedData().get("studentAssessmentItem"));
    }
    
    @Test
    public void testBodyFieldToSubdoc() {
        List<Entity> entities = Arrays.asList(createDownConvertEntity());
        assertNotNull("studentAssessment body should have studentAssessmentItem",
                entities.get(0).getBody().get("studentAssessmentItems"));
        assertNull("studentAssessmentItem should not be outside the studentAssessment body", entities.get(0)
                .getEmbeddedData().get("studentAssessmentItem"));
        saConverter.bodyFieldToSubdoc(entities);
        assertNull("studentAssessment body should not have studentAssessmentItem",
                entities.get(0).getBody().get("studentAssessmentItems"));
        assertNotNull("studentAssessmentItem should be moved outside body",
                entities.get(0).getEmbeddedData().get("studentAssessmentItem"));
    }
    
    // verify the subdoc Did is generated during down conversion(body field to subdoc)
    @SuppressWarnings("unchecked")
    @Test
    public void testSubdocDid() {
        List<Entity> entities = Arrays.asList(createDownConvertEntity());
        assertNull(
                "studentAssessmentItem should not have id before transformation",
                ((List<Map<String, Object>>) (entities.get(0).getBody().get("studentAssessmentItems"))).get(0).get(
                        "_id"));
        saConverter.bodyFieldToSubdoc(entities);
        assertFalse("subdoc id should be generated", entities.get(0).getEmbeddedData().get("studentAssessmentItem")
                .get(0).getEntityId().isEmpty());

    }

}
