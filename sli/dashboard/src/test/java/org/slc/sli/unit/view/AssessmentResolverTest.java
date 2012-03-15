package org.slc.sli.unit.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.view.AssessmentResolver;

import com.google.gson.Gson;

/**
 * Unit tests for the AssessmentResolver class.
 * 
 * @author Dliu
 * 
 */
public class AssessmentResolverTest {

    // test subject
    private AssessmentResolver resolver;
    
    // only one student data for testing.
    private GenericEntity student;
    
    // studentAssessment
    private GenericEntity studentAssmt;
    
    // studentAssessment with perf level
    private GenericEntity studentAssmtWithPerfLevel;
    
    // studentAssessment with student objective assessment
    private GenericEntity studentAssmtWithObjAssmt;

    @Before
    public void setup() {
        // resolver = new AssessmentResolver(getAssessments(), getAssessmentMetaData());
        resolver = new AssessmentResolver(null, createAssessments());
        student = createStudent();
        studentAssmt = createStudentAssessment();
        studentAssmtWithPerfLevel = createStudentAssessmentWithPerfLevel();
        studentAssmtWithObjAssmt = createStudentAssessmentWithObjAssmt();
    }

    @Test
    public void testGet() throws Exception {
        Field f = new Field();
        // get the ISAT Reading performance level
        f.setValue("ISAT Reading." + Constants.ATTR_PERF_LEVEL);
        f.setTimeSlot("MOST_RECENT_RESULT");
        String perfLevel = resolver.get(f, student);
        assertEquals("ISAT Reading Perf Level should be 3", "3", perfLevel);
        // get the ISAT Reading scale score
        f.setValue("ISAT Reading." + Constants.ATTR_SCALE_SCORE);
        f.setTimeSlot("MOST_RECENT_RESULT");
        String scaleScore = resolver.get(f, student);
        assertEquals("scale score should be 250", "250", scaleScore);
        
        // get the most recent SAT Reading scale score
        f.setValue("SAT." + Constants.ATTR_SCALE_SCORE + ".SAT-Reading");
        f.setTimeSlot("MOST_RECENT_RESULT");
        String scoreSAT = resolver.get(f, student);
        assertEquals("scale score should be 682", "682", scoreSAT);
        
        // get the highest SAT Reading scale score
        f.setValue("SAT." + Constants.ATTR_SCALE_SCORE + ".SAT-Reading");
        f.setTimeSlot("HIGHEST_EVER");
        scoreSAT = resolver.get(f, student);
        assertEquals("scale score should be 685", "685", scoreSAT);
    }
    
    @Test
    public void testGetScore() throws Exception {
        
        // test get scale score
        String scaleScore = resolver.getScore(studentAssmt, Constants.ATTR_SCALE_SCORE, null);
        assertEquals("Scale score should be 250", "250", scaleScore);
        
        // test get perf level from scale score calculation when performance level descriptor does
        // not have an integer value
        String perfLevel = resolver.getScore(studentAssmt, Constants.ATTR_PERF_LEVEL, null);
        assertEquals("Perf Level should be 3 that calculated based on scale score", "3", perfLevel);
        
        // test get perf level from performance level descriptor
        perfLevel = resolver.getScore(studentAssmtWithPerfLevel, Constants.ATTR_PERF_LEVEL, null);
        assertEquals("Perf Level should be 4 based on performance level descriptor", "4", perfLevel);
        
        // test get scale score from objective assessment for SAT-Writing
        String score = resolver.getScore(studentAssmtWithObjAssmt, Constants.ATTR_SCALE_SCORE, "SAT-Writing");
        assertEquals("SAT-Writing objective assessment score should be 680", "680", score);

        // test get percentile from objective assessment for SAT-Writing
        String percentile = resolver.getScore(studentAssmtWithObjAssmt, Constants.ATTR_PERCENTILE, "SAT-Writing");
        assertEquals("SAT-Writing objective assessment score percentile should be 80", "80", percentile);
        
        // test get scale score from objective assessment for SAT-Reading
        score = resolver.getScore(studentAssmtWithObjAssmt, Constants.ATTR_SCALE_SCORE, "SAT-Reading");
        assertEquals("SAT-Writing objective assessment score should be 682", "682", score);
        
        // test get percentile from objective assessment for SAT-Reading
        percentile = resolver.getScore(studentAssmtWithObjAssmt, Constants.ATTR_PERCENTILE, "SAT-Reading");
        assertEquals("SAT-Writing objective assessment score percentile should be 82", "82", percentile);

    }

    @Test
    public void testGetCutpoint() throws Exception {
        // get the ISAT Reading cutpoints
        Field f = new Field();
        f.setValue("ISAT Reading.Scale score");
        f.setTimeSlot("MOST_RECENT_SCORE");
        List<Integer> cutpoints = resolver.getCutpoints(f, student);
        assertEquals(5, cutpoints.size());
        assertEquals(120, cutpoints.get(0).intValue());
        assertEquals(180, cutpoints.get(1).intValue());
        assertEquals(231, cutpoints.get(2).intValue());
        assertEquals(278, cutpoints.get(3).intValue());
        assertEquals(364, cutpoints.get(4).intValue());
    }

    // --- helper functions ---
    private List<GenericEntity> getAssessments() {
        /*
         * String studentId = "111111111";
         * student = new GenericEntity();
         * student.put("id", studentId);
         * String[] studentIdArray = (String[]) Arrays.asList(studentId).toArray();
         * List<String> studentIds = Arrays.asList(studentIdArray);
         * MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
         * 
         * EntityManager entityManager = new EntityManager();
         * entityManager.setApiClient(mockClient);
         * 
         * ConfigManager configManager = new ConfigManager();
         * configManager.setApiClient(mockClient);
         * configManager.setEntityManager(entityManager);
         * ViewConfig config = configManager.getConfig("rbraverman", "IL_K-3"); // this view has
         * Dibels and TRC
         * 
         * PopulationManager aManager = new PopulationManager();
         * when(mockClient.getFilename("mock_data/rbraverman/school.json")).thenReturn(
         * "src/test/resources/mock_data/rbraverman/school.json");
         * when(mockClient.getFilename("mock_data/rbraverman/custom_view_config.json")).thenReturn(
         * "src/test/resources/mock_data/rbraverman/custom_view_config.json");
         * aManager.setEntityManager(entityManager);
         * List<GenericEntity> assmts = aManager.getAssessments("rbraverman", studentIds, config);
         * return assmts;
         */
        return null;
    }

    private List<GenericEntity> getAssessmentMetaData() {
        /*
         * EntityManager entityManager = new EntityManager();
         * PopulationManager aManager = new PopulationManager();
         * MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
         * when(mockClient.getFilename("mock_data/assessment_meta_data.json")).thenReturn(
         * "src/test/resources/mock_data/assessment_meta_data.json");
         * entityManager.setApiClient(mockClient);
         * aManager.setEntityManager(entityManager);
         * List<AssessmentMetaData> metaData = aManager.getAssessmentMetaData("rbraverman");
         * return metaData;
         */
        return null;
    }
    
    private List<GenericEntity> createAssessments() {
        String stringAssmt1 = "{ \"id\":\"1\", \"assessmentIdentificationCode\" : [ { \"identificationSystem\" : \"Test Contractor\", \"ID\" : "
                + "\"Grade 8 2011 ISAT Reading\" } ], \"academicSubject\" : \"Reading\", \"contentStandard\" : \"State Standard\", "
                + "\"assessmentFamilyHierarchyName\" : \"ISAT.ISAT Reading for Grades 3-8.ISAT Reading for Grade 8\", \"assessmentCategory\" : "
                + "\"State summative assessment 3-8 general\", \"assessmentPerformanceLevel\" : [ { \"performanceLevelDescriptor\" : { "
                + "\"description\" : \"1\" }, \"assessmentReportingMethod\" : \"Scale score\", \"minimumScore\" : 120, "
                + "\"maximumScore\" : 180 }, { \"performanceLevelDescriptor\" : { \"description\" : \"2\" }, \"assessmentReportingMethod\" : "
                + "\"Scale score\", \"minimumScore\" : 180, \"maximumScore\" : 231 }, { \"performanceLevelDescriptor\" : { \"description\" : "
                + "\"3\" }, \"assessmentReportingMethod\" : \"Scale score\", \"minimumScore\" : 231, \"maximumScore\" : 278 }, { "
                + "\"performanceLevelDescriptor\" : { \"description\" : \"4\" }, \"assessmentReportingMethod\" : \"Scale score\", "
                + "\"minimumScore\" : 278, \"maximumScore\" : 364 } ], \"revisionDate\" : \"2011-03-12\", \"gradeLevelAssessed\" : "
                + "\"Eighth grade\", \"assessmentTitle\" : \"Grade 8 2011 ISAT Reading\", \"maxRawScore\" : 450, \"version\" : 1 }";
        String stringAssmt2 = "{\"id\":\"2\", \"assessmentTitle\":\"SAT\",\"assessmentFamilyHierarchyName\":\"SAT\",\"assessmentIdentificationCode\":"
                + "[{\"identificationSystem\":\"School\",\"ID\":\"01234B\"}],\"academicSubject\":\"Reading\",\"assessmentCategory\":"
                + "\"College Addmission Test\",\"gradeLevelAssessed\":\"Twelfth grade\", \"lowestGradeLevelAssessed\":\"Eleventh grade\","
                + "\"assessmentPerformanceLevel\":[],\"assessmentPeriodDescriptor\":{\"codeValue\":\"assessment\",\"description\":\"assessment\","
                + "\"beginDate\":\"2011-01-01\",\"endDate\":\"2011-02-01\"},\"maxRawScore\":2400,\"minRawScore\":600,\"objectiveAssessment\":"
                + "[{\"identificationCode\":\"SAT-Writing\",\"maxRawScore\":800,\"percentOfAssessment\":33},{\"identificationCode\":\"SAT-Math\","
                + "\"maxRawScore\":800,\"percentOfAssessment\":33},{\"identificationCode\":\"SAT-Critical Reading\",\"maxRawScore\":800,\"percentOfAssessment\":33}],"
                + "\"contentStandard\":\"LEA Standard\",\"version\":2}";
        Gson gson = new Gson();
        Map assmt1 = gson.fromJson(stringAssmt1, Map.class);
        Map assmt2 = gson.fromJson(stringAssmt2, Map.class);
        GenericEntity genericAssmt1 = new GenericEntity(assmt1);
        GenericEntity genericAssmt2 = new GenericEntity(assmt2);
        List<GenericEntity> assmts = new ArrayList<GenericEntity>();
        assmts.add(genericAssmt1);
        assmts.add(genericAssmt2);
        return assmts;
    }
    
    /*
     * create a student assessment association that includes a performanceLevelDescriptor which only
     * has a string description value and can not be convert to Integer, the Perf Level will be
     * calculated from scale score
     */
    private GenericEntity createStudentAssessment() {
        
        String stringStudentAssessment = "{ \"administrationDate\" : \"2011-03-01\", \"specialAccommodations\" : [], \"administrationLanguage\" : "
                + "\"English\", \"studentId\" : \"1\", \"assessmentId\" : \"1\", \"serialNumber\" : \"0\", "
                + "\"performanceLevelDescriptors\" : [ { \"description\" : \"Above bench Mark\" }], \"scoreResults\" : [ { \"result\" : \"250\", \"assessmentReportingMethod\" : \"Scale score\" } ], "
                + "\"administrationEnvironment\" : \"Testing Center\", \"retestIndicator\" : \"Primary Administration\", \"linguisticAccommodations\" : [] }";
        Gson gson = new Gson();
        GenericEntity studentAssmt = gson.fromJson(stringStudentAssessment, GenericEntity.class);
        return studentAssmt;
    }
    
    /*
     * create a student assessment association that includes a performanceLevelDescriptor which has
     * an Integer description value, the Perf Level will be retrieved from this value
     */
    private GenericEntity createStudentAssessmentWithPerfLevel() {
        
        String stringStudentAssessment = "{ \"administrationDate\" : \"2011-02-01\", \"specialAccommodations\" : [], \"administrationLanguage\" : "
                + "\"English\", \"studentId\" : \"1\", \"assessmentId\" : \"1\", \"serialNumber\" : \"0\", "
                + "\"performanceLevelDescriptors\" : [ { \"description\" : \"4\" }], \"scoreResults\" : [ { \"result\" : \"250\", \"assessmentReportingMethod\" : \"Scale score\" } ], "
                + "\"administrationEnvironment\" : \"Testing Center\", \"retestIndicator\" : \"Primary Administration\", \"linguisticAccommodations\" : [] }";
        Gson gson = new Gson();
        GenericEntity studentAssmt = gson.fromJson(stringStudentAssessment, GenericEntity.class);
        return studentAssmt;
    }
    
    /*
     * create a student assessment association that includes student objective assessment which
     * includes SAT-Writing score 680 percentile 80, SAT-Math score 681 percentile 81, and
     * SAT-Reading score 682 percentile 82
     */
    private GenericEntity createStudentAssessmentWithObjAssmt() {
        String stringStudentAssessment = "{\"studentId\":\"1\",\"assessmentId\":\"2\",\"administrationDate\":\"2011-05-10\",\"administrationEndDate\":"
                + "\"2011-06-15\",\"retestIndicator\":\"1st Retest\",\"gradeLevelWhenAssessed\":\"Twelfth grade\",\"scoreResults\":[{\"assessmentReportingMethod\":"
                + "\"Scale score\",\"result\":\"2063\"},{\"assessmentReportingMethod\":\"Percentile\",\"result\":\"92\"}],\"performanceLevelDescriptors\":[],"
                + "\"studentObjectiveAssessments\":[{\"scoreResults\":[{\"assessmentReportingMethod\":\"Scale score\",\"result\":\"680\"},"
                + "{\"assessmentReportingMethod\":\"Percentile\",\"result\":\"80\"}],\"objectiveAssessment\":{\"identificationCode\":\"SAT-Writing\"}},"
                + "{\"scoreResults\":[{\"assessmentReportingMethod\":\"Scale score\",\"result\":\"681\"},{\"assessmentReportingMethod\":\"Percentile\",\"result\":"
                + "\"81\"}],\"objectiveAssessment\":{\"identificationCode\":\"SAT-Math\"}},{\"scoreResults\":[{\"assessmentReportingMethod\":\"Scale score\",\"result\":\"682\"},"
                + "{\"assessmentReportingMethod\":\"Percentile\",\"result\":\"82\"}],\"objectiveAssessment\":{\"identificationCode\":\"SAT-Reading\"}}]}";
        
        Gson gson = new Gson();
        GenericEntity studentAssmt = gson.fromJson(stringStudentAssessment, GenericEntity.class);
        return studentAssmt;
    }
    
    /*
     * create a student assessment association that includes student objective assessment which
     * includes SAT-Writing score 683 percentile 83, SAT-Math score 684 percentile 84, and
     * SAT-Reading score 685 percentile 85
     */
    private GenericEntity createStudentAssessmentWithObjAssmt1() {
        String stringStudentAssessment = "{\"studentId\":\"1\",\"assessmentId\":\"2\",\"administrationDate\":\"2011-04-10\",\"administrationEndDate\":"
                + "\"2011-06-15\",\"retestIndicator\":\"1st Retest\",\"gradeLevelWhenAssessed\":\"Twelfth grade\",\"scoreResults\":[{\"assessmentReportingMethod\":"
                + "\"Scale score\",\"result\":\"2072\"},{\"assessmentReportingMethod\":\"Percentile\",\"result\":\"92\"}],\"performanceLevelDescriptors\":[],"
                + "\"studentObjectiveAssessments\":[{\"scoreResults\":[{\"assessmentReportingMethod\":\"Scale score\",\"result\":\"683\"},"
                + "{\"assessmentReportingMethod\":\"Percentile\",\"result\":\"83\"}],\"objectiveAssessment\":{\"identificationCode\":\"SAT-Writing\"}},"
                + "{\"scoreResults\":[{\"assessmentReportingMethod\":\"Scale score\",\"result\":\"684\"},{\"assessmentReportingMethod\":\"Percentile\",\"result\":"
                + "\"84\"}],\"objectiveAssessment\":{\"identificationCode\":\"SAT-Math\"}},{\"scoreResults\":[{\"assessmentReportingMethod\":\"Scale score\",\"result\":\"685\"},"
                + "{\"assessmentReportingMethod\":\"Percentile\",\"result\":\"85\"}],\"objectiveAssessment\":{\"identificationCode\":\"SAT-Reading\"}}]}";
        
        Gson gson = new Gson();
        GenericEntity studentAssmt = gson.fromJson(stringStudentAssessment, GenericEntity.class);
        return studentAssmt;
    }

    private GenericEntity createStudent() {
        String studentString = "{ \"id\":\"1\",\"otherName\" : [], \"sex\" : \"Female\", \"studentIndicators\" : [], \"studentCharacteristics\" : [], "
                + "\"hispanicLatinoEthnicity\" : \"false\", \"economicDisadvantaged\" : \"false\", \"disabilities\" : [], \"cohortYears\" : [], "
                + "\"homeLanguages\" : [], \"section504Disabilities\" : [], \"learningStyles\" : null, \"limitedEnglishProficiency\" : \"Limited\","
                + " \"race\" : [], \"programParticipations\" : [], \"studentIdentificationCode\" : [], \"studentUniqueStateId\" : \"258392050\", "
                + "\"languages\" : [], \"address\" : [], \"displacementStatus\" : null, \"schoolFoodServicesEligibility\" : \"Reduced price\", "
                + "\"name\" : { \"middleName\" : null, \"generationCodeSuffix\" : \"Jr\", \"lastSurname\" : \"Harper\", \"firstName\" : \"Patricia\" }, "
                + "\"electronicMail\" : [], \"telephone\" : [], \"birthData\" : { \"birthDate\" : \"1994-01-09\" } }";
        Gson gson = new Gson();
        GenericEntity student = gson.fromJson(studentString, GenericEntity.class);
        student.appendToList(Constants.ATTR_STUDENT_ASSESSMENTS, createStudentAssessment());
        student.appendToList(Constants.ATTR_STUDENT_ASSESSMENTS, createStudentAssessmentWithPerfLevel());
        student.appendToList(Constants.ATTR_STUDENT_ASSESSMENTS, createStudentAssessmentWithObjAssmt());
        student.appendToList(Constants.ATTR_STUDENT_ASSESSMENTS, createStudentAssessmentWithObjAssmt1());

        return student;
    }
}
