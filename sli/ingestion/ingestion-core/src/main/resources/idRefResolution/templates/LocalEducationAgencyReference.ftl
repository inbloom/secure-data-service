
<EducationOrgIdentity>
    <#if (LocalEducationAgency.StateOrganizationId[0])?? >
    <StateOrganizationId>${LocalEducationAgency.StateOrganizationId}</StateOrganizationId>
    </#if>
    
    <#list LocalEducationAgency.EducationOrgIdentificationCode as edorgId >
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
</EducationOrgIdentity>