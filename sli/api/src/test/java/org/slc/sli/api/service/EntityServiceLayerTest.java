package org.slc.sli.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityServiceLayerTest {
    
    @Autowired
    private EntityDefinitionStore defs;
    private EntityService studentService;
    
    @Before
    public void setUp() {
        defs.init();
        EntityDefinition studentDef = defs.lookupByResourceName("students");
        studentService = studentDef.getService();
    }
    
    @Test
    public void testCrudEntity() {
        EntityBody student = new EntityBody();
        student.put("firstName", "Andrew");
        student.put("lastName", "Wiggen");
        String id = studentService.create(student);
        assertEquals(student, studentService.get(id));
        student = new EntityBody(student);
        student.put("sex", "Male");
        assertTrue(studentService.update(id, student));
        assertEquals(student, studentService.get(id));
        assertFalse(studentService.update(id, student));
        assertEquals(student, studentService.get(id));
        assertTrue(studentService.delete(id));
        try {
            EntityBody zombie = studentService.get(id);
            fail("should have not found " + zombie);
        } catch (EntityNotFoundException e) {
        }
        assertFalse(studentService.delete(id));
    }
    
    @Test
    public void testNoSuchEntity() {
        try {
            studentService.get("NoSuchStudent");
            fail("should have thrown exception");
        } catch (EntityNotFoundException e) {
        }
        try {
            studentService.update("NoSuchStudent", new EntityBody());
            fail("should have thrown exception");
        } catch (EntityNotFoundException e) {
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
        assertEquals(student1, studentService.get(id1));
        assertEquals(student2, studentService.get(id2));
        assertEquals(student3, studentService.get(id3));
        assertEquals(student4, studentService.get(id4));
        assertEquals(Arrays.asList(student1, student2, student3, student4),
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
    
    private <T> List<T> iterableToList(Iterable<T> itr) {
        List<T> result = new ArrayList<T>();
        for (T item : itr) {
            result.add(item);
        }
        return result;
    }
    
}
