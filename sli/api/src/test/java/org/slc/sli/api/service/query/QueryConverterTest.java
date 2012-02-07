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
        assertEquals(queryConverter.findParamType("student", "nonexist.field"), "NULL");
        assertEquals(queryConverter.findParamType("student", "studentUniqueStateId"), "STRING");
        assertEquals(queryConverter.findParamType("student", "hispanicLatinoEthnicity"), "BOOLEAN");

        // test school entity
        assertEquals(queryConverter.findParamType("school", "stateOrganizationId"), "STRING");
        assertEquals(queryConverter.findParamType("school", "nameOfInstitution"), "STRING");
        assertEquals(queryConverter.findParamType("school", "address"), "LIST");
        assertEquals(queryConverter.findParamType("school", "address.streetNumberName"), "STRING");
        assertEquals(queryConverter.findParamType("school", "address.addressType"), "TOKEN");
        assertEquals(queryConverter.findParamType("school", "telephone.telephoneNumber"), "STRING");

        // test student school association entity
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "studentId"), "STRING");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "schoolId"), "STRING");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "entryDate"), "DATE");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "entryGradeLevel"), "TOKEN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "repeatGradeIndicator"), "BOOLEAN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "classOf"), "TOKEN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "educationalPlans.educationalPlan"),
                "TOKEN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation", "graduationPlan.GraduationPlanType"),
                "TOKEN");
        assertEquals(
                queryConverter.findParamType("studentSchoolAssociation", "graduationPlan.totalCreditsRequired.credit"),
                "DOUBLE");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation",
                "graduationPlan.totalCreditsRequired.creditType"), "TOKEN");
        assertEquals(queryConverter.findParamType("studentSchoolAssociation",
                "graduationPlan.totalCreditsRequired.creditConversion"), "DOUBLE");

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
