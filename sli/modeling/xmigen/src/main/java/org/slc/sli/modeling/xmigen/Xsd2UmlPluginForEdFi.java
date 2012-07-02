package org.slc.sli.modeling.xmigen;

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
public final class Xsd2UmlPluginForEdFi implements Xsd2UmlPlugin {

    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }

    /**
     * A name ending with the string "Reference" is a Ed-Fi convention for a reference.
     */
    private static final boolean endsWithReference(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return name.endsWith("Reference");
    }

    // Default constructor is required for reflection creation.
    public Xsd2UmlPluginForEdFi() {
    }

    @Override
    public List<TagDefinition> declareTagDefinitions(final Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }

    @Override
    public String getAssociationEndTypeName(final ClassType classType, final Attribute attribute,
            final Xsd2UmlPluginHost host) {
        if (attribute == null) {
            throw new NullPointerException("attribute");
        }
        final String attributeName = attribute.getName();
        if (attributeName == null) {
            throw new IllegalStateException("attributeName");
        }
        if ("academicWeekReference".equals(attributeName)) {
            return "AcademicWeek";
        } else if ("accountReference".equals(attributeName)) {
            return "Account";
        } else if ("assignmentSchoolReference".equals(attributeName)) {
            return "School";
        } else if ("assessmentReference".equals(attributeName)) {
            return "Assessment";
        } else if ("assessmentFamilyReference".equals(attributeName)) {
            return "AssessmentFamily";
        } else if ("assessmentItemReference".equals(attributeName)) {
            return "AssessmentItem";
        } else if ("calendarDateReference".equals(attributeName)) {
            return "CalendarDate";
        } else if ("classPeriodReference".equals(attributeName)) {
            return "ClassPeriod";
        } else if ("cohortReference".equals(attributeName)) {
            return "Cohort";
        } else if ("courseReference".equals(attributeName)) {
            return "Course";
        } else if ("courseOfferingReference".equals(attributeName)) {
            return "CourseOffering";
        } else if ("disciplineIncidentReference".equals(attributeName)) {
            return "DisciplineIncident";
        } else if ("diplomaReference".equals(attributeName)) {
            return "Diploma";
        } else if ("educationContentReference".equals(attributeName)) {
            return "EducationContent";
        } else if ("educationOrganizationReference".equals(attributeName)) {
            return "EducationOrganization";
        } else if ("educationOrganizationPeerReference".equals(attributeName)) {
            return "EducationOrganization";
        } else if ("educationOrgReference".equals(attributeName)) {
            return "EducationOrganization";
        } else if ("educationServiceCenterReference".equals(attributeName)) {
            return "EducationServiceCenter";
        } else if ("feederSchoolReference".equals(attributeName)) {
            return "School";
        } else if ("gradeReference".equals(attributeName)) {
            return "Grade";
        } else if ("gradebookEntryReference".equals(attributeName)) {
            return "GradebookEntry";
        } else if ("gradingPeriodReference".equals(attributeName)) {
            return "GradingPeriod";
        } else if ("graduationPlanReference".equals(attributeName)) {
            return "GraduationPlan";
        } else if ("interventionReference".equals(attributeName)) {
            return "Intervention";
        } else if ("interventionPrescriptionReference".equals(attributeName)) {
            return "InterventionPrescription";
        } else if ("learningStandardReference".equals(attributeName)) {
            return "LearningStandard";
        } else if ("learningObjectiveReference".equals(attributeName)) {
            return "LearningObjective";
        } else if ("localEducationAgencyReference".equals(attributeName)) {
            return "LocalEducationAgency";
        } else if ("locationReference".equals(attributeName)) {
            return "Location";
        } else if ("meetingTimeReference".equals(attributeName)) {
            return "MeetingTime";
        } else if ("objectiveAssessmentReference".equals(attributeName)) {
            return "ObjectiveAssessment";
        } else if ("programReference".equals(attributeName)) {
            return "Program";
        } else if ("parentReference".equals(attributeName)) {
            return "Parent";
        } else if ("receivingSchoolReference".equals(attributeName)) {
            return "School";
        } else if ("responsibilitySchoolReference".equals(attributeName)) {
            return "School";
        } else if ("reportCardReference".equals(attributeName)) {
            return "ReportCard";
        } else if ("schoolReference".equals(attributeName)) {
            return "School";
        } else if ("sectionReference".equals(attributeName)) {
            return "Section";
        } else if ("sessionReference".equals(attributeName)) {
            return "Session";
        } else if ("staffReference".equals(attributeName)) {
            return "Staff";
        } else if ("stateEducationAgencyReference".equals(attributeName)) {
            return "StateEducationAgency";
        } else if ("studentReference".equals(attributeName)) {
            return "Student";
        } else if ("studentAcademicRecordReference".equals(attributeName)) {
            return "StudentAcademicRecord";
        } else if ("studentAssessmentReference".equals(attributeName)) {
            return "StudentAssessment";
        } else if ("studentCompetencyReference".equals(attributeName)) {
            return "StudentCompetency";
        } else if ("studentCompetencyObjectiveReference".equals(attributeName)) {
            return "StudentCompetencyObjective";
        } else if ("studentObjectiveAssessmentReference".equals(attributeName)) {
            return "StudentObjectiveAssessment";
        } else if ("studentSectionAssociationReference".equals(attributeName)) {
            return "StudentSectionAssociation";
        } else if ("teacherReference".equals(attributeName)) {
            return "Teacher";
        } else {
            throw new UnsupportedOperationException(classType.getName() + "." + attribute.getName());
        }
    }

    @Override
    public boolean isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) {
        if (attribute == null) {
            throw new NullPointerException("attribute");
        }
        final String attributeName = attribute.getName();
        if (attributeName == null) {
            throw new IllegalStateException("attributeName");
        }
        if (endsWithReference(attributeName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) {
        return host.nameAssociation(lhs, rhs, host);
    }

    @Override
    public String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        return complexType.getLocalPart().concat(" extends ").concat(base.getLocalPart());
    }

    @Override
    public String nameFromSchemaAttributeName(final QName name) {
        return camelCase(name.getLocalPart());
    }

    @Override
    public String nameFromSchemaElementName(final QName name) {
        return camelCase(name.getLocalPart());
    }

    @Override
    public String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        return simpleType.getLocalPart().concat(" restricts ").concat(base.getLocalPart());
    }

    @Override
    public String nameFromTypeName(final QName name) {
        return name.getLocalPart();
    }

    @Override
    public List<TaggedValue> tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) {
        throw new UnsupportedOperationException();
    }
}
