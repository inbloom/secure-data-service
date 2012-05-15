   <DisciplineIncidentIdentity>

<#if (DisciplineIncident.IncidentIdentifier[0])??>
    <IncidentIdentifier>
        ${DisciplineIncident.IncidentIdentifier}
    </IncidentIdentifier>
</#if>

<#if (DisciplineIncident.SchoolReference.EducationalOrgIdentity.StateOrganizationId[0])??>
    <StateOrganizationId>${DisciplineIncident.SchoolReference.EducationalOrgIdentity.StateOrganizationId}</StateOrganizationId>
</#if>

<#if (DisciplineIncident.SchoolReference.EducationalOrgIdentity.EducationOrgIdentificationCode[0])??>
    <EducationOrgIdentificationCode

    <#if (DisciplineIncident.SchoolReference.EducationalOrgIdentity.EducationOrgIdentificationCode.@IdentificationSystem[0])??>
    IdentificationSystem="${DisciplineIncident.SchoolReference.EducationalOrgIdentity.EducationOrgIdentificationCode.@IdentificationSystem}"
    </#if>
    >${DisciplineIncident.SchoolReference.EducationalOrgIdentity.EducationOrgIdentificationCode}</EducationOrgIdentificationCode>
</#if>

</DisciplineIncidentIdentity>
