<GradingPeriodReference>
    <GradingPeriodIdentity>

        <#if (GradingPeriod.GradingPeriodIdentity.GradingPeriod[0])??>
            <GradingPeriod>${GradingPeriod.GradingPeriodIdentity.GradingPeriod}</GradingPeriod>
        </#if>

        <#if (GradingPeriod.GradingPeriodIdentity.SchoolYear[0])??>
            <SchoolYear>${GradingPeriod.GradingPeriodIdentity.SchoolYear}</SchoolYear>
        </#if >

        <#if (GradingPeriod.GradingPeriodIdentity.EducationalOrgIdentity[0])??>
            <#list GradingPeriod.GradingPeriodIdentity.EducationalOrgIdentity as edorgIdentity >
                <StateOrganizationId>${edorgIdentity.StateOrganizationId}</StateOrganizationId>
                <EducationOrgIdentificationCode>
                    <#if (edorgIdentity.EducationOrgIdentificationCode.@IdentificationSystem[0])??> 
                        IdentificationSystem="${edorgIdentity.EducationOrgIdentificationCode.@IdentificationSystem}"
                    </#if>
                    <#if (edorgIdentity.EducationOrgIdentificationCode.ID[0])??>
                        <ID>${edorgIdentity.EducationOrgIdentificationCode.ID}</ID>
                    </#if>
                </EducationOrgIdentificationCode>
            </#list>
        </#if>

    </GradingPeriodIdentity>
</GradingPeriodReference>