package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.config.ConfigUtil;
import org.slc.sli.config.Field;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.SecurityUtil;


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
    private EntityManager entityManager;
    
    private Map<String, GenericEntity> assessmentFamilyMap;
    
    public PopulationManager() { 
        assessmentFamilyMap = new HashMap<String, GenericEntity>();
    }
    
    /**
     * Initialize with population-independent metadata.
     * 
     */
    public void init() {
        
        List<GenericEntity> assessmentMetaDataList = entityManager.getAssessmentMetadata();
        for (GenericEntity assessmentFamily : assessmentMetaDataList) {
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
    public Map<String, GenericEntity> getAssessmentFamilyMap() {
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
    public List<GenericEntity> getStudentSummaries(String token, List<String> studentIds) {
        
        List<GenericEntity> studentPrograms = entityManager.getPrograms(token, studentIds);
        List<GenericEntity> studentAssessments = entityManager.getAssessments(token, studentIds);
        List<GenericEntity> studentSummaries = entityManager.getStudents(token, studentIds);
        
        // Initialize student programs
        Map studentProgramMap = new HashMap<String, Object>();
        for (GenericEntity studentProgram : studentPrograms) {
            List<String> programs = (List<String>) studentProgram.get("programs");            
            studentProgramMap.put(studentProgram.get("studentId"), programs);
        }

        // Initialize student assessments
        Map studentAssessmentMap = new HashMap<String, Object>();
        for (GenericEntity studentAssessment : studentAssessments) {
            String assessmentName = (String) studentAssessment.get("assessmentName");
            studentAssessment.put("assessmentFamily", this.getAssessmentFamilyMap().get(assessmentName));
            studentAssessmentMap.put(studentAssessment.get("studentId"), studentAssessment);
        }

        // Initialize student summaries
        for (GenericEntity studentSummary : studentSummaries) {
            String id = (String) studentSummary.get("id");
            studentSummary.put("programs", studentProgramMap.get(id));
            studentSummary.put("assessments", studentAssessmentMap.get(id));
        }
        
        return studentSummaries;
    }
    
    /**
     * 
     * @param username
     * @param studentIds
     * @param config
     * @return
     */
    public List<GenericEntity> getStudentInfo(String username, List<String> studentIds, ViewConfig config) {
        
        // extract the studentInfo data fields
        List<Field> dataFields = ConfigUtil.getDataFields(config, "studentInfo");
        
        // call the entity manager
        List<GenericEntity> studentInfo = new ArrayList<GenericEntity>();
        if (dataFields.size() > 0) {
            studentInfo.addAll(entityManager.getStudents(SecurityUtil.getToken(), studentIds));
        }
        
        // return the results
        return studentInfo;
    }
    
    
    /**
     * Returns the student program association data for the giving list of students
     */    
    public List<GenericEntity> getStudentProgramAssociations(String username, List<String> studentIds) {
        List<GenericEntity> programs = new ArrayList<GenericEntity>();
        programs.addAll(entityManager.getPrograms(SecurityUtil.getToken(), studentIds));
        return programs;
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

            List<GenericEntity> studentSummaries = populationManager.getStudentSummaries(token, null);
            
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
