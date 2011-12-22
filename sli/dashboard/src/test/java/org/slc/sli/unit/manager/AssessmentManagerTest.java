package org.slc.sli.unit.manager;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.ConfigManager;
import org.slc.sli.config.ViewConfig;
import org.slc.sli.entity.Assessment;
import org.slc.sli.manager.AssessmentManager;



/**
 * Unit tests for the StudentManager class.
 * 
 */
public class AssessmentManagerTest {

    @Before
    public void setup() {
        
    }

    @Test
    public void testGetAssessments() {
        
        String[] studentIdArray = {"453827070", "943715230"};
        List<String> studentIds = Arrays.asList(studentIdArray);
        ViewConfig config = ConfigManager.getInstance().getConfig("lkim", "IL_3-8_ELA"); // this view has ISAT Reading and ISAT Writing
        List<Assessment> assmts = AssessmentManager.getInstance().getAssessments("lkim", studentIds, config);
        assertEquals(24, assmts.size()); // mock assmt data has 6 assmt results/student/assmt type
    }
    
}
