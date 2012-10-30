<GradingPeriodReference>
    <GradingPeriodIdentity>
        <#if (GradingPeriod.GradingPeriodIdentity.EducationalOrgReference.EducationalOrgIdentity.StateOrganizationId[0])??>
            <EducationalOrgReference>
                <EducationalOrgIdentity>
                    <StateOrganizationId>${GradingPeriod.GradingPeriodIdentity.EducationalOrgReference.EducationalOrgIdentity.StateOrganizationId}</StateOrganizationId>
                </EducationalOrgIdentity>
            </EducationalOrgReference>
        </#if>

        <#if (GradingPeriod.GradingPeriodIdentity.GradingPeriod[0])??>
            <GradingPeriod>${GradingPeriod.GradingPeriodIdentity.GradingPeriod}</GradingPeriod>
        </#if>

        <#if (GradingPeriod.GradingPeriodIdentity.BeginDate[0])??>
            <BeginDate>${GradingPeriod.GradingPeriodIdentity.BeginDate}</BeginDate>
        </#if>

    </GradingPeriodIdentity>
</GradingPeriodReference>
