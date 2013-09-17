package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentToStudentParentAssociationValidatorTest {
    
    @Autowired
    private StudentToStudentParentAssociationValidator validator;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Autowired
    private ValidatorTestHelper helper;
      
    private List<Entity> studentParentAssociationList = null;
    
    @Before
    public void setUp() {
        studentParentAssociationList = Arrays.asList(helper.generateStudentParentAssoc("studentID1", "parentID1"),
                helper.generateStudentParentAssoc("studentID1", "parentID2"));

        Entity student = Mockito.mock(Entity.class);
        Map<String, List<Entity>> studentParentAssociationMap = new HashMap<String, List<Entity>>();
        String key = "studentParentAssociation";
        studentParentAssociationMap.put(key, studentParentAssociationList);
        Mockito.when(student.getEmbeddedData()).thenReturn(studentParentAssociationMap);
        injector.setStudentContext(student);
    }
    
    /*
     * Send the two valid studentParentAssociations for the student to the validator.
     * The validator should return true.
     */
    @Test
    public void testValidStudentParentAssociation() {
        Set<String> ids = new HashSet<String>();
        for (Entity e : studentParentAssociationList) {
            String s = e.getEntityId();
            ids.add(s);
        }
        boolean valid = validator.validate(EntityNames.STUDENT_PARENT_ASSOCIATION, ids).containsAll(ids);
        Assert.assertTrue(valid);
    }
    
    /*
     * Add an invalid studentParentAssociation to the two valid ones that the student contains.
     * The validator should return false.
     */
    @Test
    public void testOneInvalidStudentParentAssociation() {
        Set<String> ids = new HashSet<String>();  
        for (Entity e : studentParentAssociationList) {
            String s = e.getEntityId();
            ids.add(s);
        }
        // add the invalid studentParentAssocation - the validator should return false
        ids.add("invalidID");
        boolean valid = validator.validate(EntityNames.STUDENT_PARENT_ASSOCIATION, ids).containsAll(ids);
        Assert.assertTrue(!valid);
    }
    
    /*
     * Empty list of ids - should return false from the areParametersValid method in validator
     */
    @Test
    public void testEmptyStudentParentAssociation() {
        Set<String> ids = new HashSet<String>();  
        ids.add("invalidID");
        boolean valid = validator.validate(EntityNames.STUDENT_PARENT_ASSOCIATION, ids).containsAll(ids);
        Assert.assertTrue(!valid);
    }
    
}
