<StudentAcademicRecordIdentity>
    <StudentReference>
        <StudentIdentity>
            <StudentUniqueStateId>${StudentAcademicRecord.StudentReference.StudentIdentity.StudentUniqueStateId}</StudentUniqueStateId>
        </StudentIdentity>
    </StudentReference>
    <SessionReference>
        <SessionIdentity>
            <#if (StudentAcademicRecord.SessionReference.SessionIdentity.StateOrganizationId[0])??>
                <EducationalOrgReference>
                    <EducationalOrgIdentity>
                        <StateOrganizationId>${StudentAcademicRecord.SessionReference.SessionIdentity.StateOrganizationId}</StateOrganizationId>
                    </EducationalOrgIdentity>
                </EducationalOrgReference>
            </#if>
            
            <SessionName>${StudentAcademicRecord.SessionReference.SessionIdentity.SessionName}</SessionName>
        </SessionIdentity>
    </SessionReference>
</StudentAcademicRecordIdentity>
