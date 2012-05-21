
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
    <#list Program.EducationOrgIdentificationCode as edorgId>
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
</ProgramIdentity>
