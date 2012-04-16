/**
 * 
 */
package org.slc.sli.api.security.context.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * @author rlatta
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class PathFindingContextResolverTest {
    
    @Autowired
    private PathFindingContextResolver resolver;
    
    private AssociativeContextHelper mockHelper;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockHelper = Mockito.mock(AssociativeContextHelper.class);
        resolver.setHelper(mockHelper);
        List<String> tsKeys = Arrays.asList(new String[] { "teacherId", "sectionId" });
        List<String> ssKeys = Arrays.asList(new String[] { "sectionId", "studentId" });
        when(mockHelper.getAssocKeys(eq(EntityNames.TEACHER), any(AssociationDefinition.class))).thenReturn(tsKeys);
        when(mockHelper.getAssocKeys(eq(EntityNames.SECTION), any(AssociationDefinition.class))).thenReturn(ssKeys);

    }

    @Test
    public void testCanResolve() throws Exception {
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.STUDENT));
        assertTrue(resolver.canResolve(EntityNames.STUDENT, EntityNames.TEACHER));
        assertFalse(resolver.canResolve(EntityNames.AGGREGATION, EntityNames.TEACHER));
    }
    
    @Test
    public void testFindTeacherToSections() throws Exception {
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn("1");
        List<String> finalList = Arrays.asList(new String[] { "2", "3", "4" });
        List<String> keys = Arrays.asList(new String[] { "teacherId", "sectionId" });
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.SECTION));
        when(
                mockHelper.findEntitiesContainingReference(eq(EntityNames.TEACHER_SECTION_ASSOCIATION),
                        eq("teacherId"),
                        eq("sectionId"), any(List.class))).thenReturn(finalList);
        List<String> returned = resolver.findAccessible(mockEntity);
        assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue(returned.contains(id));
        }

    }
    
    @Test
    public void testFindTeacherToStudent() throws Exception {
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn("1");
        List<String> finalList = Arrays.asList(new String[] { "5", "6", "7" });
        
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.STUDENT));
        when(
                mockHelper.findEntitiesContainingReference(eq(EntityNames.TEACHER_SECTION_ASSOCIATION),
                        eq("teacherId"), eq("sectionId"), any(List.class))).thenReturn(
                Arrays.asList(new String[] { "2", "3", "4" }));
        
        when(
                mockHelper.findEntitiesContainingReference(eq(EntityNames.STUDENT_SECTION_ASSOCIATION),
                        eq("sectionId"), eq("studentId"), any(List.class))).thenReturn(finalList);
        List<String> returned = resolver.findAccessible(mockEntity);
        assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue(returned.contains(id));
        }
        
    }
    
    @Test
    public void testFindTeacherToTeacher() throws Exception {
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn("1");
        List<String> finalList = Arrays.asList(new String[] { "1" });
        List<String> tsKeys1 = Arrays.asList(new String[] { "schoolId", "teacherId" });
        List<String> tsKeys2 = Arrays.asList(new String[] { "teacherId", "schoolId" });

        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.TEACHER));
        when(mockHelper.getAssocKeys(eq(EntityNames.SCHOOL), any(AssociationDefinition.class))).thenReturn(tsKeys1);
        when(mockHelper.getAssocKeys(eq(EntityNames.TEACHER), any(AssociationDefinition.class))).thenReturn(tsKeys2);
        when(
                mockHelper.findEntitiesContainingReference(eq("teacherSchoolAssociation"),
                        eq("teacherId"), eq("schoolId"), any(List.class))).thenReturn(
                Arrays.asList(new String[] { "2", "3", "4" }));
        
        when(
                mockHelper.findEntitiesContainingReference(eq("teacherSchoolAssociation"),
                        eq("schoolId"), eq("teacherId"), any(List.class))).thenReturn(finalList);
        List<String> returned = resolver.findAccessible(mockEntity);
        assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue(returned.contains(id));
        }
    }

}
