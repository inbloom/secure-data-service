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
      
    private List<Entity> spas = null;
    
    @Before
    public void setUp() {
        spas = Arrays.asList(helper.generateStudentParentAssoc("studentID1", "parentID1"),
                helper.generateStudentParentAssoc("studentID2", "parentID2"));

        Entity student = Mockito.mock(Entity.class);
        Map<String, List<Entity>> value = new HashMap<String, List<Entity>>();
        String key = "studentParentAssociation";
        value.put(key, spas);
        Mockito.when(student.getEmbeddedData()).thenReturn(value);
        injector.setStudentContext(student);
    }
    
    @Test
    public void testValidSingleAssociation() {
        //String entityType, Set<String> ids
        Set<String> ids = new HashSet<String>();
        for (Entity e : spas) {
            String s = e.getEntityId();
            ids.add(s);
        }
        boolean valid = validator.validate(EntityNames.STUDENT_PARENT_ASSOCIATION, ids);
        Assert.assertTrue(valid);
    }
    
}
