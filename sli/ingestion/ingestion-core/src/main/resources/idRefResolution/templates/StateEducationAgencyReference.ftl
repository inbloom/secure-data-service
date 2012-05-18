
<EducationOrgIdentity>
    <#if (StateEducationAgency.StateOrganizationId[0])?? >
    <StateOrganizationId>${StateEducationAgency.StateOrganizationId}</StateOrganizationId>
    </#if>
    
    <#if (StateEducationAgency.EducationOrgIdentificationCode[0])?? >
    <EducationOrgIdentificationCode
        <#if (StateEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
        IdentificationSystem="${StateEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (StateEducationAgency.EducationOrgIdentificationCode.ID[0])??>
        <ID>${StateEducationAgency.EducationOrgIdentificationCode.ID}</ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</EducationOrgIdentity>