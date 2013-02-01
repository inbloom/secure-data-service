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

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

/**
 * Used to configure the reverse-engineering of UML from W3C XML Schemas.
 * 
 * The conversion of EdFi TitleCase attribute names to camelCase is configurable.
 * 
 */
public final class Xsd2UmlPluginForEdFi extends Xsd2UmlPluginDefault implements Xsd2UmlHostedPlugin {
    
    /**
     * A name ending with the string "Reference" is a Ed-Fi convention for a reference.
     */
    private static final boolean endsWithReference(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        return name.endsWith("Reference");
    }
    
    /**
     * Control the massaging of the W3C XML Schema. The less we do the better.
     */
    private static final boolean CAMEL_CASE_SCHEMA_NAMES = true;
    
    // Default constructor is required for reflection creation.
    public Xsd2UmlPluginForEdFi() {
        //No Op
    }
    
    @Override
    public List<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }
    
    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        if (attribute == null) {
            throw new IllegalArgumentException("attribute");
        }
        final String attributeName = attribute.getName();
        if (attributeName == null) {
            throw new IllegalStateException("attributeName");
        }
        if ("academicWeekReference".equalsIgnoreCase(attributeName)) {
            return "AcademicWeek";
        } else if ("accountReference".equalsIgnoreCase(attributeName)) {
            return "Account";
        } else if ("assignmentSchoolReference".equalsIgnoreCase(attributeName)) {
            return "School";
        } else if ("assessmentReference".equalsIgnoreCase(attributeName)) {
            return "Assessment";
        } else if ("assessmentFamilyReference".equalsIgnoreCase(attributeName)) {
            return "AssessmentFamily";
        } else if ("assessmentItemReference".equalsIgnoreCase(attributeName)) {
            return "AssessmentItem";
        } else if ("calendarDateReference".equalsIgnoreCase(attributeName)) {
            return "CalendarDate";
        } else if ("classPeriodReference".equalsIgnoreCase(attributeName)) {
            return "ClassPeriod";
        } else if ("cohortReference".equalsIgnoreCase(attributeName)) {
            return "Cohort";
        } else if ("courseReference".equalsIgnoreCase(attributeName)) {
            return "Course";
        } else if ("courseOfferingReference".equalsIgnoreCase(attributeName)) {
            return "CourseOffering";
        } else if ("disciplineIncidentReference".equalsIgnoreCase(attributeName)) {
            return "DisciplineIncident";
        } else if ("diplomaReference".equalsIgnoreCase(attributeName)) {
            return "Diploma";
        } else if ("educationContentReference".equalsIgnoreCase(attributeName)) {
            return "EducationContent";
        } else if ("educationOrganizationNetworkReference".equalsIgnoreCase(attributeName)) {
            return "EducationOrganizationNetwork";
        } else if ("educationOrganizationPeerReference".equalsIgnoreCase(attributeName)) {
            return "EducationOrganization";
        } else if ("educationOrganizationReference".equalsIgnoreCase(attributeName)) {
            return "EducationOrganization";
        } else if ("educationOrgReference".equalsIgnoreCase(attributeName)) {
            return "EducationOrganization";
        } else if ("educationServiceCenterReference".equalsIgnoreCase(attributeName)) {
            return "EducationServiceCenter";
        } else if ("feederSchoolReference".equalsIgnoreCase(attributeName)) {
            return "School";
        } else if ("gradeReference".equalsIgnoreCase(attributeName)) {
            return "Grade";
        } else if ("gradebookEntryReference".equalsIgnoreCase(attributeName)) {
            return "GradebookEntry";
        } else if ("gradingPeriodReference".equalsIgnoreCase(attributeName)) {
            return "GradingPeriod";
        } else if ("graduationPlanReference".equalsIgnoreCase(attributeName)) {
            return "GraduationPlan";
        } else if ("interventionReference".equalsIgnoreCase(attributeName)) {
            return "Intervention";
        } else if ("interventionPrescriptionReference".equalsIgnoreCase(attributeName)) {
            return "InterventionPrescription";
        } else if ("learningStandardReference".equalsIgnoreCase(attributeName)) {
            return "LearningStandard";
        } else if ("learningStandardItemReference".equalsIgnoreCase(attributeName)) {
            return "LearningStandard";
        } else if ("learningObjectiveReference".equalsIgnoreCase(attributeName)) {
            return "LearningObjective";
        } else if ("localEducationAgencyReference".equalsIgnoreCase(attributeName)) {
            return "LocalEducationAgency";
        } else if ("locationReference".equalsIgnoreCase(attributeName)) {
            return "Location";
        } else if ("mandatingEducationOrganizationReference".equalsIgnoreCase(attributeName)) {
            return "EducationOrganization";
        } else if ("meetingTimeReference".equalsIgnoreCase(attributeName)) {
            return "MeetingTime";
        } else if ("objectiveAssessmentReference".equalsIgnoreCase(attributeName)) {
            return "ObjectiveAssessment";
        } else if ("prerequisiteLearningStandardReference".equalsIgnoreCase(attributeName)) {
            return "LearningStandard";
        } else if ("programReference".equalsIgnoreCase(attributeName)) {
            return "Program";
        } else if ("parentReference".equalsIgnoreCase(attributeName)) {
            return "Parent";
        } else if ("receivingSchoolReference".equalsIgnoreCase(attributeName)) {
            return "School";
        } else if ("responsibilitySchoolReference".equalsIgnoreCase(attributeName)) {
            return "School";
        } else if ("reportCardReference".equalsIgnoreCase(attributeName)) {
            return "ReportCard";
        } else if ("schoolReference".equalsIgnoreCase(attributeName)) {
            return "School";
        } else if ("sectionReference".equalsIgnoreCase(attributeName)) {
            return "Section";
        } else if ("sessionReference".equalsIgnoreCase(attributeName)) {
            return "Session";
        } else if ("staffReference".equalsIgnoreCase(attributeName)) {
            return "Staff";
        } else if ("stateEducationAgencyReference".equalsIgnoreCase(attributeName)) {
            return "StateEducationAgency";
        } else if ("studentReference".equalsIgnoreCase(attributeName)) {
            return "Student";
        } else if ("studentAcademicRecordReference".equalsIgnoreCase(attributeName)) {
            return "StudentAcademicRecord";
        } else if ("studentAssessmentReference".equalsIgnoreCase(attributeName)) {
            return "StudentAssessment";
        } else if ("studentCompetencyReference".equalsIgnoreCase(attributeName)) {
            return "StudentCompetency";
        } else if ("studentCompetencyObjectiveReference".equalsIgnoreCase(attributeName)) {
            return "StudentCompetencyObjective";
        } else if ("studentObjectiveAssessmentReference".equalsIgnoreCase(attributeName)) {
            return "StudentObjectiveAssessment";
        } else if ("studentSectionAssociationReference".equalsIgnoreCase(attributeName)) {
            return "StudentSectionAssociation";
        } else if ("teacherReference".equalsIgnoreCase(attributeName)) {
            return "Teacher";
        } else {
            throw new UnsupportedOperationException(classType.getName() + "." + attribute.getName());
        }
    }
    
    @Override
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        if (attribute == null) {
            throw new IllegalArgumentException("attribute");
        }
        final String attributeName = attribute.getName();
        if (attributeName == null) {
            throw new IllegalStateException("attributeName");
        }
        
        return endsWithReference(attributeName);
    }
    
    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlHostedPlugin host) {
        return host.nameAssociation(lhs, rhs, host);
    }
    
    @Override
    public String nameFromSchemaAttributeName(final QName name) {
        if (CAMEL_CASE_SCHEMA_NAMES) {
            return Xsd2UmlHelper.camelCase(name.getLocalPart());
        } else {
            return super.nameFromSchemaAttributeName(name);
        }
    }
    
    @Override
    public String nameFromSchemaElementName(final QName name) {
        if (CAMEL_CASE_SCHEMA_NAMES) {
            return Xsd2UmlHelper.camelCase(name.getLocalPart());
        } else {
            return super.nameFromSchemaElementName(name);
        }
    }
    
    @Override
    public List<TaggedValue> tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        throw new UnsupportedOperationException();
    }
}
