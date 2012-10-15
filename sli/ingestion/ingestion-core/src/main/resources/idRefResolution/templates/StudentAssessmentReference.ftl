<StudentAssessmentIdentity>
    <AdministrationDate>${StudentAssessment.AdministrationDate}</AdministrationDate>
    <StudentReference>
        <StudentIdentity>
            <StudentUniqueStateId>${StudentAssessment.StudentReference.StudentIdentity.StudentUniqueStateId}</StudentUniqueStateId>
        </StudentIdentity>
    </StudentReference>
    <AssessmentReference>
        <AssessmentIdentity>
            <AssessmentIdentificationCode>
                IdentificationSystem="${StudentAssessment.AssessmentReference.AssessmentIdentity.AssessmentIdentificationCode.@IdentificationSystem}"
                <ID>${StudentAssessment.AssessmentReference.AssessmentIdentity.AssessmentIdentificationCode.ID}</ID>
            </AssessmentIdentificationCode>
        </AssessmentIdentity>
    </AssessmentReference>
</StudentAssessmentIdentity>

