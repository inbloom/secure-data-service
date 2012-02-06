package org.slc.sli.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * PopulationManager facilitates creation of logical aggregations of EdFi entities/associations such as a
 * student summary comprised of student profile, enrollment, program, and assessment information in order to 
 * deliver the Population Summary interaction.
 *       
 * @author Robert Bloh rbloh@wgen.net
 * 
 */
public class PopulationManager {
    
    private static Logger log = LoggerFactory.getLogger(PopulationManager.class);
    
    @Autowired
    public EntityManager entityManager;
    
    private Map<String, SimpleEntity> assessmentFamilyMap;
    
    public PopulationManager() { 
        assessmentFamilyMap = new HashMap<String, SimpleEntity>();
    }
    
    /**
     * Initialize with population-independent metadata.
     * 
     */
    public void init() {
        
        List<SimpleEntity> assessmentMetaDataList = entityManager.getAssessmentMetadata();
        for (SimpleEntity assessmentFamily : assessmentMetaDataList) {
            List<Map> assessments = (List<Map>) assessmentFamily.get("children");
            
            for (Map assessment : assessments) {
                this.getAssessmentFamilyMap().put((String) assessment.get("name"), assessmentFamily);
            }
        }
        
    }
    
    /**
     * Get the assessment family lookup map
     * 
     * @return assessmentFamilyMap
     *         - the assessment family map
     */
    public Map<String, SimpleEntity> getAssessmentFamilyMap() {
        return this.assessmentFamilyMap;
    }
    
    /**
     * Get the list of student summaries identified by the student id list and authorized for the
     * security token
     * 
     * @param token
     *            - the principle authentication token
     * @param studentIds
     *            - the student id list
     * @return studentList
     *         - the student summary entity list
     */
    public List<SimpleEntity> getStudentSummaries(String token, List<String> studentIds) {
        
        List<SimpleEntity> studentPrograms = entityManager.getPrograms(token, studentIds);
        List<SimpleEntity> studentAssessments = entityManager.getAssessments(token, studentIds);
        List<SimpleEntity> studentSummaries = entityManager.getStudents(token, studentIds);
        
        // Initialize student programs
        Map studentProgramMap = new HashMap<String, Object>();
        for (SimpleEntity studentProgram : studentPrograms) {
            List<String> programs = (List<String>) studentProgram.get("programs");            
            studentProgramMap.put(studentProgram.get("studentId"), programs);
        }

        // Initialize student assessments
        Map studentAssessmentMap = new HashMap<String, Object>();
        for (SimpleEntity studentAssessment : studentAssessments) {
            String assessmentName = (String) studentAssessment.get("assessmentName");
            studentAssessment.put("assessmentFamily", this.getAssessmentFamilyMap().get(assessmentName));
            studentAssessmentMap.put(studentAssessment.get("studentId"), studentAssessment);
        }

        // Initialize student summaries
        for (SimpleEntity studentSummary : studentSummaries) {
            String id = (String) studentSummary.get("id");
            studentSummary.put("programs", studentProgramMap.get(id));
            studentSummary.put("assessments", studentAssessmentMap.get(id));
        }
        
        return studentSummaries;
    }
    
    public static void main(String[] arguments) {
        
        log.info("Starting PopulationManager...");
        
        try {
            String token = "rbraverman";
            
            EntityManager entityManager = new EntityManager();
            
            PopulationManager populationManager = new PopulationManager();
            populationManager.setEntityManager(entityManager);
            populationManager.init();
            
            log.info("Building Student Summaries...");

            List<SimpleEntity> studentSummaries = populationManager.getStudentSummaries(token, null);
            
            log.info("Building Student Summaries Again...");

            studentSummaries = populationManager.getStudentSummaries(token, null);
            
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        
        log.info("Finished PopulationManager.");
        
    }
    
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
}
