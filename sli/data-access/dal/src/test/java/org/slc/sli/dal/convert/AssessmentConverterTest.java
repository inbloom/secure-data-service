/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dal.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * JUnit for assessment converter
 */
public class AssessmentConverterTest {

    @InjectMocks
    AssessmentConverter assessmentConverter = new AssessmentConverter();

    @Mock
    INaturalKeyExtractor naturalKeyExtractor;
    
    @Mock
    MongoEntityRepository repo;
    
    @Mock
    MongoTemplate template;

    private Map<String, Object> item1 = new HashMap<String, Object>();
    private Map<String, Object> item2 = new HashMap<String, Object>();
    private Map<String, Object> item3 = new HashMap<String, Object>();
    private Map<String, Object> item4 = new HashMap<String, Object>();
    private Entity assessmentPeriodDescriptor;
    private Entity assessmentFamilyC;
    private Entity assessmentFamilyB;
    private Entity assessmentFamilyA;

    @Before
    public void setup() throws NoNaturalKeysDefinedException {
        item1.put("assessmentId", "ID");
        item1.put("_id", "item1");
        item1.put("abc", "somevalue1");
        item2.put("assessmentId", "ID");
        item2.put("_id", "item2");
        item2.put("abc", "somevalue2");
        item3.put("assessmentId", "ID");
        item3.put("_id", "item2");
        item3.put("abc", "somevalue3");
        item4.put("assessmentId", "ID");
        item4.put("_id", "item4");
        item4.put("abc", "somevalue4");
        
        Map<String, Object> apdBody = new HashMap<String, Object>();
        apdBody.put("codeValue", "green");
        assessmentPeriodDescriptor = new MongoEntity("assessmentPeriodDescriptor", "mydescriptorid", apdBody, null);
        
        Map<String, Object> rootBody = new HashMap<String, Object>();
        rootBody.put("assessmentFamilyTitle", "A");
        assessmentFamilyA = new MongoEntity("assessmentFamily", "AF-A", rootBody, null);
        
        Map<String, Object> parentBody = new HashMap<String, Object>();
        parentBody.put("assessmentFamilyTitle", "B");
        parentBody.put(AssessmentConverter.ASSESSMENT_FAMILY_ASSESSMENT_FAMILY_REFERENCE, "AF-A");
        assessmentFamilyB = new MongoEntity("assessmentFamily", "AF-B", parentBody, null);
        
        Map<String, Object> leafBody = new HashMap<String, Object>();
        leafBody.put("assessmentFamilyTitle", "C");
        leafBody.put(AssessmentConverter.ASSESSMENT_FAMILY_ASSESSMENT_FAMILY_REFERENCE, "AF-B");
        assessmentFamilyC = new MongoEntity("assessmentFamily", "AF-C", leafBody, null);
        
        naturalKeyExtractor = Mockito.mock(NaturalKeyExtractor.class);
        repo = Mockito.mock(MongoEntityRepository.class);
        template = Mockito.mock(MongoTemplate.class);
        MockitoAnnotations.initMocks(this);
        
        when(repo.getTemplate()).thenReturn(template);
        when(repo.update(eq(EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR), any(Entity.class), eq(false))).thenReturn(true);
        when(template.findById("mydescriptorid", Entity.class, EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR)).thenReturn(assessmentPeriodDescriptor);
        when(template.findById("AF-A", Entity.class, EntityNames.ASSESSMENT_FAMILY)).thenReturn(assessmentFamilyA);
        when(template.findById("AF-B", Entity.class, EntityNames.ASSESSMENT_FAMILY)).thenReturn(assessmentFamilyB);
        when(template.findById("AF-C", Entity.class, EntityNames.ASSESSMENT_FAMILY)).thenReturn(assessmentFamilyC);
    }

    /*
     * subdocs in embedded data
     */
    private Entity createUpConvertEntity() {
        String entityType = "assessment";
        String entityId = "ID";
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("assessmentPeriodDescriptorId", "mydescriptorid");
        body.put(AssessmentConverter.ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE, assessmentFamilyC.getEntityId());
        Map<String, Object> metaData = new HashMap<String, Object>();
        Map<String, List<Entity>> embeddedData = new HashMap<String, List<Entity>>();

        embeddedData.put("assessmentItem", Arrays.asList((Entity) new MongoEntity("assessmentItem", "item1", item1,
                null), (Entity) new MongoEntity("assessmentItem", "item2", item2, null), (Entity) new MongoEntity(
                        "assessmentItem", "item3", item3, null), (Entity) new MongoEntity(
                                "assessmentItem", "item4", item4, null)));

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
        Map<String, Object> apdBody = new HashMap<String, Object>();
        apdBody.put("codeValue", "green");
        apdBody.put("description", "something not really useful");
        body.put("assessmentPeriodDescriptor", apdBody);
        body.put(AssessmentConverter.ASSESSMENT_FAMILY_HIERARCHY_STRING, "A.B.C");
        return new MongoEntity(entityType, entityId, body, metaData);
    }

    @Test
    public void upconvertShouldRemoveAPD_references() {
        Entity entity = createUpConvertEntity();
        assessmentConverter.subdocToBodyField(entity);
        assertNull(entity.getBody().get("assessmentPeriodDescriptorId"));
    }
    
    @Test
    public void upconvertNoEmbeddedSubdocShouldRemainUnchanged() {
        List<Entity> entity = Arrays.asList(createDownConvertEntity());
        Entity clone = createDownConvertEntity();
        assessmentConverter.subdocToBodyField(entity);
        assertEquals(clone.getBody(), entity.get(0).getBody());
    }
    
    @Test
    public void upconvertShouldConstructAssessmentFamilyHierarchy() {
        Entity entity = createUpConvertEntity();
        assessmentConverter.subdocToBodyField(entity);
        assertNull(entity.getBody().get(AssessmentConverter.ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE));
        assertEquals("A.B.C", entity.getBody().get(AssessmentConverter.ASSESSMENT_FAMILY_HIERARCHY_STRING));
    }
    
    @Test
    public void downconvertShouldDeleteAssessmentFamilyHierarchy() {
        Entity entity = createDownConvertEntity();
        assessmentConverter.bodyFieldToSubdoc(entity);
        assertNull(entity.getBody().get(AssessmentConverter.ASSESSMENT_FAMILY_HIERARCHY_STRING));
    }
    
    @Test
    public void downconvertShouldDeleteAssessmentFamilyHierarchyExistingAssessment() {
        // if an update of an existing assessment is performed, the assessmentFamilyReference
        // should remain part of the assessment after the update occurs.
        Entity existingAssessment = createDownConvertEntity();
        existingAssessment.getBody().put(AssessmentConverter.ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE, assessmentFamilyA.getEntityId());
        when(template.findById(existingAssessment.getEntityId(), Entity.class, EntityNames.ASSESSMENT)).thenReturn(existingAssessment);
        
        Entity updatedAssessment = createDownConvertEntity();
        updatedAssessment.getBody().put("assessmentTitle", "A_new_title");
        
        assessmentConverter.bodyFieldToSubdoc(updatedAssessment);
        assertNull(updatedAssessment.getBody().get(AssessmentConverter.ASSESSMENT_FAMILY_HIERARCHY_STRING));
        assertEquals(existingAssessment.getBody().get(AssessmentConverter.ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE),
                updatedAssessment.getBody().get(AssessmentConverter.ASSESSMENT_ASSESSMENT_FAMILY_REFERENCE));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void upconvertEmbeddedSubdocShouldMoveInsideBody() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Entity entity = createUpConvertEntity();
        Entity clone = createUpConvertEntity();
        assertNull(entity.getBody().get("assessmentItem"));
        assessmentConverter.subdocToBodyField(entity);
        assertNotNull(entity.getBody().get("assessmentItem"));
        assertEquals(clone.getEmbeddedData().get("assessmentItem").get(0).getBody().get("abc"),
                ((List<Map<String, Object>>) (entity.getBody().get("assessmentItem"))).get(0).get("abc"));
        assertEquals("somevalue1", PropertyUtils.getProperty(entity, "body.assessmentItem.[0].abc"));
        assertEquals("somevalue2", PropertyUtils.getProperty(entity, "body.assessmentItem.[1].abc"));
        assertNull(entity.getEmbeddedData().get("assessmentItem"));
        //assessmentPeriodDescriptorId should have been removed from entity body 
        assertNull(entity.getBody().get("assessmentPeriodDescriptorId"));
        assertNotNull(entity.getBody().get("assessmentPeriodDescriptor"));
        assertEquals(((Map<String, Object>)(entity.getBody().get("assessmentPeriodDescriptor"))).get("codeValue"),
        assessmentPeriodDescriptor.getBody().get("codeValue"));
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
        assertNull(entity.getBody().get("assessmentPeriodDescriptor"));
        assertNotNull(entity.getBody().get("assessmentPeriodDescriptorId"));
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
    public void invalidApdIdShouldBeFilteredOutInUp() {
        when(template.findById("mydescriptorid", Entity.class, EntityNames.ASSESSMENT_PERIOD_DESCRIPTOR)).thenReturn(null);
        Entity entity = createUpConvertEntity();
        assessmentConverter.subdocToBodyField(Arrays.asList(entity));
        assertNull(entity.getBody().get("assessmentPeriodDescriptor"));
        assertNull(entity.getBody().get("assessmentPeriodDescriptorId"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testHierachyInObjectiveAssessmentsToAPI() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Entity assessment = createHierarchicalUpConvertEntity();
        assessmentConverter.subdocToBodyField(assessment);
        List<Map<String, Object>> oas = (List<Map<String, Object>>) PropertyUtils.getProperty(assessment,
                "body.objectiveAssessment");
        assertEquals(1, oas.size());
        assertEquals("ParentOA", PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].title"));
        assertEquals("Child1",
                PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[0].title"));
        assertEquals("Child2",
                PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[1].title"));
        assertEquals("GrandChild", PropertyUtils.getProperty(assessment,
                "body.objectiveAssessment.[0].objectiveAssessments.[0].objectiveAssessments.[0].title"));
    }

    private Entity createHierarchicalUpConvertEntity() {
        Entity assessment = createUpConvertEntity();
        Map<String, Object> parentOABody = new HashMap<String, Object>();
        parentOABody.put("assessmentId", "ID");
        parentOABody.put("title", "ParentOA");
        parentOABody.put("identificationCode", "ParentOA");
        parentOABody.put("subObjectiveAssessment", Arrays.asList("Child1", "Child2"));
        Entity parentOA = new MongoEntity("objectiveAssessment", "OA1", parentOABody, null);
        Map<String, Object> child1Body = new HashMap<String, Object>();
        child1Body.put("assessmentId", "ID");
        child1Body.put("title", "Child1");
        child1Body.put("identificationCode", "Child1");
        child1Body.put("subObjectiveAssessment", Arrays.asList("NA", "GrandChild"));
        child1Body.put("assessmentItemRefs", Arrays.asList("item1", "item3"));
        Entity child1 = new MongoEntity("objectiveAssessment", "OA2", child1Body, null);
        Map<String, Object> child2Body = new HashMap<String, Object>();
        child2Body.put("assessmentId", "ID");
        child2Body.put("title", "Child2");
        child2Body.put("identificationCode", "Child2");
        child2Body.put("assessmentItemRefs", Arrays.asList("item2", "NA"));
        Entity child2 = new MongoEntity("objectiveAssessment", "OA3", child2Body, null);
        Map<String, Object> grandChildBody = new HashMap<String, Object>();
        grandChildBody.put("assessmentId", "ID");
        grandChildBody.put("title", "GrandChild");
        grandChildBody.put("identificationCode", "GrandChild");
        Entity grandChild = new MongoEntity("objectiveAssessment", "OA4", grandChildBody, null);
        assessment.getEmbeddedData().put("objectiveAssessment", Arrays.asList(parentOA, child1, child2, grandChild));
        return assessment;
    }

    @Test
    public void testHierachyInObjectiveAssessmentsToDAL() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Entity assessment = createHierarchicalDownConvertEntity();
        assessmentConverter.bodyFieldToSubdoc(assessment);
        assertEquals("ParentOA",
                PropertyUtils.getProperty(assessment, "embeddedData.objectiveAssessment.[3].body.title"));
        assertEquals("Child1", PropertyUtils.getProperty(assessment,
                "embeddedData.objectiveAssessment.[3].body.subObjectiveAssessment.[0]"));
        assertEquals("Child2", PropertyUtils.getProperty(assessment,
                "embeddedData.objectiveAssessment.[3].body.subObjectiveAssessment.[1]"));
        assertEquals("Child1", PropertyUtils.getProperty(assessment, "embeddedData.objectiveAssessment.[1].body.title"));
        assertEquals("GrandChild", PropertyUtils.getProperty(assessment,
                "embeddedData.objectiveAssessment.[1].body.subObjectiveAssessment.[0]"));
        assertEquals("Child2", PropertyUtils.getProperty(assessment, "embeddedData.objectiveAssessment.[2].body.title"));
        assertEquals("GrandChild",
                PropertyUtils.getProperty(assessment, "embeddedData.objectiveAssessment.[0].body.title"));
    }

    @SuppressWarnings("unchecked")
    private Entity createHierarchicalDownConvertEntity() {
        Entity assessment = createDownConvertEntity();
        Map<String, Object> grandChild = new HashMap<String, Object>();
        grandChild.put("assessmentId", "ID");
        grandChild.put("title", "GrandChild");
        grandChild.put("identificationCode", "GrandChild");
        Map<String, Object> child1 = new HashMap<String, Object>();
        child1.put("assessmentId", "ID");
        child1.put("title", "Child1");
        child1.put("identificationCode", "Child1");
        child1.put("objectiveAssessments", Arrays.asList(grandChild));
        child1.put("assessmentItem", Arrays.asList(item1, item2));
        Map<String, Object> child2 = new HashMap<String, Object>();
        child2.put("assessmentId", "ID");
        child2.put("title", "Child2");
        child2.put("identificationCode", "Child2");
        child2.put("assessmentItem", Arrays.asList(item3));
        Map<String, Object> parentOA = new HashMap<String, Object>();
        parentOA.put("assessmentId", "ID");
        parentOA.put("title", "ParentOA");
        parentOA.put("identificationCode", "ParentOA");
        parentOA.put("objectiveAssessments", Arrays.asList(child1, child2));
        assessment.getBody().put("objectiveAssessment", Arrays.asList(parentOA));
        return assessment;
    }

    @Test
    public void testAssessmentsWithAIsInOAsToAPI() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Entity assessment = createHierarchicalUpConvertEntity();
        assessmentConverter.subdocToBodyField(assessment);
        assertEquals("somevalue1", PropertyUtils.getProperty(assessment,
                "body.objectiveAssessment.[0].objectiveAssessments.[0].assessmentItem.[0].abc"));
        assertEquals("somevalue2", PropertyUtils.getProperty(assessment,
                "body.objectiveAssessment.[0].objectiveAssessments.[1].assessmentItem.[0].abc"));
        assertEquals("somevalue3", PropertyUtils.getProperty(assessment,
                "body.objectiveAssessment.[0].objectiveAssessments.[0].assessmentItem.[1].abc"));
        assertEquals("somevalue4", PropertyUtils.getProperty(assessment, "body.assessmentItem.[0].abc"));
        assertNull(PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[0].assessmentItem.[0]._id"));
        assertNull(PropertyUtils.getProperty(assessment, "body.objectiveAssessment.[0].objectiveAssessments.[0].assessmentItem.[0].assessmentId"));
    }

    @Test
    public void testAssessmentsWithAIsInOAsToDAL() throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Entity assessment = createHierarchicalDownConvertEntity();
        assessmentConverter.bodyFieldToSubdoc(assessment);
        assertEquals("1", PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[0].body.a"));
        assertEquals("somevalue1", PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[1].body.abc"));
        assertEquals("somevalue2", PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[2].body.abc"));
        assertEquals("somevalue3", PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[3].body.abc"));
        assertNotNull(PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[0].entityId"));
        assertNotNull(PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[1].entityId"));
        assertNotNull(PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[2].entityId"));
        assertNotNull(PropertyUtils.getProperty(assessment, "embeddedData.assessmentItem.[3].entityId"));

    }
}
