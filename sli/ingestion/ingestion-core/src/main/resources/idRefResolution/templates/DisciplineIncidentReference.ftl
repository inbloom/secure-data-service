   <DisciplineIncidentIdentity>

<#if (DisciplineIncident.IncidentIdentifier[0])??>
    <IncidentIdentifier>
        ${DisciplineIncident.IncidentIdentifier}
    </IncidentIdentifier>
</#if>

<#if (DisciplineIncident.SchoolReference.EducationalOrgIdentity.StateOrganizationId[0])??>
    <StateOrganizationId>${DisciplineIncident.SchoolReference.EducationalOrgIdentity.StateOrganizationId}</StateOrganizationId>
</#if>

<#list isciplineIncident.SchoolReference.EducationalOrgIdentity.EducationOrgIdentificationCode as edorgId>
    <EducationOrgIdentificationCode

    <#if (edorgId.@IdentificationSystem[0])??>
    IdentificationSystem="${edorgId.@IdentificationSystem}"
    </#if>
    >
    <ID>${edorgId.ID}</ID>
    </EducationOrgIdentificationCode>
</#list>

</DisciplineIncidentIdentity>
