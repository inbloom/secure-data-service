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
 * @author dong liu <dliu@wgen.net>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class QueryConverterTest {
    @Autowired
    QueryConverter queryConverter;

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

    }

    @Test
    public void testStringToQuery() {
        assertEquals(queryConverter.stringToQuery("studentSchoolAssociation", "entryGradeLevel=First grade")
                .getQueryObject(), new Query(Criteria.where("body.entryGradeLevel").is("First grade")).getQueryObject());
    }

    @Test(expected = QueryParseException.class)
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQueryException1() {
        queryConverter.stringToQuery("studentSchoolAssociationn", "nonexist.field=test");
    }

    @Test(expected = QueryParseException.class)
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQueryException2() {
        queryConverter.stringToQuery("studentSchoolAssociation", "incomplete.field=");
    }

    @Test(expected = QueryParseException.class)
    @Ignore("need to be replaced with neutral schema")
    public void testStringToQueryException3() {
        queryConverter.stringToQuery("studentSchoolAssociation", "incomplete.field");
    }

    @Test
    public void testStringToQueryReservedKeys() {
        assertEquals(queryConverter.stringToQuery("student", "sessionId=12345678").getQueryObject(),
                new Query().getQueryObject());
        assertEquals(queryConverter.stringToQuery("student", "start-index=10").getQueryObject(),
                new Query().getQueryObject());
    }
}
