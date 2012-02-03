package org.slc.sli.api.service.query;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    @Ignore("need to be replaced with neutral schema")
    public void testfindParamType() {
        assertEquals(queryConverter.findParamType("studentAssessmentAssociation", "performanceLevel"), "STRING");
        assertEquals(queryConverter.findParamType("studentAssessmentAssociation", "scoreResults.result"), "STRING");
        assertEquals(queryConverter.findParamType("student", "nonexist.field"), "NULL");
        assertEquals(queryConverter.findParamType("student", "studentUniqueStateId"), "STRING");
        assertEquals(queryConverter.findParamType("student", "hispanicLatinoEthnicity"), "BOOLEAN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "entryGradeLevel"), "ENUM");
    }
    
    @Test
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQuery() {
        assertEquals(queryConverter.stringToQuery("studentAssessmentAssociation", "performanceLevel=4")
                .getQueryObject(), new Query(Criteria.where("body.performanceLevel").is("4")).getQueryObject());
    }
    
    @Test(expected = QueryParseException.class)
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQueryException1() {
        queryConverter.stringToQuery("studentAssessmentAssociation", "nonexist.field=test");
    }
    
    @Test(expected = QueryParseException.class)
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQueryException2() {
        queryConverter.stringToQuery("studentAssessmentAssociation", "incomplete.field=");
    }
    
    @Test(expected = QueryParseException.class)
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQueryException3() {
        queryConverter.stringToQuery("studentAssessmentAssociation", "incomplete.field");
    }

    @Test
    public void testStringToQueryReservedKeys() {
        assertEquals(queryConverter.stringToQuery("student", "sessionId=12345678").getQueryObject(),
                new Query().getQueryObject());
        assertEquals(queryConverter.stringToQuery("student", "start-index=10").getQueryObject(),
                new Query().getQueryObject());
    }
}
