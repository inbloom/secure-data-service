
<ProgramIdentity>
    <#if (Program.ProgramId[0])??>
    <ProgramId>${Program.ProgramId}</ProgramId >
    </#if>
    <#if (Program.ProgramType[0])??>
    <ProgramType>${Program.ProgramType}</ProgramType>
    </#if>
    <#if (Program.StateOrganizationId[0])??>
    <StateOrganizationId>${Program.StateOrganizationId}</StateOrganizationId>
    </#if>
    <#if (Program.EducationOrgIdentificationCode[0])??>
    <EducationOrgIdentificationCode
        <#if (Program.EducationOrgIdentificationCode.@IdentificationSystem[0])??>
         IdentificationSystem="${Program.EducationOrgIdentificationCode.@IdentificationSystem}"
         </#if>
         >
         <#if (Program.EducationOrgIdentificationCode.ID[0])??>
        <ID>${Program.EducationOrgIdentificationCode.ID}</ID>
        </#if>
    </EducationOrgIdentificationCode>
    </#if>
</ProgramIdentity>
