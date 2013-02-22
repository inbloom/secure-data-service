package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

/**
 * JUnit for assessment converter
 */
public class AssessmentConverterTest {

    @InjectMocks
    AssessmentConverter assessmentConverter = new AssessmentConverter();

    @Mock
    INaturalKeyExtractor naturalKeyExtractor;

    @Before
    public void setup() throws NoNaturalKeysDefinedException {
        naturalKeyExtractor = Mockito.mock(NaturalKeyExtractor.class);
        MockitoAnnotations.initMocks(this);
    }

    /*
     * subdocs in embedded data
     */
    private Entity createUpConvertEntity() {
        String entityType = "assessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        List<Entity> embedded = new ArrayList<Entity>();
        Map<String, Object> item2 = new HashMap<String, Object>();
        item2.put("assessmentId", "ID");
        item2.put("_id", "IDChildID");
        item2.put("abc", "somevalue");
        embedded.add(new MongoEntity("assessmentItem", null, item2, null));
        embeddedData.put("assessmentItem", embedded);

        return new MongoEntity(entityType, entityId, body, metaData, null, null, embeddedData, null);
    }

    /*
     * subdocs inside body
     */
    private Entity createDownConvertEntity() {
        String entityType = "assessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, Object> metaData = new HashMap<String, Object>();

        List<Map<String, Object>> insideBody = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("a", "1");
        insideBody.add(item);
        body.put("assessmentItem", insideBody);

        return new MongoEntity(entityType, entityId, body, metaData);
    }

    @Test
    public void upconvertNoEmbeddedSubdocShouldRemainUnchanged() {
        List<Entity> entity = Arrays.asList(createDownConvertEntity());
        Entity clone = createDownConvertEntity();
        assessmentConverter.subdocToBodyField(entity);
        assertEquals(clone.getBody(), entity.get(0).getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void upconvertEmbeddedSubdocShouldMoveInsideBody() {
        Entity entity = createUpConvertEntity();
        Entity clone = createUpConvertEntity();
        assertNull(entity.getBody().get("assessmentItem"));
        assessmentConverter.subdocToBodyField(entity);
        assertNotNull(entity.getBody().get("assessmentItem"));
        assertEquals(clone.getEmbeddedData().get("assessmentItem").get(0).getBody().get("abc"),
                ((List<Map<String, Object>>) (entity.getBody().get("assessmentItem"))).get(0).get("abc"));
        assertEquals(((List<Map<String, Object>>) (entity.getBody().get("assessmentItem"))).get(0).get("abc"),
                "somevalue");
        assertNull(entity.getEmbeddedData().get("assessmentItem"));
    }

    @Test
    public void downconvertBodyHasNoSubdocShouldNotChange() throws NoNaturalKeysDefinedException {
        List<Entity> entity = Arrays.asList(createUpConvertEntity());
        Entity clone = createUpConvertEntity();
        assessmentConverter.bodyFieldToSubdoc(entity);
        assertEquals(clone.getBody(), entity.get(0).getBody());
    }

    @Test
    public void downconvertBodyToSubdoc() {
        Entity entity = createDownConvertEntity();
        assessmentConverter.bodyFieldToSubdoc(entity);
        assertNull(entity.getBody().get("assessmentItem"));
        assertNotNull(entity.getEmbeddedData().get("assessmentItem"));
    }

    @Test
    public void bodyToSubdocGenerateId() {
        Entity entity = createDownConvertEntity();
        assessmentConverter.bodyFieldToSubdoc(entity);
        assertNotNull("should move assessmentItem from in body to out body",
                entity.getEmbeddedData().get("assessmentItem"));
        assertEquals("should have one assessmentItem in subdoc field", 1, entity.getEmbeddedData()
                .get("assessmentItem").size());
        String id = entity.getEmbeddedData().get("assessmentItem").get(0).getEntityId();
        assertNotNull("should generate id for subdoc entity", id);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHierachyInObjectiveAssessments() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Entity assessment = createUpConvertEntity();
        Map<String, Object> parentOABody = new HashMap<String, Object>();
        parentOABody.put("assessmentId", "ID");
        parentOABody.put("title", "ParentOA");
        parentOABody.put("subObjectiveAssessment", Arrays.asList("Child1", "Child2"));
        Entity parentOA = new MongoEntity("objectiveAssessment", "ParentOA", parentOABody, null);
        Map<String, Object> child1Body = new HashMap<String, Object>();
        child1Body.put("assessmentId", "ID");
        child1Body.put("title", "Child1");
        child1Body.put("subObjectiveAssessment", Arrays.asList("NA", "GrandChild"));
        Entity child1 = new MongoEntity("objectiveAssessment", "Child1", child1Body, null);
        Map<String, Object> child2Body = new HashMap<String, Object>();
        child2Body.put("assessmentId", "ID");
        child2Body.put("title", "Child2");
        Entity child2 = new MongoEntity("objectiveAssessment", "Child2", child2Body, null);
        Map<String, Object> grandChildBody = new HashMap<String, Object>();
        grandChildBody.put("assessmentId", "ID");
        grandChildBody.put("title", "GrandChild");
        Entity grandChild = new MongoEntity("objectiveAssessment", "GrandChild", grandChildBody, null);
        assessment.getEmbeddedData().put("objectiveAssessment", Arrays.asList(parentOA, child1, child2, grandChild));
        assessmentConverter.subdocToBodyField(assessment);
        List<Map<String, Object>> oas = (List<Map<String, Object>>) PropertyUtils.getProperty(assessment, "body.objectiveAssessment");
        assertEquals(1, oas.size());
        assertEquals("ParentOA", PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].title"));
        assertEquals("Child1", PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[0].title"));
        assertEquals("Child2", PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[1].title"));
        assertEquals("GrandChild", PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[0].objectiveAssessments.[0].title"));
    }
}
