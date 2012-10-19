<GraduationPlanReference>
    <GraduationPlanIdentity>
        <GraduationPlanType>
            ${GraduationPlan.GraduationPlanType}
        </GraduationPlanType>
        <#if (GraduationPlan.EducationOrganizationReference.EducationalOrgIdentity.StateOrganizationId[0])??>
        <EducationalOrgReference>
            <EducationalOrgIdentity>
                <StateOrganizationId>${GraduationPlan.EducationOrganizationReference.EducationalOrgIdentity.StateOrganizationId}</StateOrganizationId>
            </EducationalOrgIdentity>
        </EducationalOrgReference>
        </#if>
    </GraduationPlanIdentity>
</GraduationPlanReference>