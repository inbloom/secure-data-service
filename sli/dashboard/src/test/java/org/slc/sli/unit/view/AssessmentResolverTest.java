package org.slc.sli.unit.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.view.AssessmentResolver;

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

    @Before
    public void setup() {
        // resolver = new AssessmentResolver(getAssessments(), getAssessmentMetaData());
        resolver = new AssessmentResolver(null, createAssessments());
        student = createStudent();
        studentAssmt = createStudentAssessment();

    }

    @Test
    public void testGet() throws Exception {
        Field f = new Field();
        // get the ISAT Reading performance level
        f.setValue("ISAT Reading." + Constants.ATTR_PERF_LEVEL);
        f.setTimeSlot("MOST_RECENT_SCORE");
        String perfLevel = resolver.get(f, student);
        assertEquals("ISAT Reading Perf Level should be 3", "3", perfLevel);
        // get the ISAT Reading scale score
        f.setValue("ISAT Reading." + Constants.ATTR_SCALE_SCORE);
        f.setTimeSlot("MOST_RECENT_SCORE");
        String scaleScore = resolver.get(f, student);
        assertEquals("scale score should be 250", "250", scaleScore);
    }
    
    @Test
    public void testGetScore() throws Exception {
        
        // test get scale score
        String scaleScore = resolver.getScore(studentAssmt, Constants.ATTR_SCALE_SCORE);
        assertEquals("Scale score should be 250", "250", scaleScore);
        
        // test get perf level
        String perfLevel = resolver.getScore(studentAssmt, Constants.ATTR_PERF_LEVEL);
        assertEquals("Perf Level should be 3", "3", perfLevel);

    }

    @Ignore
    @Test
    public void testGetCutpoint() throws Exception {
        // get the D-next cutpoints
        Field f = new Field();
        f.setValue("DIBELS_NEXT.scaleScore");
        f.setTimeSlot("MOST_RECENT_SCORE");
        List<Integer> cutpoints = resolver.getCutpoints(f, student);
        // for this student, most recent dibels next score is grade 2 BOY
        assertEquals(4, cutpoints.size());
        assertEquals(3, cutpoints.get(0).intValue());
        assertEquals(109, cutpoints.get(1).intValue());
        assertEquals(141, cutpoints.get(2).intValue());
        assertEquals(359, cutpoints.get(3).intValue());
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
        String stringAssmt = "{ \"id\":\"1\", \"assessmentIdentificationCode\" : [ { \"identificationSystem\" : \"Test Contractor\", \"ID\" : "
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
        Gson gson = new Gson();
        Map assmt = gson.fromJson(stringAssmt, Map.class);
        GenericEntity genericAssmt = new GenericEntity(assmt);
        List<GenericEntity> assmts = new ArrayList<GenericEntity>();
        assmts.add(genericAssmt);
        return assmts;
    }
    
    private GenericEntity createStudentAssessment() {
        
        String stringStudentAssessment = "{ \"administrationDate\" : \"2011-03-01\", \"specialAccommodations\" : [], \"administrationLanguage\" : "
                + "\"English\", \"studentId\" : \"1\", \"assessmentId\" : \"1\", \"serialNumber\" : \"0\", "
                + "\"performanceLevelDescriptors\" : [ { \"description\" : \"Above bench Mark\" }], \"scoreResults\" : [ { \"result\" : \"250\", \"assessmentReportingMethod\" : \"Scale score\" } ], "
                + "\"administrationEnvironment\" : \"Testing Center\", \"retestIndicator\" : \"Primary Administration\", \"linguisticAccommodations\" : [] }";
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

        return student;
    }
}
