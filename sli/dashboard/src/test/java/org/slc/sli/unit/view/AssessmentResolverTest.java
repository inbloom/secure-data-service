package org.slc.sli.unit.view;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.AssessmentResolver;

/**
 * Unit tests for the AssessmentResolver class.
 * Uses canned data for user rbraverman
 *
 */
public class AssessmentResolverTest {

    // test subject
    private AssessmentResolver resolver;
    
    // only one student data for testing. 
    private GenericEntity student;

    @Before
    public void setup() {
        resolver = new AssessmentResolver(getAssessments(), getAssessmentMetaData());
    }

    @Test
    public void testGet() throws Exception {
        Field f = new Field();
        // get the D-next performance level
        f.setValue("DIBELS_NEXT.perfLevel");
        f.setTimeSlot("MOST_RECENT_SCORE");
        String dnextPerfLevel = resolver.get(f, student);
        assertEquals("@B", dnextPerfLevel);
        // get the D-next Comp score
        f.setValue("DIBELS_NEXT.scaleScore");
        f.setTimeSlot("MOST_RECENT_SCORE");
        String dnextScore = resolver.get(f, student);
        assertEquals("111", dnextScore);
        // get the TRC reading level
        f.setValue("TRC.lexileScore");
        f.setTimeSlot("MOST_RECENT_SCORE");
        String trcScore = resolver.get(f, student);
        assertEquals("K", trcScore);
    }

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
        String studentId = "111111111";
        student = new GenericEntity();
        student.put("id", studentId);
        String[] studentIdArray = (String[]) Arrays.asList(studentId).toArray();
        List<String> studentIds = Arrays.asList(studentIdArray);
        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
        
        EntityManager entityManager = new EntityManager();
        entityManager.setApiClient(mockClient);

        ConfigManager configManager = new ConfigManager();
        configManager.setApiClient(mockClient);
        configManager.setEntityManager(entityManager);
        ViewConfig config = configManager.getConfig("rbraverman", "IL_K-3"); // this view has Dibels and TRC

        PopulationManager aManager = new PopulationManager();
        when(mockClient.getFilename("mock_data/rbraverman/school.json")).thenReturn("src/test/resources/mock_data/rbraverman/school.json");
        when(mockClient.getFilename("mock_data/rbraverman/custom_view_config.json")).thenReturn("src/test/resources/mock_data/rbraverman/custom_view_config.json");
        aManager.setEntityManager(entityManager);
        List<GenericEntity> assmts = aManager.getAssessments("rbraverman", studentIds, config);
        return assmts;*/
        return null;
    }
    private List<GenericEntity> getAssessmentMetaData() {
        /*
        EntityManager entityManager = new EntityManager();
        PopulationManager aManager = new PopulationManager(); 
        MockAPIClient mockClient = PowerMockito.spy(new MockAPIClient());
        when(mockClient.getFilename("mock_data/assessment_meta_data.json")).thenReturn("src/test/resources/mock_data/assessment_meta_data.json");
        entityManager.setApiClient(mockClient);
        aManager.setEntityManager(entityManager);
        List<AssessmentMetaData> metaData = aManager.getAssessmentMetaData("rbraverman");
        return metaData;*/
        return null;
    }
}
