/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.modeling.tools.xmigen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;

import javax.xml.namespace.QName;
import java.util.Collections;

public class Xsd2UmlPluginForEdFiTest {
    private Xsd2UmlPluginForEdFi pluginForEdFi;
    private Xsd2UmlPluginHost host;
    private Xsd2UmlHostedPlugin plugin;
    private ClassType classType;
    private Attribute attribute;
    
    @Before
    public void setUp() throws Exception {
        pluginForEdFi = new Xsd2UmlPluginForEdFi();
        host = Mockito.mock(Xsd2UmlPluginHost.class);
        plugin = Mockito.mock(Xsd2UmlHostedPlugin.class);
        classType = Mockito.mock(ClassType.class);
        attribute = Mockito.mock(Attribute.class);
    }
    
    @Test
    public void testDeclareTagDefinitions() throws Exception {
        Assert.assertEquals(Collections.emptyList(), pluginForEdFi.declareTagDefinitions(host));
    }
    
    @Test
    public void testGetAssociationEndTypeName() throws Exception {
        Mockito.when(attribute.getName()).thenReturn("academicWeekReference");
        Assert.assertEquals("AcademicWeek", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("accountReference");
        Assert.assertEquals("Account", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("assignmentSchoolReference");
        Assert.assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("assessmentReference");
        Assert.assertEquals("Assessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("assessmentFamilyReference");
        Assert.assertEquals("AssessmentFamily", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("assessmentItemReference");
        Assert.assertEquals("AssessmentItem", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("calendarDateReference");
        Assert.assertEquals("CalendarDate", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("classPeriodReference");
        Assert.assertEquals("ClassPeriod", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("cohortReference");
        Assert.assertEquals("Cohort", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("courseReference");
        Assert.assertEquals("Course", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("courseOfferingReference");
        Assert.assertEquals("CourseOffering", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("disciplineIncidentReference");
        Assert.assertEquals("DisciplineIncident", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("diplomaReference");
        Assert.assertEquals("Diploma", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("educationContentReference");
        Assert.assertEquals("EducationContent", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("educationOrganizationNetworkReference");
        Assert.assertEquals("EducationOrganizationNetwork",
                pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("educationOrganizationPeerReference");
        Assert.assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("educationOrganizationReference");
        Assert.assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("educationOrgReference");
        Assert.assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("educationServiceCenterReference");
        Assert.assertEquals("EducationServiceCenter", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("feederSchoolReference");
        Assert.assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("gradeReference");
        Assert.assertEquals("Grade", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));

        Mockito.when(attribute.getName()).thenReturn("gradebookEntryReference");
        Assert.assertEquals("GradebookEntry", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("gradingPeriodReference");
        Assert.assertEquals("GradingPeriod", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("graduationPlanReference");
        Assert.assertEquals("GraduationPlan", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("interventionReference");
        Assert.assertEquals("Intervention", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("interventionPrescriptionReference");
        Assert.assertEquals("InterventionPrescription", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("learningStandardReference");
        Assert.assertEquals("LearningStandard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("learningStandardItemReference");
        Assert.assertEquals("LearningStandard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("learningObjectiveReference");
        Assert.assertEquals("LearningObjective", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("localEducationAgencyReference");
        Assert.assertEquals("LocalEducationAgency", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("locationReference");
        Assert.assertEquals("Location", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("mandatingEducationOrganizationReference");
        Assert.assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("meetingTimeReference");
        Assert.assertEquals("MeetingTime", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("objectiveAssessmentReference");
        Assert.assertEquals("ObjectiveAssessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("prerequisiteLearningStandardReference");
        Assert.assertEquals("LearningStandard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("programReference");
        Assert.assertEquals("Program", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("parentReference");
        Assert.assertEquals("Parent", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("receivingSchoolReference");
        Assert.assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("responsibilitySchoolReference");
        Assert.assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("reportCardReference");
        Assert.assertEquals("ReportCard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("schoolReference");
        Assert.assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("sectionReference");
        Assert.assertEquals("Section", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("sessionReference");
        Assert.assertEquals("Session", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("staffReference");
        Assert.assertEquals("Staff", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("stateEducationAgencyReference");
        Assert.assertEquals("StateEducationAgency", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentReference");
        Assert.assertEquals("Student", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentAcademicRecordReference");
        Assert.assertEquals("StudentAcademicRecord", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentAssessmentReference");
        Assert.assertEquals("StudentAssessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentCompetencyReference");
        Assert.assertEquals("StudentCompetency", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentCompetencyObjectiveReference");
        Assert.assertEquals("StudentCompetencyObjective", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentObjectiveAssessmentReference");
        Assert.assertEquals("StudentObjectiveAssessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("studentSectionAssociationReference");
        Assert.assertEquals("StudentSectionAssociation", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("teacherReference");
        Assert.assertEquals("Teacher", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
    }
    
    @Test
    public void testIsAssociationEnd() throws Exception {
        Mockito.when(attribute.getName()).thenReturn("Reference");
        Assert.assertTrue(pluginForEdFi.isAssociationEnd(classType, attribute, host));
        Mockito.when(attribute.getName()).thenReturn("Ref");
        Assert.assertFalse(pluginForEdFi.isAssociationEnd(classType, attribute, host));
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsAssociationEndNullException() throws Exception {
        pluginForEdFi.isAssociationEnd(classType, null, host);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testIsAssociationEndIllegalException() throws Exception {
        Mockito.when(attribute.getName()).thenReturn(null);
        pluginForEdFi.isAssociationEnd(classType, attribute, host);
    }
    
    @Test
    public void testNameAssociation() throws Exception {
        AssociationEnd associationEnd = Mockito.mock(AssociationEnd.class);
        Mockito.when(
                plugin.nameAssociation(Mockito.any(AssociationEnd.class), Mockito.any(AssociationEnd.class),
                        Mockito.any(Xsd2UmlHostedPlugin.class))).thenReturn("test");
        Assert.assertEquals("test", pluginForEdFi.nameAssociation(associationEnd, associationEnd, plugin));
    }
    
    @Test
    public void testNameFromSchemaAttributeName() throws Exception {
        QName qName = new QName(SliMongoConstants.NAMESPACE_SLI, "test");
        Assert.assertEquals("test", pluginForEdFi.nameFromSchemaAttributeName(qName));
    }
    
    @Test
    public void testNameFromSchemaElementName() throws Exception {
        QName qName = new QName(SliMongoConstants.NAMESPACE_SLI, "test");
        Assert.assertEquals("test", pluginForEdFi.nameFromSchemaElementName(qName));
        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testTagsFromAppInfo() throws Exception {
        pluginForEdFi.tagsFromAppInfo(null, host);
    }
}
