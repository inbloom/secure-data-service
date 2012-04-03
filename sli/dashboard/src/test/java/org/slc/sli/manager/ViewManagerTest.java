package org.slc.sli.manager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.view.modifier.ViewModifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/application-context-test.xml" })
public class ViewManagerTest {
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ViewManager viewManager = new ViewManager(); // class under test
    
    @Before
    public void setup() {
    }
    
    @Test
    public void testGetAndSetViewConfig() {
        List<ViewConfig> testConfigs = new ArrayList<ViewConfig>();
        ViewConfig config1 = new ViewConfig();
        ViewConfig config2 = new ViewConfig();

        testConfigs.add(config1);
        testConfigs.add(config2);

        viewManager.setViewConfigs(testConfigs);
        assertEquals(testConfigs, viewManager.getViewConfigs());
    }
    
    @Test 
    public void testSetActiveViewConfig() {
        ViewConfig config1 = new ViewConfig();
            
        viewManager.setActiveViewConfig(config1);
        assertEquals(config1, viewManager.getActiveConfig());
    }
    
    @Test 
    public void testApply() {
        ViewModifier modifier = mock(ViewModifier.class);

        viewManager.apply(modifier);
        verify(modifier).modify(any(ViewConfig.class));
    }
    
    @Test
    public void testGetApplicableViewConfigs() {
        List<ViewConfig> viewConfigs = new ArrayList<ViewConfig>();
        ViewConfig view3to8 = new ViewConfig();
        view3to8.setValue("3-8");
        ViewConfig view9to12 = new ViewConfig();
        view9to12.setValue("9-12");
        ViewConfig viewKto3 = new ViewConfig();
        viewKto3.setValue("0-3");

        viewConfigs.add(viewKto3);
        viewConfigs.add(view3to8);
        viewConfigs.add(view9to12);

        viewManager.setViewConfigs(viewConfigs);

        List<String> studentsKto3 = new ArrayList<String>();
        studentsKto3.add("studentsKto3");

        List<String> students3to8 = new ArrayList<String>();
        students3to8.add("students3to8");

        List<String> students9to12 = new ArrayList<String>();
        students9to12.add("students9to12");

        List<String> emptyList = new ArrayList<String>();

        when(entityManager.getStudents("", emptyList)).thenReturn(null);
        when(entityManager.getStudents("", studentsKto3)).thenReturn(createStudentsKto3());
        when(entityManager.getStudents("", students3to8)).thenReturn(createStudents3to8());
        when(entityManager.getStudents("", students9to12)).thenReturn(createStudents9to12());
        
        assertEquals("Should return empty list when student list is empty", 0,
                viewManager.getApplicableViewConfigs(emptyList, "").size());

        assertTrue("Should contain k to 3 view", viewManager.getApplicableViewConfigs(studentsKto3, "").contains(viewKto3));
        assertTrue("Should contain 3 to 8 view", viewManager.getApplicableViewConfigs(studentsKto3, "").contains(view3to8));
        assertEquals(2, viewManager.getApplicableViewConfigs(studentsKto3, "").size());

        assertTrue("Should contain 3 to 8 view", viewManager.getApplicableViewConfigs(students3to8, "").contains(view3to8));
        assertTrue("Should contain K to 3 view", viewManager.getApplicableViewConfigs(students3to8, "").contains(viewKto3));
        assertEquals(2, viewManager.getApplicableViewConfigs(students3to8, "").size());

        assertTrue("Should contain 9 to 12 view", viewManager.getApplicableViewConfigs(students9to12, "").contains(view9to12));
        assertEquals(1, viewManager.getApplicableViewConfigs(students9to12, "").size());

    }

    private List<GenericEntity> createStudents9to12() {
        List<GenericEntity> students = new ArrayList<GenericEntity>();
        students.add(createStudent("Ninth grade"));
        students.add(createStudent("Tenth grade"));
        students.add(createStudent("Eleventh grade"));
        students.add(createStudent("Twelfth grade"));
        return students;
    }

    private List<GenericEntity> createStudents3to8() {
        List<GenericEntity> students = new ArrayList<GenericEntity>();
        students.add(createStudent("Third grade"));
        students.add(createStudent("Fifth grade"));
        students.add(createStudent("Seventh grade"));
        students.add(createStudent("Eighth grade"));
        return students;
    }

    private List<GenericEntity> createStudentsKto3() {
        List<GenericEntity> students = new ArrayList<GenericEntity>();
        students.add(createStudent("Kindergarten"));
        students.add(createStudent("First grade"));
        students.add(createStudent("Second grade"));
        students.add(createStudent("Third grade"));
        return students;
    }

    private GenericEntity createStudent(String gradeLevel) {
        GenericEntity student = new GenericEntity();
        student.put(Constants.ATTR_GRADE_LEVEL, gradeLevel);
        return student;
    }

}
