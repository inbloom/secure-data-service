package org.slc.sli.unit.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.AssessmentMetaDataResolver;

/**
 * Unit tests for the AssessmentMetaDataResolver class.
 * 
 * @author dliu
 */

public class AssessmentMetaDataResolverTest {
    private AssessmentMetaDataResolver resolver;
    
    @Before
    public void setup() {
        resolver = new AssessmentMetaDataResolver(createAssessments());
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
    
    @Test
    public void testCalculatePerfLevel() {
        assertEquals("perf level for scale score 130 should be 1", "1", resolver.calculatePerfLevel("1", "130"));
        assertEquals("perf level for scale score 190 should be 2", "2", resolver.calculatePerfLevel("1", "190"));
        assertEquals("perf level for scale score 250 should be 3", "3", resolver.calculatePerfLevel("1", "250"));
        assertEquals("perf level for scale score 300 should be 4", "4", resolver.calculatePerfLevel("1", "300"));
        
    }
}

