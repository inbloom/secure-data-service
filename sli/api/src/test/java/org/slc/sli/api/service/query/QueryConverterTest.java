package org.slc.sli.api.service.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Query Converter Test
 * 
 * @author dong liu <dliu@wgen.net>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class QueryConverterTest {
    @Autowired
    QueryConverter queryConverter;
    
    @Test
    public void testNull() {
        // should always return a null, so callers don't have to worry about null checking
        assertTrue(queryConverter.stringToQuery(null, null, null, null) != null);
    }
    
    @Test
    public void testfindParamType() {
        // test student entity
        assertEquals("NULL", queryConverter.findParamType("student", "nonexist.field"));
        assertEquals("STRING", queryConverter.findParamType("student", "studentUniqueStateId"));
        assertEquals("BOOLEAN", queryConverter.findParamType("student", "hispanicLatinoEthnicity"));
        
        // test school entity
        assertEquals("STRING", queryConverter.findParamType("school", "stateOrganizationId"));
        assertEquals("STRING", queryConverter.findParamType("school", "nameOfInstitution"));
        assertEquals("LIST", queryConverter.findParamType("school", "address"));
        assertEquals("STRING", queryConverter.findParamType("school", "address.streetNumberName"));
        assertEquals("TOKEN", queryConverter.findParamType("school", "address.addressType"));
        assertEquals("STRING", queryConverter.findParamType("school", "telephone.telephoneNumber"));
        
        // test student school association entity
        assertEquals("STRING", queryConverter.findParamType("studentSchoolAssociation", "studentId"));
        assertEquals("STRING", queryConverter.findParamType("studentSchoolAssociation", "schoolId"));
        assertEquals("DATE", queryConverter.findParamType("studentSchoolAssociation", "entryDate"));
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation", "entryGradeLevel"));
        assertEquals("BOOLEAN", queryConverter.findParamType("studentSchoolAssociation", "repeatGradeIndicator"));
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation", "classOf"));
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation", "educationalPlans"));
        assertEquals("TOKEN",
                queryConverter.findParamType("studentSchoolAssociation", "graduationPlan.GraduationPlanType"));
        assertEquals("DOUBLE",
                queryConverter.findParamType("studentSchoolAssociation", "graduationPlan.totalCreditsRequired.credit"));
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation",
                "graduationPlan.totalCreditsRequired.creditType"));
        assertEquals("DOUBLE", queryConverter.findParamType("studentSchoolAssociation",
                "graduationPlan.totalCreditsRequired.creditConversion"));
        
        // test assessment entity
        assertEquals("STRING", queryConverter.findParamType("assessment", "assessmentTitle"));
        assertEquals("TOKEN", queryConverter.findParamType("assessment", "academicSubject"));
        assertEquals("TOKEN", queryConverter.findParamType("assessment", "assessmentCategory"));
        assertEquals("TOKEN", queryConverter.findParamType("assessment", "contentStandard"));
        assertEquals("INTEGER", queryConverter.findParamType("assessment", "version"));
        
        // test student assessment association
        assertEquals("DATE", queryConverter.findParamType("studentAssessmentAssociation", "administrationDate"));
        assertEquals("DATE", queryConverter.findParamType("studentAssessmentAssociation", "administrationEndDate"));
        assertEquals("TOKEN", queryConverter.findParamType("studentAssessmentAssociation", "retestIndicator"));
        assertEquals("STRING", queryConverter.findParamType("studentAssessmentAssociation", "scoreResults.result"));
        
    }
    
    @Test
    public void testSorting() {
        Query query = new Query(Criteria.where("body.entryGradeLevel").is("First grade"));
        query.sort().on("body.entryGradeLevel", Order.DESCENDING);
        
        Query convertedQuery = queryConverter.stringToQuery("studentSchoolAssociation", "entryGradeLevel=First grade",
                "entryGradeLevel", SortOrder.descending);
        assertEquals(query.getQueryObject(), convertedQuery.getQueryObject());
        assertEquals(query.getSortObject(), convertedQuery.getSortObject());
    }
    
    @Test
    public void testStringToQuery() {
        assertEquals(new Query(Criteria.where("body.entryGradeLevel").is("First grade")).getQueryObject(),
                queryConverter.stringToQuery("studentSchoolAssociation", "entryGradeLevel=First grade")
                        .getQueryObject());
    }
    
    @Test(expected = QueryParseException.class)
    @Ignore("remove after neutral schema validation for all existint entities are turned on")
    public void testStringToQueryException1() {
        queryConverter.stringToQuery("studentSchoolAssociationn", "nonexist.field=test");
    }
    
    @Test(expected = QueryParseException.class)
    public void testStringToQueryException2() {
        queryConverter.stringToQuery("studentSchoolAssociation", "incomplete.field=");
    }
    
    @Test(expected = QueryParseException.class)
    public void testStringToQueryException3() {
        queryConverter.stringToQuery("studentSchoolAssociation", "incomplete.field");
    }
    
    @Test
    public void testStringToQueryReservedKeys() {
        assertEquals(new Query().getQueryObject(), queryConverter.stringToQuery("student", "sessionId=12345678")
                .getQueryObject());
        assertEquals(new Query().getQueryObject(), queryConverter.stringToQuery("student", "start-index=10")
                .getQueryObject());
        assertEquals(new Query().getQueryObject(),
                queryConverter.stringToQuery("studentSchoolAssociation", "full-entities=true").getQueryObject());
    }
}
