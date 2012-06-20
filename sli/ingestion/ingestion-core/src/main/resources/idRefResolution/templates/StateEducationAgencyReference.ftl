
<EducationalOrgIdentity>
    <#if (StateEducationAgency.StateOrganizationId[0])?? >
    <StateOrganizationId>${StateEducationAgency.StateOrganizationId}</StateOrganizationId>
    </#if>
    
    <#list StateEducationAgency.EducationOrgIdentificationCode as edorgId >
    <EducationOrgIdentificationCode
        <#if (edorgId.@IdentificationSystem[0])??> 
        IdentificationSystem="${edorgId.@IdentificationSystem}"
        </#if>
        >
        <#if (edorgId.ID[0])??>
        <ID>${edorgId.ID}</ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#list>
</EducationalOrgIdentity>