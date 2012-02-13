package org.slc.sli.unit.manager;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.EntityManager;
import org.slc.sli.manager.PopulationManager;

/**
 * 
 * Tests for the population manager.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EntityManager.class)
public class PopulationManagerTest {
    
    
    /*
     * Test that the assessmentFamilyMap is populated from values in entityManager.
     */
    @Test
    public void testAssessmentFamilyMapFromInit() throws Exception {
        
        EntityManager mockedEntityManager = PowerMockito.spy(new EntityManager());
        
        HashMap<String, String> assessment = new HashMap<String, String>();
        assessment.put("name", "Dibels");
        LinkedList<Map> assessments = new LinkedList<Map>();
        assessments.add(assessment);
        HashMap<String, Object> genericEntityMap = new HashMap<String, Object>();
        genericEntityMap.put("children", assessments);
        GenericEntity genericEntity = new GenericEntity(genericEntityMap);
        LinkedList<GenericEntity> assessmentMetaDataList = new LinkedList<GenericEntity>();
        assessmentMetaDataList.add(genericEntity);
        
        PowerMockito.doReturn(assessmentMetaDataList).when(mockedEntityManager, "getAssessmentMetadata");
        PopulationManager popManager = new PopulationManager();
        assertTrue(popManager.getAssessmentFamilyMap().size() == 0);
        popManager.setEntityManager(mockedEntityManager);
        popManager.init();
        assertTrue(popManager.getAssessmentFamilyMap().containsKey("Dibels"));
    }
    
    
    
    /*
     *Test that student summaries are being populated from programs, assessments, and students 
     */
    @Test
    public void testGetStudentSummaries() throws Exception {
        String studentId = "student_id";
        String token = "token";
        String assessmentName = "Dibels";
        String assessmentFamily = "Reading";
        List<String> studentIds = new LinkedList<String>(); 
        studentIds.add(studentId);
        List<String> programs = new LinkedList<String>();
        programs.add("ELL");
       
        EntityManager mockedEntityManager = PowerMockito.spy(new EntityManager());
       
        // Setup studentPrograms
        GenericEntity studentProgram = new GenericEntity();
        studentProgram.put("studentId", studentId);
        studentProgram.put("programs", programs);
        List<GenericEntity> studentPrograms = new LinkedList<GenericEntity>();
        studentPrograms.add(studentProgram);
       

        PowerMockito.doReturn(studentPrograms).when(mockedEntityManager, "getPrograms", token, studentIds);
       
        // Setup studentAssessments
        GenericEntity studentAssessment = new GenericEntity();
        studentAssessment.put("assessmentName", assessmentName);
        studentAssessment.put("studentId", studentId);
        List<GenericEntity> studentAssessments = new LinkedList<GenericEntity>();
        studentAssessments.add(studentAssessment);
       
        PowerMockito.doReturn(studentAssessments).when(mockedEntityManager, "getAssessments", token, studentIds);
        
        
        //Setup studentSummaries
        GenericEntity studentSummary = new GenericEntity();
        studentSummary.put("id", studentId);
        List<GenericEntity> studentSummaries = new LinkedList<GenericEntity>();
        studentSummaries.add(studentSummary);
        
        PowerMockito.doReturn(studentSummaries).when(mockedEntityManager, "getStudents", token, studentIds);
        
        
        PopulationManager popMan = new PopulationManager();
        popMan.setEntityManager(mockedEntityManager);
        
        List<GenericEntity> studentSumamries = popMan.getStudentSummaries(token, studentIds); 
        assertTrue(studentSummaries.size() == 1);
        
        GenericEntity result = studentSummaries.get(0);
        assertTrue(result.get("id").equals(studentId));
        assertTrue(programs.equals(result.get("programs")));
        
        GenericEntity assessments = (GenericEntity) result.get("assessments");
        assertTrue(assessments.get("assessmentName").equals(assessmentName));
        assertTrue(assessments.get("assessmentFamily") == null);
    }
}
