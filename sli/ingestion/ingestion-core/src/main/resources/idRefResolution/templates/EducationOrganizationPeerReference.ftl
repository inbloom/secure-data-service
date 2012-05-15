<#if (School[0])??>
<#assign entityName=School>
<#elseif (StateEducationAgency[0])??>
<#assign entityName=StateEducationAgency>
<#elseif (LocalEducationAgency[0])??>
<#assign entityName=LocalEducationAgency>
</#if>


<EducationOrgIdentity>
    <#if (entityName.StateOrganizationId[0])?? >
    <StateOrganizationId>${entityName.StateOrganizationId}</StateOrganizationId>
    </#if>
    
    <#if (entityName.EducationOrgIdentificationCode[0])?? >
    <EducationOrgIdentificationCode
        <#if (entityName.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
        IdentificationSystem="${entityName.EducationOrgIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (entityName.EducationOrgIdentificationCode.ID[0])??>
        <ID>${entityName.EducationOrgIdentificationCode.ID}</ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</EducationOrgIdentity>
</EducationOrgIdentity>