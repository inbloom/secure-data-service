<StudentAcademicRecordIdentity>
    <StudentReference>
        <StudentIdentity>
            <StudentUniqueStateId>${StudentAcademicRecord.StudentReference.StudentIdentity.StudentUniqueStateId}</StudentUniqueStateId>
        </StudentIdentity>
    </StudentReference>
    <SessionReference>
        <SessionIdentity>
            <#if (StudentAcademicRecord.SessionReference.SessionIdentity.EducationalOrgReference.EducationalOrgIdentity.StateOrganizationId[0])??>
                <EducationalOrgReference>
                    <EducationalOrgIdentity>
                        <StateOrganizationId>${StudentAcademicRecord.SessionReference.SessionIdentity.EducationalOrgReference.EducationalOrgIdentity.StateOrganizationId}</StateOrganizationId>
                    </EducationalOrgIdentity>
                </EducationalOrgReference>
            </#if>
            
            <SessionName>${StudentAcademicRecord.SessionReference.SessionIdentity.SessionName}</SessionName>
        </SessionIdentity>
    </SessionReference>
</StudentAcademicRecordIdentity>
