
<EducationOrgIdentity>
    <#if (School.StateOrganizationId[0])?? >
    <StateOrganizationId>
        ${School.StateOrganizationId}
    </StateOrganizationId>
    </#if>
    
    <#if (School.EducationOrgIdentificationCode[0])?? >
    <EducationOrgIdentificationCode
        <#if (School.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
        IdentificationSystem="${School.EducationOrgIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (School.EducationOrgIdentificationCode.ID[0])??>
        <ID>
            ${School.EducationOrgIdentificationCode.ID}
        </ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</EducationOrgIdentity>

<EducationOrgIdentity>
    <#if (StateEducationAgency.StateOrganizationId[0])?? >
    <StateOrganizationId>
        ${StateEducationAgency.StateOrganizationId}
    </StateOrganizationId>
    </#if>
    
    <#if (StateEducationAgency.EducationOrgIdentificationCode[0])?? >
    <EducationOrgIdentificationCode
        <#if (StateEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
        IdentificationSystem="${StateEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (StateEducationAgency.EducationOrgIdentificationCode.ID[0])??>
        <ID>
            ${StateEducationAgency.EducationOrgIdentificationCode.ID}
        </ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</EducationOrgIdentity>

<EducationOrgIdentity>
    <#if (LocalEducationAgency.StateOrganizationId[0])?? >
    <StateOrganizationId>
        ${LocalEducationAgency.StateOrganizationId}
    </StateOrganizationId>
    </#if>
    
    <#if (LocalEducationAgency.EducationOrgIdentificationCode[0])?? >
    <EducationOrgIdentificationCode
        <#if (LocalEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
        IdentificationSystem="${LocalEducationAgency.EducationOrgIdentificationCode.@IdentificationSystem}"
        </#if>
        >
        <#if (LocalEducationAgency.EducationOrgIdentificationCode.ID[0])??>
        <ID>
            ${LocalEducationAgency.EducationOrgIdentificationCode.ID}
        </ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</EducationOrgIdentity>
