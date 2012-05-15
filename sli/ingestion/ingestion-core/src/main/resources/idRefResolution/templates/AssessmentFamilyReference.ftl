
<AssessmentFamilyIdentity>

 <#if (AssessmentFamily.AssessmentFamilyIdentificationCode[0])??>
    <AssessmentFamilyIdentificationCode


        <#if (AssessmentFamily.AssessmentFamilyIdentificationCode.@IdentificationSystem[0])??>
        IdentificationSystem="${AssessmentFamily.AssessmentFamilyIdentificationCode.@IdentificationSystem}"
        </#if>

        <#if (AssessmentFamily.AssessmentFamilyIdentificationCode.@AssigningOrganizationCode[0])??>
        AssigningOrganizationCode="${AssessmentFamily.AssessmentFamilyIdentificationCode.@AssigningOrganizationCode}"
        </#if>
        >
        <#if (AssessmentFamily.AssessmentFamilyIdentificationCode.ID[0])??>
        <ID>${AssessmentFamily.AssessmentFamilyIdentificationCode.ID}</ID>
        </#if>
    </AssessmentFamilyIdentificationCode>
   </#if>
    <#if (AssessmentFamily.AssessmentFamilyTitle[0])??>
    <AssessmentFamilyTitle>${AssessmentFamily.AssessmentFamilyTitle}</AssessmentFamilyTitle>
    </#if>

    <#if (AssessmentFamily.Version[0])??>
    <Version>${AssessmentFamily.Version}</Version>
     </#if>
</AssessmentFamilyIdentity>
