<StudentAssessmentIdentity>
    <AdministrationDate>${StudentAssessment.AdministrationDate}</AdministrationDate>
    <StudentReference>
        <StudentIdentity>
            <StudentUniqueStateId>${StudentAssessment.StudentReference.StudentIdentity.StudentUniqueStateId}</StudentUniqueStateId>
        </StudentIdentity>
    </StudentReference>
    <AssessmentReference>
        <AssessmentIdentity>
            <#if (StudentAssessment.AssessmentReference.AssessmentIdentity.AssessmentTitle[0])??>
                <AssessmentTitle>${StudentAssessment.AssessmentReference.AssessmentIdentity.AssessmentTitle}</AssessmentTitle>
            </#if>
            <#if (StudentAssessment.AssessmentReference.AssessmentIdentity.AcademicSubject[0])??>
                <AcademicSubject>${StudentAssessment.AssessmentReference.AssessmentIdentity.AcademicSubject}</AcademicSubject>
            </#if>
            <#if (StudentAssessment.AssessmentReference.AssessmentIdentity.GradeLevelAssessed[0])??>
                <GradeLevelAssessed>${StudentAssessment.AssessmentReference.AssessmentIdentity.GradeLevelAssessed}</GradeLevelAssessed>
            </#if>
            <#if (StudentAssessment.AssessmentReference.AssessmentIdentity.Version[0])??>
                <Version>${StudentAssessment.AssessmentReference.AssessmentIdentity.Version}</Version>
            </#if>
        </AssessmentIdentity>
    </AssessmentReference>
</StudentAssessmentIdentity>

