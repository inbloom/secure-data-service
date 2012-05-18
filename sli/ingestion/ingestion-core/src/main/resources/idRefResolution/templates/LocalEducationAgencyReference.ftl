
<EducationOrgIdentity>
    <#if (LocalEducationAgency.StateOrganizationId[0])?? >
    <StateOrganizationId>${LocalEducationAgency.StateOrganizationId}</StateOrganizationId>
    </#if>
    
    <#if (LocalEducationAgency.EducationOrgIdentificationCode[0])?? >
    <EducationOrgIdentificationCode
        <#if (LocalEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
        IdentificationSystem="${LocalEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (LocalEducationAgency.EducationOrgIdentificationCode.ID[0])??>
        <ID>${LocalEducationAgency.EducationOrgIdentificationCode.ID}</ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</EducationOrgIdentity>