package org.slc.sli.api.service.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Query Converter Test 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class QueryConverterTest {
    @Autowired
    QueryConverter queryConverter;
    
    @Test
    public void testfindParamType() {
        assertEquals(queryConverter.findParamType("studentAssessmentAssociation", "performanceLevel"), "STRING");
        assertEquals(queryConverter.findParamType("studentAssessmentAssociation", "scoreResults.result"), "STRING");
        assertEquals(queryConverter.findParamType("student", "nonexist.field"), "NULL");
        assertEquals(queryConverter.findParamType("student", "studentUniqueStateId"), "INT");
        assertEquals(queryConverter.findParamType("student", "hispanicLatinoEthnicity"), "BOOLEAN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "entryGradeLevel"), "ENUM");
    }
}
