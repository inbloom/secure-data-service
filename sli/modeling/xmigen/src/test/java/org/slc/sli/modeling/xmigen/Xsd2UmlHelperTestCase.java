/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.modeling.xmigen;

import junit.framework.TestCase;

/**
 * Tests the XSD to UML helper utility.
 * 
 * @author kmyers
 *
 */
public class Xsd2UmlHelperTestCase extends TestCase {
    
    public void testLhsReplace() {
        assertEquals("bcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "a", ""));
        assertEquals("bcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "A", ""));
        assertEquals("xbcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "a", "x"));
        assertEquals("xybcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "a", "xy"));
    }
    
    public void testRhsReplace() {
        assertEquals("abcdef", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "g", ""));
        assertEquals("abcdefx", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "g", "x"));
        assertEquals("abcdefxy", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "g", "xy"));
    }
    
    public void testInnerReplace() {
        assertEquals("abdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "c", ""));
        assertEquals("abxdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "c", "x"));
        assertEquals("abxydefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "c", "xy"));
    }
    
    public void testEmptyReplace() {
        assertEquals("abcdefg", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "", ""));
        assertEquals("xaxbxcxdxexfxgx", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "", "x"));
        assertEquals("xyaxybxycxydxyexyfxygxy", Xsd2UmlHelper.replaceAllIgnoreCase("abcdefg", "", "xy"));
    }
    
    public void testPluralizeBasic() {
        assertEquals("reportCards", Xsd2UmlHelper.pluralize("reportCard"));
    }
    
    public void testPluralizeSpecialCases() {
        assertEquals("staff", Xsd2UmlHelper.pluralize("staff"));
        assertEquals("Staff", Xsd2UmlHelper.pluralize("Staff"));
    }
    
    public void testPluralizeEndsWithY() {
        assertEquals("gradebookEntries", Xsd2UmlHelper.pluralize("gradebookEntry"));
        assertEquals("Categories", Xsd2UmlHelper.pluralize("Category"));
        assertEquals("Agencies", Xsd2UmlHelper.pluralize("Agency"));
    }
    
    public void testPluralizeIdempotent() {
        assertEquals("grades", Xsd2UmlHelper.pluralize("grades"));
        assertEquals("reportCards", Xsd2UmlHelper.pluralize("reportCards"));
        assertEquals("learningObjectives", Xsd2UmlHelper.pluralize("learningObjectives"));
        assertEquals("learningStandards", Xsd2UmlHelper.pluralize("learningStandards"));
    }
    
    public void testCamelCase() {
        assertEquals("CIP", Xsd2UmlHelper.camelCase("CIP"));
        assertEquals("cipCode", Xsd2UmlHelper.camelCase("CIPCode"));
        assertEquals("CTE", Xsd2UmlHelper.camelCase("CTE"));
        assertEquals("GPA", Xsd2UmlHelper.camelCase("GPA"));
        assertEquals("ID", Xsd2UmlHelper.camelCase("ID"));
        assertEquals("IEP", Xsd2UmlHelper.camelCase("IEP"));
        assertEquals("LEA", Xsd2UmlHelper.camelCase("LEA"));
        assertEquals("URI", Xsd2UmlHelper.camelCase("URI"));
        assertEquals("URL", Xsd2UmlHelper.camelCase("URL"));
        assertEquals("URN", Xsd2UmlHelper.camelCase("URN"));
    }
    
    public void testMakeAssociationEndName() {
        checkEndName("fooBarBazAssociations", "BarBaz", "barBaz", 1, "FooBarBazAssociation");
        checkEndName("fooBarBazAssociations", "Foo", "foo", 1, "FooBarBazAssociation");
        checkEndName("fooBars", "BigBad", "bigBads", 1, "FooBar");
        checkEndName("bigBadFooBars", "Bad", "bigBad", 2, "FooBar");
        checkEndName("fooBarAssociations", "Bar", "bar", 1, "FooBarAssociation");
        checkEndName("fooBarAssociations", "Foo", "foo", 1, "FooBarAssociation");
        checkEndName("foos", "BarBaz", "barBaz", 1, "Foo");
        checkEndName("foos", "BarBazAssociation", "barBazAssociation", 1, "Foo");
        checkEndName("fooBars", "Big", "big", 1, "FooBar");
        checkEndName("foos", "Any", "any", 1, "Foo");
        checkEndName("disciplineActions", "Student", "students", 1, "DisciplineAction");
        checkEndName("gradingPeriods", "CalendarDate", "calendarDates", 1, "GradingPeriod");
        checkEndName("reportCards", "Student", "student", 1, "ReportCard");
        checkEndName("reportCards", "StudentCompetency", "studentCompetencies", 1, "ReportCard");
        checkEndName("reportCards", "GradingPeriod", "gradingPeriod", 1, "ReportCard");
        checkEndName("reportCards", "Grade", "grades", 1, "ReportCard");
        checkEndName("childLearningObjectives", "LearningObjective", "parentLearningObjective", 1, "LearningObjective");
        checkEndName("learningObjectives", "LearningStandard", "learningStandards", 1, "LearningObjective");
        checkEndName("fooBarObjectives", "EducationOrganization", "educationOrganization", 1, "FooBarObjective");
        checkEndName("sessions", "EducationOrganization", "school", 1, "Session");
        checkEndName("sessions", "GradingPeriod", "gradingPeriods", 1, "Session");
        checkEndName("gradingPeriodIdentityTypes", "EducationOrganization", "school", 1, "GradingPeriodIdentityType");
        checkEndName("restraintEvents", "Program", "programs", 1, "RestraintEvent");
        checkEndName("restraintEvents", "School", "school", 1, "RestraintEvent");
        checkEndName("restraintEvents", "Student", "student", 1, "RestraintEvent");
        checkEndName("studentAcademicRecords", "Student", "student", 1, "StudentAcademicRecord");
        checkEndName("studentAcademicRecords", "Session", "session", 1, "StudentAcademicRecord");
        checkEndName("studentCompetencies", "LearningObjective", "learningObjective", 1, "StudentCompetency");
        checkEndName("fooBarcies", "FooBarcyObjective", "fooBarcyObjective", 1, "FooBarcy");
        checkEndName("fooCompetencies", "FooSectionAssociation", "fooSectionAssociation", 1, "FooCompetency");
        checkEndName("studentDisciplineIncidentAssociations", "Student", "student", 1, "StudentDisciplineIncidentAssociation");
        checkEndName("fooBarBazAssociations", "BarBaz", "barBaz", 1, "FooBarBazAssociation");
        checkEndName("fooOrganizations", "FooOrganization", "parentFooAgency", 1, "FooOrganization");
        checkEndName("sections", "Assessment", "assessmentReferences", 1, "Section");
        checkEndName("sections", "CourseOffering", "courseOffering", 1, "Section");
        checkEndName("staffProgramAssociations", "Program", "programs", 1, "StaffProgramAssociation");
        checkEndName("staffProgramAssociations", "Staff", "staff", 1, "StaffProgramAssociation");
        checkEndName("courseOfferings", "Course", "course", 1, "CourseOffering");
        checkEndName("courseOfferings", "EducationOrganization", "school", 1, "CourseOffering");
        checkEndName("courseOfferings", "Session", "session", 1, "CourseOffering");
        checkEndName("fooDescriptors", "EducationOrganization", "educationOrganizations", 1, "FooDescriptor");
        checkEndName("attendances", "School", "school", 1, "Attendance");
        checkEndName("attendances", "Student", "student", 1, "Attendance");
        checkEndName("behaviorDescriptors", "EducationOrganization", "educationOrganizations", 1, "BehaviorDescriptor");
        checkEndName("objectiveAssessments", "LearningObjective", "learningObjectives", 1, "ObjectiveAssessment");
        checkEndName("disciplineIncidents", "Staff", "staff", 1, "DisciplineIncident");
        checkEndName("disciplineIncidents", "School", "school", 1, "DisciplineIncident");
        checkEndName("fooBarAssociations", "EducationOrganization", "educationOrganization", 1, "FooBarAssociation");
        checkEndName("courseTranscripts", "Course", "course", 1, "CourseTranscript");
        checkEndName("fooBarAssociations", "FooAcademicRecord", "fooAcademicRecord", 1, "FooBarAssociation");
        checkEndName("courseTranscripts", "Student", "student", 1, "CourseTranscript");
        checkEndName("cohorts", "Program", "programs", 1, "Cohort");
        checkEndName("cohorts", "EducationOrganization", "educationOrg", 1, "Cohort");
        checkEndName("studentGradebookEntries", "Student", "student", 1, "StudentGradebookEntry");
        checkEndName("studentGradebookEntries", "Section", "section", 1, "StudentGradebookEntry");
        checkEndName("fooStudentGradebookEntries", "GradebookEntry", "gradebookEntry", 1, "FooStudentGradebookEntry");
        checkEndName("courses", "School", "school", 1, "Course");
        checkEndName("fooEntries", "Bar", "bar", 1, "FooEntry");
        checkEndName("barFooFumBigBarAgencies", "BarAnything", "barFooFum", 2, "BigBarAgency");
        checkEndName("bigBarAgencyBigBarAgencies", "BarAnything", "bigBarAgency", 2, "BigBarAgency");
    }
    
    private void checkEndName(final String expected, final String sourceTypeName, final String sourceName,
            final int degeneracy, final String targetTypeName) {
        assertEquals(expected,
                Xsd2UmlHelper.makeAssociationEndName(sourceTypeName, sourceName, degeneracy, targetTypeName));
    }
}
