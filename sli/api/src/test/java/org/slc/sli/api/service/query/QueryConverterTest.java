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
    BasicQueryConverter queryConverter;
    
    @Test
    public void testNull() {
        // should always return a null, so callers don't have to worry about null checking
        assertTrue(queryConverter.stringToQuery(null, null, null, null) != null);
    }
    
    @Test
    public void testfindParamType() {
        // test student entity
        assertEquals("NULL", queryConverter.findParamType("student", "nonexist.field").getType());
        assertEquals("STRING", queryConverter.findParamType("student", "studentUniqueStateId").getType());
        assertEquals("BOOLEAN", queryConverter.findParamType("student", "hispanicLatinoEthnicity").getType());
        
        // test school entity
        assertEquals("STRING", queryConverter.findParamType("school", "stateOrganizationId").getType());
        assertEquals("STRING", queryConverter.findParamType("school", "nameOfInstitution").getType());
        assertEquals("LIST", queryConverter.findParamType("school", "address").getType());
        assertEquals("STRING", queryConverter.findParamType("school", "address.streetNumberName").getType());
        assertEquals("TOKEN", queryConverter.findParamType("school", "address.addressType").getType());
        assertEquals("STRING", queryConverter.findParamType("school", "telephone.telephoneNumber").getType());
        
        // test student school association entity
        assertEquals("STRING", queryConverter.findParamType("studentSchoolAssociation", "studentId").getType());
        assertEquals("STRING", queryConverter.findParamType("studentSchoolAssociation", "schoolId").getType());
        assertEquals("DATE", queryConverter.findParamType("studentSchoolAssociation", "entryDate").getType());
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation", "entryGradeLevel").getType());
        assertEquals("BOOLEAN", queryConverter.findParamType("studentSchoolAssociation", "repeatGradeIndicator")
                .getType());
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation", "classOf").getType());
        assertEquals("TOKEN", queryConverter.findParamType("studentSchoolAssociation", "educationalPlans").getType());
        assertEquals("TOKEN",
                queryConverter.findParamType("studentSchoolAssociation", "graduationPlan.GraduationPlanType").getType());
        assertEquals("DOUBLE",
                queryConverter.findParamType("studentSchoolAssociation", "graduationPlan.totalCreditsRequired.credit")
                        .getType());
        assertEquals(
                "TOKEN",
                queryConverter.findParamType("studentSchoolAssociation",
                        "graduationPlan.totalCreditsRequired.creditType").getType());
        assertEquals(
                "DOUBLE",
                queryConverter.findParamType("studentSchoolAssociation",
                        "graduationPlan.totalCreditsRequired.creditConversion").getType());
        
        // test assessment entity
        assertEquals("STRING", queryConverter.findParamType("assessment", "assessmentTitle").getType());
        assertEquals("TOKEN", queryConverter.findParamType("assessment", "academicSubject").getType());
        assertEquals("TOKEN", queryConverter.findParamType("assessment", "assessmentCategory").getType());
        assertEquals("TOKEN", queryConverter.findParamType("assessment", "contentStandard").getType());
        assertEquals("INTEGER", queryConverter.findParamType("assessment", "version").getType());
        
        // test student assessment association
        assertEquals("DATE", queryConverter.findParamType("studentAssessmentAssociation", "administrationDate")
                .getType());
        assertEquals("DATE", queryConverter.findParamType("studentAssessmentAssociation", "administrationEndDate")
                .getType());
        assertEquals("TOKEN", queryConverter.findParamType("studentAssessmentAssociation", "retestIndicator").getType());
        assertEquals("STRING", queryConverter.findParamType("studentAssessmentAssociation", "scoreResults.result")
                .getType());
        
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
