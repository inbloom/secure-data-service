/**
 * 
 */
package org.slc.sli.api.security.context.resolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeConnection;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
    private SecurityContextInjector injector;
    
    @Autowired
    private PathFindingContextResolver resolver;
    
    private AssociativeContextHelper mockHelper;
    
    private Repository<Entity> mockRepo;

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockHelper = Mockito.mock(AssociativeContextHelper.class);
        mockRepo = Mockito.mock(Repository.class);
        resolver.setHelper(mockHelper);
        resolver.setRepository(mockRepo);
        List<String> tsKeys = Arrays.asList(new String[] { "teacherId", "sectionId" });
        List<String> ssKeys = Arrays.asList(new String[] { "sectionId", "studentId" });
        when(mockHelper.getAssocKeys(eq(EntityNames.TEACHER), any(AssociationDefinition.class))).thenReturn(tsKeys);
        when(mockHelper.getAssocKeys(eq(EntityNames.SECTION), any(AssociationDefinition.class))).thenReturn(ssKeys);
    }

    @Test
    public void testCanResolve() throws Exception {
        assertFalse(resolver.canResolve(EntityNames.TEACHER, EntityNames.STUDENT));
        assertTrue(resolver.canResolve(EntityNames.STUDENT, EntityNames.TEACHER));
        assertFalse(resolver.canResolve(EntityNames.AGGREGATION, EntityNames.TEACHER));
    }
    

    @SuppressWarnings("unchecked")
    @Test
    public void testFindTeacherToSections() throws Exception {
        injector.setDemoContext();
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn("1");

        //override for demo user
        Map<String, Object> mockBody = new HashMap<String, Object>();
        mockBody.put("staffUniqueStateId", "mock");
        when(mockEntity.getBody()).thenReturn(mockBody);

        List<String> finalList = Arrays.asList(new String[] { "2", "3", "4" });
        assertTrue("Can resolve teacher to section", resolver.canResolve(EntityNames.TEACHER, EntityNames.SECTION));
        when(
                mockHelper.findEntitiesContainingReference(eq(EntityNames.TEACHER_SECTION_ASSOCIATION),
                        eq("teacherId"), eq("sectionId"), any(List.class), any(List.class))).thenReturn(
                Arrays.asList(new String[] { "5", "6", "7" }));
        when(
                mockHelper.findEntitiesContainingReference(eq(EntityNames.STUDENT_SECTION_ASSOCIATION),
                        eq("sectionId"), eq("studentId"), any(List.class), any(List.class))).thenReturn(
                Arrays.asList(new String[] { "8", "9", "10" }));
        when(mockHelper.getAssocKeys(eq(EntityNames.STUDENT), any(AssociationDefinition.class))).thenReturn(
                Arrays.asList(new String[] { "studentId", "sectionId" }));
        when(
                mockHelper.findEntitiesContainingReference(eq(EntityNames.STUDENT_SECTION_ASSOCIATION),
                        eq("studentId"), eq("sectionId"), any(List.class), any(List.class))).thenReturn(finalList);
        
        List<String> returned = resolver.findAccessible(mockEntity);
        // assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue("List contains " + id, returned.contains(id));
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindTeacherToTeacher() throws Exception {
        injector.setDemoContext();
        Entity mockEntity = Mockito.mock(Entity.class);

        //override for demo user
        Map<String, Object> mockBody = new HashMap<String, Object>();
        mockBody.put("staffUniqueStateId", "mock");
        when(mockEntity.getBody()).thenReturn(mockBody);

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
        // assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue(returned.contains(id));
        }
    }
    
    @Test
    public void testGetResourceName() throws Exception {
        String currentNodeType = "currentNodeType";
        String nextNodeType = "nextNodeType";
        String connectionNodeType = "connectionNodeType";
        
        SecurityNode mockCurrentNode = Mockito.mock(SecurityNode.class);
        SecurityNode mockNextNode = Mockito.mock(SecurityNode.class);
        SecurityNodeConnection mockConnection = Mockito.mock(SecurityNodeConnection.class);

        when(mockCurrentNode.getType()).thenReturn(currentNodeType);
        when(mockNextNode.getType()).thenReturn(nextNodeType);
        when(mockConnection.getAssociationNode()).thenReturn(connectionNodeType);
        assert (this.resolver.getResourceName(mockCurrentNode, mockNextNode, mockConnection).equals(connectionNodeType));
        when(mockConnection.getAssociationNode()).thenReturn("");
        when(mockConnection.isReferenceInSelf()).thenReturn(true);
        assert (this.resolver.getResourceName(mockCurrentNode, mockNextNode, mockConnection).equals(currentNodeType));
        when(mockConnection.isReferenceInSelf()).thenReturn(false);
        assert (this.resolver.getResourceName(mockCurrentNode, mockNextNode, mockConnection).equals(nextNodeType));
    }
}
