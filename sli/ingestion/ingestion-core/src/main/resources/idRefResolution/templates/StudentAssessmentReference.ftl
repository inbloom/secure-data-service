<StudentAssessmentIdentity>
    <AdministrationDate>${StudentObjectiveAssessment.StudentAssessmentReference.StudentAssessmentIdentity.AdministrationDate}</AdministrationDate>
    <StudentReference>
        <StudentIdentity>
            <StudentUniqueStateId>${StudentObjectiveAssessment.StudentAssessmentReference.StudentAssessmentIdentity.StudentReference.StudentIdentity.StudentUniqueStateId}</StudentUniqueStateId>
        </StudentIdentity>
    </StudentReference>
    <AssessmentReference>
        <AssessmentIdentity>
            <AssessmentIdentificationCode>
                IdentificationSystem="${StudentObjectiveAssessment.StudentAssessmentReference.StudentAssessmentIdentity.AssessmentReference.AssessmentIdentity.AssessmentIdentificationCode.@IdentificationSystem}"
                <ID>${StudentObjectiveAssessment.StudentAssessmentReference.StudentAssessmentIdentity.AssessmentReference.AssessmentIdentity.AssessmentIdentificationCode.ID}</ID>
            </AssessmentIdentificationCode>
        </AssessmentIdentity>
    </AssessmentReference>
</StudentAssessmentIdentity>

