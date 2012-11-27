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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;

import javax.xml.namespace.QName;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 9/12/12
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Xsd2UmlPluginForEdFiTest {
    private Xsd2UmlPluginForEdFi pluginForEdFi;
    private Xsd2UmlPluginHost host;
    private ClassType classType;
    private Attribute attribute;
    
    @Before
    public void setUp() throws Exception {
        pluginForEdFi = new Xsd2UmlPluginForEdFi();
        host = mock(Xsd2UmlPluginHost.class);
        classType = mock(ClassType.class);
        attribute = mock(Attribute.class);
    }
    
    @Test
    public void testDeclareTagDefinitions() throws Exception {
        assertEquals(Collections.emptyList(), pluginForEdFi.declareTagDefinitions(host));
    }
    
    @Test
    public void testGetAssociationEndTypeName() throws Exception {
        when(attribute.getName()).thenReturn("academicWeekReference");
        assertEquals("AcademicWeek", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("accountReference");
        assertEquals("Account", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("assignmentSchoolReference");
        assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("assessmentReference");
        assertEquals("Assessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("assessmentFamilyReference");
        assertEquals("AssessmentFamily", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("assessmentItemReference");
        assertEquals("AssessmentItem", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("calendarDateReference");
        assertEquals("CalendarDate", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("classPeriodReference");
        assertEquals("ClassPeriod", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("cohortReference");
        assertEquals("Cohort", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("courseReference");
        assertEquals("Course", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("courseOfferingReference");
        assertEquals("CourseOffering", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("disciplineIncidentReference");
        assertEquals("DisciplineIncident", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("diplomaReference");
        assertEquals("Diploma", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("educationContentReference");
        assertEquals("EducationContent", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("educationOrganizationNetworkReference");
        assertEquals("EducationOrganizationNetwork",
                pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("educationOrganizationPeerReference");
        assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("educationOrganizationReference");
        assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("educationOrgReference");
        assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("educationServiceCenterReference");
        assertEquals("EducationServiceCenter", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("feederSchoolReference");
        assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("gradeReference");
        assertEquals("Grade", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        
        when(attribute.getName()).thenReturn("gradebookEntryReference");
        assertEquals("GradebookEntry", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("gradingPeriodReference");
        assertEquals("GradingPeriod", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("graduationPlanReference");
        assertEquals("GraduationPlan", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("interventionReference");
        assertEquals("Intervention", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("interventionPrescriptionReference");
        assertEquals("InterventionPrescription", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("learningStandardReference");
        assertEquals("LearningStandard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("learningStandardItemReference");
        assertEquals("LearningStandard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("learningObjectiveReference");
        assertEquals("LearningObjective", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("localEducationAgencyReference");
        assertEquals("LocalEducationAgency", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("locationReference");
        assertEquals("Location", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("mandatingEducationOrganizationReference");
        assertEquals("EducationOrganization", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("meetingTimeReference");
        assertEquals("MeetingTime", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("objectiveAssessmentReference");
        assertEquals("ObjectiveAssessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("prerequisiteLearningStandardReference");
        assertEquals("LearningStandard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("programReference");
        assertEquals("Program", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("parentReference");
        assertEquals("Parent", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("receivingSchoolReference");
        assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("responsibilitySchoolReference");
        assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("reportCardReference");
        assertEquals("ReportCard", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("schoolReference");
        assertEquals("School", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("sectionReference");
        assertEquals("Section", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("sessionReference");
        assertEquals("Session", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("staffReference");
        assertEquals("Staff", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("stateEducationAgencyReference");
        assertEquals("StateEducationAgency", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentReference");
        assertEquals("Student", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentAcademicRecordReference");
        assertEquals("StudentAcademicRecord", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentAssessmentReference");
        assertEquals("StudentAssessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentCompetencyReference");
        assertEquals("StudentCompetency", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentCompetencyObjectiveReference");
        assertEquals("StudentCompetencyObjective", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentObjectiveAssessmentReference");
        assertEquals("StudentObjectiveAssessment", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("studentSectionAssociationReference");
        assertEquals("StudentSectionAssociation", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
        when(attribute.getName()).thenReturn("teacherReference");
        assertEquals("Teacher", pluginForEdFi.getAssociationEndTypeName(classType, attribute, host));
    }
    
    @Test
    public void testIsAssociationEnd() throws Exception {
        when(attribute.getName()).thenReturn("Reference");
        assertTrue(pluginForEdFi.isAssociationEnd(classType, attribute, host));
        when(attribute.getName()).thenReturn("Ref");
        assertFalse(pluginForEdFi.isAssociationEnd(classType, attribute, host));
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsAssociationEndNullException() throws Exception {
        pluginForEdFi.isAssociationEnd(classType, null, host);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testIsAssociationEndIllegalException() throws Exception {
        when(attribute.getName()).thenReturn(null);
        pluginForEdFi.isAssociationEnd(classType, attribute, host);
    }
    
    @Test
    public void testNameAssociation() throws Exception {
        AssociationEnd associationEnd = mock(AssociationEnd.class);
        when(
                host.nameAssociation(Mockito.any(AssociationEnd.class), Mockito.any(AssociationEnd.class),
                        Mockito.any(Xsd2UmlPluginHost.class))).thenReturn("test");
        assertEquals("test", pluginForEdFi.nameAssociation(associationEnd, associationEnd, host));
    }
    
    @Test
    public void testNameFromSchemaAttributeName() throws Exception {
        QName qName = new QName(SliMongoConstants.NAMESPACE_SLI, "test");
        assertEquals("test", pluginForEdFi.nameFromSchemaAttributeName(qName));
    }
    
    @Test
    public void testNameFromSchemaElementName() throws Exception {
        QName qName = new QName(SliMongoConstants.NAMESPACE_SLI, "test");
        assertEquals("test", pluginForEdFi.nameFromSchemaElementName(qName));
        
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testTagsFromAppInfo() throws Exception {
        pluginForEdFi.tagsFromAppInfo(null, host);
    }
}
