package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

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
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;
import org.springframework.data.mongodb.core.MongoTemplate;

public class StudentAssessmentConverterTest {
    
    @InjectMocks
    StudentAssessmentConverter saConverter = new StudentAssessmentConverter();

    @Mock
    INaturalKeyExtractor naturalKeyExtractor;
    
    @Mock
    MongoEntityRepository repo;
    
    @Mock
    MongoTemplate template;

    @Before
    public void setup() {
        naturalKeyExtractor = Mockito.mock(NaturalKeyExtractor.class);
        repo = Mockito.mock(MongoEntityRepository.class);
        template = Mockito.mock(MongoTemplate.class);
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
    
    private Entity createAssessment() {
        String entityType = "assessment";
        String entityId = "assessmentId";
        Map<String, Object> assessmentBody = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        assessmentBody.put("assessmentTitle", "assessment1");
        assessmentBody.put("assessmentId", "assessmentId");
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();
        List<Entity> embedded = new ArrayList<Entity>();
        Map<String, Object> assessmentItem1 = new HashMap<String, Object>();
        assessmentItem1.put("assessmentId", "assessmentId");
        assessmentItem1.put("identificationCode", "code1");
        assessmentItem1.put("itemCategory", "Matching");
        // create the embedded assessmentItem with id match studentAssessmentItem ref to
        // assessmentItem
        embedded.add(new MongoEntity("assessmentItem", "IDChildID", assessmentItem1, null));
        embeddedData.put("assessmentItem", embedded);
        return new MongoEntity(entityType, entityId, assessmentBody, metaData, null, null, embeddedData, null);

    }

    /*
     * subdocs in parent entity body
     */
    private Entity createDownConvertEntity() {
        String entityType = "studentAssessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("assessmentId", "assessmentId");
        Map<String, Object> metaData = new HashMap<String, Object>();
        
        List<Map<String, Object>> inBodydocs = new ArrayList<Map<String, Object>>();
        Map<String, Object> inBodydoc = new HashMap<String, Object>();
        inBodydoc.put("studentAssessmentId", "ID");
        inBodydoc.put("assessmentId", "assessmentId");
        inBodydoc.put("assessmentItemId", "IDChildID");
        inBodydoc.put("assessmentItem", new HashMap<String, Object>());
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
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUpConvertShouldCollapseAssessmentItem() {
        Entity saEntity = createUpConvertEntity();
        saEntity.getBody().put("assessmentId", "assessmentId");
        saEntity.getBody().put("studentId", "studentId");
        List<Entity> entities = Arrays.asList(saEntity);
        assertNull("studentAssessmentItem should not be in body", entities.get(0).getBody()
                .get("studentAssessmentItem"));
        Entity assessment = createAssessment();
        when(repo.getTemplate()).thenReturn(template);
        when(template.findById("assessmentId", Entity.class, EntityNames.ASSESSMENT)).thenReturn(assessment);
        saConverter.subdocToBodyField(entities);
        assertNotNull("studentAssessmentItem should be moved into body",
                entities.get(0).getBody().get("studentAssessmentItems"));
        assertNotNull(
                "assessmentItem should be collapsed into the  studentAssessmentItem",
                ((List<Map<String, Object>>) (entities.get(0).getBody().get("studentAssessmentItems"))).get(0).get(
                        "assessmentItem"));
        Map<String, Object> assessmentItemBody = (Map<String, Object>) ((List<Map<String, Object>>) (entities.get(0)
                .getBody().get("studentAssessmentItems"))).get(0).get("assessmentItem");
        assertEquals("collapsed assessmentItem should have assessmentId field is assessmentId", "assessmentId",
                assessmentItemBody.get("assessmentId"));
        assertEquals("collapsed assessmentItem should have identificationCode field is code1", "code1",
                assessmentItemBody.get("identificationCode"));
        assertEquals("collapsed assessmentItem should have itemCategory is Matching", "Matching",
                assessmentItemBody.get("itemCategory"));
        
    }
}
