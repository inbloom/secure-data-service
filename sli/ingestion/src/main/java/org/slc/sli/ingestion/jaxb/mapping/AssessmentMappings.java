package org.slc.sli.ingestion.jaxb.mapping;

/**
 * ED-FI -> SLI mappings for Assessment entity
 *
 * @author dduran
 *
 */
public class AssessmentMappings {

    public static org.slc.sli.ingestion.jaxb.domain.sli.Assessment mapAssessment(
            org.slc.sli.ingestion.jaxb.domain.edfi.Assessment edFiAssessment) {
        org.slc.sli.ingestion.jaxb.domain.sli.Assessment sliAssessment = new org.slc.sli.ingestion.jaxb.domain.sli.Assessment();

        // assessmentTitle
        sliAssessment.setAssessmentTitle(edFiAssessment.getAssessmentTitle());

        // assessmentFamilyHierarchyName
        // sliAssessment.setAssessmentFamilyHierarchyName(edFiAssessment.getAssessmentFamilyHierarchyName());

        // assessmentIdentificationCode
        sliAssessment.getAssessmentIdentificationCode().addAll(
                mapAssessmentIdentificationCodeList(edFiAssessment.getAssessmentIdentificationCode()));

        // assessmentCategory
        sliAssessment.setAssessmentCategory(org.slc.sli.ingestion.jaxb.domain.sli.AssessmentCategoryType
                .fromValue(edFiAssessment.getAssessmentCategory().value()));

        // academicSubject
        sliAssessment.setAcademicSubject(org.slc.sli.ingestion.jaxb.domain.sli.AcademicSubjectType
                .fromValue(edFiAssessment.getAcademicSubject().value()));

        // gradeLevelAssessed
        sliAssessment.setGradeLevelAssessed(org.slc.sli.ingestion.jaxb.domain.sli.GradeLevelType
                .fromValue(edFiAssessment.getGradeLevelAssessed().value()));

        // lowestGradeLevelAssessed
        sliAssessment.setLowestGradeLevelAssessed(org.slc.sli.ingestion.jaxb.domain.sli.GradeLevelType
                .fromValue(edFiAssessment.getLowestGradeLevelAssessed().value()));

        // assessmentPerformanceLevel
        sliAssessment.getAssessmentPerformanceLevel().addAll(
                mapAssessmentPerformanceLevelList(edFiAssessment.getAssessmentPerformanceLevel()));

        // contentStandard
        sliAssessment.setContentStandard(org.slc.sli.ingestion.jaxb.domain.sli.ContentStandardType
                .fromValue(edFiAssessment.getContentStandard().value()));

        // assessmentForm
        sliAssessment.setAssessmentForm(edFiAssessment.getAssessmentForm());

        // version
        sliAssessment.setVersion(edFiAssessment.getVersion());

        // revisionDate
        sliAssessment.setRevisionDate(edFiAssessment.getRevisionDate());

        // maxRawScore
        sliAssessment.setMaxRawScore(edFiAssessment.getMaxRawScore());

        // minRawScore
        // sliAssessment.setMinRawScore(edFiAssessment.getMinRawScore());

        // nomenclature
        sliAssessment.setNomenclature(edFiAssessment.getNomenclature());

        // assessmentPeriodDescriptor
        // sliAssessment.setAssessmentPeriodDescriptor(mapAssessmentPeriodDescriptor(edFiAssessment.getAssessmentPeriodDescriptor()));

        // objectiveAssessment
        // sliAssessment.getObjectiveAssessment().addAll(mapObjectiveAssessmentList(edFiAssessment.getObjectiveAssessment()));
        return sliAssessment;
    }

    public static java.util.Collection<org.slc.sli.ingestion.jaxb.domain.sli.AssessmentIdentificationCode> mapAssessmentIdentificationCodeList(
            java.util.List<org.slc.sli.ingestion.jaxb.domain.edfi.AssessmentIdentificationCode> edFiAssessmentIdentificationCodeList) {

        java.util.Collection<org.slc.sli.ingestion.jaxb.domain.sli.AssessmentIdentificationCode> sliAssessmentIdentificationCodeList = new java.util.ArrayList<org.slc.sli.ingestion.jaxb.domain.sli.AssessmentIdentificationCode>(
                edFiAssessmentIdentificationCodeList.size());

        for (org.slc.sli.ingestion.jaxb.domain.edfi.AssessmentIdentificationCode edFiAssessmentIdentificationCode : edFiAssessmentIdentificationCodeList) {
            org.slc.sli.ingestion.jaxb.domain.sli.AssessmentIdentificationCode sliAssessmentIdentificationCode = new org.slc.sli.ingestion.jaxb.domain.sli.AssessmentIdentificationCode();

            // id
            sliAssessmentIdentificationCode.setID(edFiAssessmentIdentificationCode.getID());

            // identificationSystem
            sliAssessmentIdentificationCode
                    .setIdentificationSystem(org.slc.sli.ingestion.jaxb.domain.sli.AssessmentIdentificationSystemType
                            .fromValue(edFiAssessmentIdentificationCode.getIdentificationSystem().value()));

            // assigningOrganizationCode
            sliAssessmentIdentificationCode.setAssigningOrganizationCode(edFiAssessmentIdentificationCode
                    .getAssigningOrganizationCode());

            sliAssessmentIdentificationCodeList.add(sliAssessmentIdentificationCode);
        }
        return sliAssessmentIdentificationCodeList;
    }

    public static java.util.Collection<org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPerformanceLevel> mapAssessmentPerformanceLevelList(
            java.util.List<org.slc.sli.ingestion.jaxb.domain.edfi.AssessmentPerformanceLevel> edFiAssessmentPerformanceLevelList) {

        java.util.Collection<org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPerformanceLevel> sliAssessmentPerformanceLevelList = new java.util.ArrayList<org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPerformanceLevel>(
                edFiAssessmentPerformanceLevelList.size());

        for (org.slc.sli.ingestion.jaxb.domain.edfi.AssessmentPerformanceLevel edFiAssessmentPerformanceLevel : edFiAssessmentPerformanceLevelList) {
            org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPerformanceLevel sliAssessmentPerformanceLevel = new org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPerformanceLevel();

            // performanceLevelDescriptor
            /*sliAssessmentPerformanceLevel
                    .setPerformanceLevelDescriptor(mapPerformanceLevelDescriptor(edFiAssessmentPerformanceLevel
                            .getPerformanceLevelDescriptor()));*/

            // assessmentReportingMethod
            sliAssessmentPerformanceLevel
                    .setAssessmentReportingMethod(org.slc.sli.ingestion.jaxb.domain.sli.AssessmentReportingMethodType
                            .fromValue(edFiAssessmentPerformanceLevel.getAssessmentReportingMethod().value()));

            // minimumScore
            sliAssessmentPerformanceLevel.setMinimumScore(edFiAssessmentPerformanceLevel.getMinimumScore());

            // maximumScore
            sliAssessmentPerformanceLevel.setMaximumScore(edFiAssessmentPerformanceLevel.getMaximumScore());

            sliAssessmentPerformanceLevelList.add(sliAssessmentPerformanceLevel);
        }
        return sliAssessmentPerformanceLevelList;
    }

    public static org.slc.sli.ingestion.jaxb.domain.sli.PerformanceLevelDescriptor mapPerformanceLevelDescriptor(
            org.slc.sli.ingestion.jaxb.domain.edfi.PerformanceLevelDescriptor edFiPerformanceLevelDescriptor) {
        org.slc.sli.ingestion.jaxb.domain.sli.PerformanceLevelDescriptor sliPerformanceLevelDescriptor = new org.slc.sli.ingestion.jaxb.domain.sli.PerformanceLevelDescriptor();

        // codeValue
        sliPerformanceLevelDescriptor.setCodeValue(edFiPerformanceLevelDescriptor.getCodeValue());

        // description
        sliPerformanceLevelDescriptor.setDescription(edFiPerformanceLevelDescriptor.getDescription());

        // performanceBaseConversion
        sliPerformanceLevelDescriptor
                .setPerformanceBaseConversion(org.slc.sli.ingestion.jaxb.domain.sli.PerformanceBaseType
                        .fromValue(edFiPerformanceLevelDescriptor.getPerformanceBaseConversion().value()));
        return sliPerformanceLevelDescriptor;
    }

    public static org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPeriodDescriptor mapAssessmentPeriodDescriptor(
            org.slc.sli.ingestion.jaxb.domain.edfi.AssessmentPeriodDescriptor edFiAssessmentPeriodDescriptor) {
        org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPeriodDescriptor sliAssessmentPeriodDescriptor = new org.slc.sli.ingestion.jaxb.domain.sli.AssessmentPeriodDescriptor();

        // codeValue
        sliAssessmentPeriodDescriptor.setCodeValue(edFiAssessmentPeriodDescriptor.getCodeValue());

        // description
        sliAssessmentPeriodDescriptor.setDescription(edFiAssessmentPeriodDescriptor.getDescription());

        // shortDescription
        sliAssessmentPeriodDescriptor.setShortDescription(edFiAssessmentPeriodDescriptor.getShortDescription());

        // beginDate
        sliAssessmentPeriodDescriptor.setBeginDate(edFiAssessmentPeriodDescriptor.getBeginDate());

        // endDate
        sliAssessmentPeriodDescriptor.setEndDate(edFiAssessmentPeriodDescriptor.getEndDate());
        return sliAssessmentPeriodDescriptor;
    }

    public static java.util.Collection<org.slc.sli.ingestion.jaxb.domain.sli.ObjectiveAssessment> mapObjectiveAssessmentList(
            java.util.List<org.slc.sli.ingestion.jaxb.domain.edfi.ObjectiveAssessment> edFiObjectiveAssessmentList) {

        java.util.Collection<org.slc.sli.ingestion.jaxb.domain.sli.ObjectiveAssessment> sliObjectiveAssessmentList = new java.util.ArrayList<org.slc.sli.ingestion.jaxb.domain.sli.ObjectiveAssessment>(
                edFiObjectiveAssessmentList.size());

        for (org.slc.sli.ingestion.jaxb.domain.edfi.ObjectiveAssessment edFiObjectiveAssessment : edFiObjectiveAssessmentList) {
            org.slc.sli.ingestion.jaxb.domain.sli.ObjectiveAssessment sliObjectiveAssessment = new org.slc.sli.ingestion.jaxb.domain.sli.ObjectiveAssessment();

            // identificationCode
            sliObjectiveAssessment.setIdentificationCode(edFiObjectiveAssessment.getIdentificationCode());

            // maxRawScore
            sliObjectiveAssessment.setMaxRawScore(edFiObjectiveAssessment.getMaxRawScore());

            // assessmentPerformanceLevel
            sliObjectiveAssessment.getAssessmentPerformanceLevel().addAll(
                    mapAssessmentPerformanceLevelList(edFiObjectiveAssessment.getAssessmentPerformanceLevel()));

            // percentOfAssessment
            sliObjectiveAssessment.setPercentOfAssessment(edFiObjectiveAssessment.getPercentOfAssessment());

            // nomenclature
            sliObjectiveAssessment.setNomenclature(edFiObjectiveAssessment.getNomenclature());

            sliObjectiveAssessmentList.add(sliObjectiveAssessment);
        }
        return sliObjectiveAssessmentList;
    }

}
