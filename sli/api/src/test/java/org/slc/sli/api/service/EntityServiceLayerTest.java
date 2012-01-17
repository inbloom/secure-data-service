package org.slc.sli.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.validation.EntityValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Service layer tests for the API.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EntityServiceLayerTest {
    
    @Autowired
    private EntityDefinitionStore defs;
    private EntityDefinition studentDef;
    private EntityDefinition schoolDef;
    private AssociationDefinition studentEnrollmentDef;
    private EntityService studentService;
    private EntityService schoolService;
    private AssociationService studentSchoolAssociationService;
    @Autowired
    private EntityRepository repo;
    
    public void setSecurityContextInjector(SecurityContextInjector securityContextInjector) {
        this.securityContextInjector = securityContextInjector;
    }
    
    @Autowired
    private SecurityContextInjector securityContextInjector;
    
    @Before
    public void setUp() {
        // inject administrator security context for unit testing
        securityContextInjector.setAdminContext();
        
        repo.deleteAll("student");
        repo.deleteAll("school");
        studentDef = defs.lookupByResourceName("students");
        schoolDef = defs.lookupByResourceName("schools");
        studentEnrollmentDef = (AssociationDefinition) defs.lookupByResourceName("student-school-associations");
        studentService = studentDef.getService();
        schoolService = schoolDef.getService();
        studentSchoolAssociationService = studentEnrollmentDef.getService();
    }
    
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testCrudEntity() {
        EntityBody student = new EntityBody();
        student.put("firstName", "Andrew");
        student.put("lastName", "Wiggen");
        String id = studentService.create(student);
        EntityBody retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        student = new EntityBody(student);
        student.put("sex", "Male");
        assertTrue(studentService.update(id, student));
        retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        assertEquals(student.get("sex"), retrievedEntity.get("sex"));
        assertFalse(studentService.update(id, student));
        retrievedEntity = studentService.get(id);
        assertEquals(student.get("firstName"), retrievedEntity.get("firstName"));
        assertEquals(student.get("lastName"), retrievedEntity.get("lastName"));
        assertEquals(student.get("sex"), retrievedEntity.get("sex"));
        try {
            studentService.delete(id);
        } catch (EntityNotFoundException e) {
            fail();
        }
        try {
            EntityBody zombie = studentService.get(id);
            fail("should have not found " + zombie);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
        try {
            studentService.delete(id);
            fail("Exception should have been thrown");
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testNoSuchEntity() {
        try {
            studentService.get("NoSuchStudent");
            fail("should have thrown exception");
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
        try {
            studentService.update("NoSuchStudent", new EntityBody());
            fail("should have thrown exception");
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
        
    }
    
    @Test
    public void testMultipleEntities() {
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        EntityBody student2 = new EntityBody();
        student2.put("firstName", "Petra");
        student2.put("lastName", "Arkanian");
        EntityBody student3 = new EntityBody();
        student3.put("firstName", "Andrew");
        student3.put("lastName", "Wiggen");
        EntityBody student4 = new EntityBody();
        student4.put("firstName", "Julian");
        student4.put("lastName", "Delphiki");
        String id1 = studentService.create(student1);
        String id2 = studentService.create(student2);
        String id3 = studentService.create(student3);
        String id4 = studentService.create(student4);
        EntityBody retrievedStudent1 = studentService.get(id1);
        assertEquals(student1.get("firstName"), retrievedStudent1.get("firstName"));
        assertEquals(student1.get("lastName"), retrievedStudent1.get("lastName"));
        EntityBody retrievedStudent2 = studentService.get(id2);
        assertEquals(student2.get("firstName"), retrievedStudent2.get("firstName"));
        assertEquals(student2.get("lastName"), retrievedStudent2.get("lastName"));
        EntityBody retrievedStudent3 = studentService.get(id3);
        assertEquals(student3.get("firstName"), retrievedStudent3.get("firstName"));
        assertEquals(student3.get("lastName"), retrievedStudent3.get("lastName"));
        EntityBody retrievedStudent4 = studentService.get(id4);
        assertEquals(student4.get("firstName"), retrievedStudent4.get("firstName"));
        assertEquals(student4.get("lastName"), retrievedStudent4.get("lastName"));
        assertEquals(Arrays.asList(retrievedStudent1, retrievedStudent2, retrievedStudent3, retrievedStudent4),
                studentService.get(Arrays.asList(id1, id2, id3, id4)));
        List<String> firstSet = iterableToList(studentService.list(0, 2));
        assertEquals(2, firstSet.size());
        List<String> secondSet = iterableToList(studentService.list(2, 2));
        assertEquals(2, secondSet.size());
        Set<String> wholeSet = new HashSet<String>();
        wholeSet.addAll(firstSet);
        wholeSet.addAll(secondSet);
        assertEquals(new HashSet<String>(Arrays.asList(id1, id2, id3, id4)), wholeSet);
        studentService.delete(id1);
        studentService.delete(id2);
        studentService.delete(id3);
        studentService.delete(id4);
        assertEquals(new ArrayList<EntityBody>(), studentService.list(0, 4));
    }
    
    @Test
    public void testLinkedResources() {
        assertTrue(defs.getLinked(studentDef).contains(studentEnrollmentDef));
    }
    
    @Test
    public void testAssociations() {
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("name.firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        EntityBody student2 = new EntityBody();
        student2.put("firstName", "Petra");
        student2.put("lastName", "Arkanian");
        EntityBody student3 = new EntityBody();
        student3.put("firstName", "Andrew");
        student3.put("lastName", "Wiggen");
        EntityBody student4 = new EntityBody();
        student4.put("firstName", "Julian");
        student4.put("lastName", "Delphiki");
        String id1 = studentService.create(student1);
        String id2 = studentService.create(student2);
        String id3 = studentService.create(student3);
        String id4 = studentService.create(student4);
        EntityBody school = new EntityBody();
        school.put("name", "Battle School");
        school.put("nameOfInstitution", "Battle School");
        String schoolId = schoolService.create(school);
        EntityBody assoc1 = new EntityBody();
        assoc1.put("schoolId", schoolId);
        assoc1.put("studentId", id1);
        assoc1.put("startDate", (new Date()).getTime());
        assoc1.put("entryGradeLevel", "First grade");
        String assocId1 = studentSchoolAssociationService.create(assoc1);
        EntityBody retrievedAssoc1 = studentSchoolAssociationService.get(assocId1);
        assertEquals(retrievedAssoc1.get("schoolId"), assoc1.get("schoolId"));
        assertEquals(retrievedAssoc1.get("studentId"), assoc1.get("studentId"));
        assertEquals(retrievedAssoc1.get("startDate"), assoc1.get("startDate"));
        EntityBody assoc2 = new EntityBody();
        assoc2.put("schoolId", schoolId);
        assoc2.put("studentId", id2);
        assoc2.put("startDate", (new Date()).getTime());
        assoc2.put("entryGradeLevel", "Second grade");
        String assocId2 = studentSchoolAssociationService.create(assoc2);
        EntityBody retrievedAssoc2 = studentSchoolAssociationService.get(assocId2);
        assertEquals(retrievedAssoc2.get("schoolId"), assoc2.get("schoolId"));
        assertEquals(retrievedAssoc2.get("studentId"), assoc2.get("studentId"));
        assertEquals(retrievedAssoc2.get("startDate"), assoc2.get("startDate"));
        EntityBody assoc3 = new EntityBody();
        assoc3.put("schoolId", schoolId);
        assoc3.put("studentId", id3);
        assoc3.put("startDate", (new Date()).getTime());
        assoc3.put("entryGradeLevel", "Third grade");
        String assocId3 = studentSchoolAssociationService.create(assoc3);
        EntityBody retrievedAssoc3 = studentSchoolAssociationService.get(assocId3);
        assertEquals(retrievedAssoc3.get("schoolId"), assoc3.get("schoolId"));
        assertEquals(retrievedAssoc3.get("studentId"), assoc3.get("studentId"));
        assertEquals(retrievedAssoc3.get("startDate"), assoc3.get("startDate"));
        EntityBody assoc4 = new EntityBody();
        assoc4.put("schoolId", schoolId);
        assoc4.put("studentId", id4);
        assoc4.put("startDate", (new Date()).getTime());
        assoc4.put("entryGradeLevel", "Fourth grade");
        String assocId4 = studentSchoolAssociationService.create(assoc4);
        EntityBody retrievedAssoc4 = studentSchoolAssociationService.get(assocId4);
        assertEquals(retrievedAssoc4.get("schoolId"), assoc4.get("schoolId"));
        assertEquals(retrievedAssoc4.get("studentId"), assoc4.get("studentId"));
        assertEquals(retrievedAssoc4.get("startDate"), assoc4.get("startDate"));
        assertEquals(Arrays.asList(retrievedAssoc1, retrievedAssoc2, retrievedAssoc3, retrievedAssoc4),
                studentSchoolAssociationService.get(Arrays.asList(assocId1, assocId2, assocId3, assocId4)));
        assertEquals(Arrays.asList(assocId1), studentSchoolAssociationService.getAssociationsWith(id1, 0, 4, null));
        assertEquals(Arrays.asList(assocId2), studentSchoolAssociationService.getAssociationsWith(id2, 0, 4, null));
        assertEquals(Arrays.asList(assocId3), studentSchoolAssociationService.getAssociationsWith(id3, 0, 4, null));
        assertEquals(Arrays.asList(assocId4), studentSchoolAssociationService.getAssociationsWith(id4, 0, 4, null));
        assertEquals(Arrays.asList(assocId1, assocId2, assocId3, assocId4),
                studentSchoolAssociationService.getAssociationsTo(schoolId, 0, 4, null));
        
        // test query fields
        assertEquals(Arrays.asList(assocId1),
                studentSchoolAssociationService.getAssociationsWith(id1, 0, 4, "entryGradeLevel=First grade"));
        assertFalse(studentSchoolAssociationService.getAssociationsWith(id1, 0, 4, "entryGradeLevel=Second grade")
                .iterator().hasNext());
        
        assertEquals(Arrays.asList(assocId1),
                studentSchoolAssociationService.getAssociationsTo(schoolId, 0, 4, "entryGradeLevel=First grade"));
        assertFalse(studentSchoolAssociationService.getAssociationsTo(schoolId, 0, 4, "entryGradeLevel=Fifth grade")
                .iterator().hasNext());
        
        assertEquals(Arrays.asList(schoolId),
                studentSchoolAssociationService.getAssociatedEntitiesWith(id1, 0, 4, "nameOfInstitution=Battle School"));
        assertFalse(studentSchoolAssociationService
                .getAssociatedEntitiesWith(id1, 0, 4, "nameOfInstitution=new Battle School").iterator().hasNext());
        
        assertEquals(Arrays.asList(id1),
                studentSchoolAssociationService.getAssociatedEntitiesTo(schoolId, 0, 4, "name.firstName=Bonzo"));
        assertFalse(studentSchoolAssociationService.getAssociatedEntitiesTo(schoolId, 0, 4, "name.firstName=non exist")
                .iterator().hasNext());
        
        studentService.delete(id1);
        studentService.delete(id2);
        studentService.delete(id3);
        studentService.delete(id4);
        schoolService.delete(schoolId);
    }
    
    // test referential validation for association creation
    @Test(expected = EntityValidationException.class)
    public void testCreateAssocValidate() {
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        String id1 = studentService.create(student1);
        
        EntityBody school = new EntityBody();
        school.put("name", "Battle School");
        schoolService.create(school);
        
        EntityBody assoc1 = new EntityBody();
        // assoc1.put("schoolId", schoolId);
        assoc1.put("studentId", id1);
        assoc1.put("startDate", (new Date()).getTime());
        studentSchoolAssociationService.create(assoc1);
    }
    
    // test delete source entity also remove association entity
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteWithAssoc1() {
        Map<String, String> ids = setupTestDeleteWithAssoc();
        String studentId = ids.get("studentId");
        String assocId = ids.get("assocId");
        
        EntityBody assocEntity = studentSchoolAssociationService.get(assocId);
        assertNotNull(assocEntity);
        assertEquals(assocEntity.get("studentId"), studentId);
        
        studentService.delete(studentId);
        studentSchoolAssociationService.get(assocId);
    }
    
    // test delete target entity also remove association entity
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteWithAssoc2() {
        Map<String, String> ids = setupTestDeleteWithAssoc();
        String schoolId = ids.get("schoolId");
        String assocId = ids.get("assocId");
        String assoc2Id = ids.get("assoc2Id");
        
        EntityBody assocEntity = studentSchoolAssociationService.get(assocId);
        assertNotNull(assocEntity);
        assertEquals(assocEntity.get("schoolId"), schoolId);
        
        schoolService.delete(schoolId);
        // studentSchoolAssociationService.get(assocId);
        studentSchoolAssociationService.get(assoc2Id);
    }
    
    private <T> List<T> iterableToList(Iterable<T> itr) {
        List<T> result = new ArrayList<T>();
        for (T item : itr) {
            result.add(item);
        }
        return result;
    }
    
    private Map<String, String> setupTestDeleteWithAssoc() {
        Map<String, String> ids = new HashMap<String, String>();
        EntityBody student1 = new EntityBody();
        student1.put("firstName", "Bonzo");
        student1.put("lastName", "Madrid");
        String studentId = studentService.create(student1);
        
        EntityBody student2 = new EntityBody();
        student1.put("firstName", "Jane");
        student1.put("lastName", "Doe");
        String student2Id = studentService.create(student2);
        
        EntityBody school = new EntityBody();
        school.put("name", "Battle School");
        String schoolId = schoolService.create(school);
        
        EntityBody assoc = new EntityBody();
        assoc.put("schoolId", schoolId);
        assoc.put("studentId", studentId);
        assoc.put("startDate", (new Date()).getTime());
        String assocId = studentSchoolAssociationService.create(assoc);
        
        EntityBody assoc2 = new EntityBody();
        assoc2.put("schoolId", schoolId);
        assoc2.put("studentId", student2Id);
        assoc2.put("startDate", (new Date()).getTime());
        String assoc2Id = studentSchoolAssociationService.create(assoc2);
        
        ids.put("studentId", studentId);
        ids.put("schoolId", schoolId);
        ids.put("assocId", assocId);
        
        ids.put("student2Id", student2Id);
        ids.put("assoc2Id", assoc2Id);
        
        return ids;
    }
}