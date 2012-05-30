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
    
    <#list entityName.EducationOrgIdentificationCode as edorgId >
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
</EducationOrgIdentity>