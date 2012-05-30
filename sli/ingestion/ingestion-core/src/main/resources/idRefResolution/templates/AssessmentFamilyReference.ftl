
<AssessmentFamilyIdentity>

 <#list AssessmentFamily.AssessmentFamilyIdentificationCode as afid>
    <AssessmentFamilyIdentificationCode

        <#if (afid.@IdentificationSystem[0])??>
        IdentificationSystem="${afid.@IdentificationSystem}"
        </#if>

        <#if (afid.@AssigningOrganizationCode[0])??>
        AssigningOrganizationCode="${afid.@AssigningOrganizationCode}"
        </#if>
        >
        <#if (afid.ID[0])??>
        <ID>${afid.ID}</ID>
        </#if>
    </AssessmentFamilyIdentificationCode>
   </#list>
    <#if (AssessmentFamily.AssessmentFamilyTitle[0])??>
    <AssessmentFamilyTitle>${AssessmentFamily.AssessmentFamilyTitle}</AssessmentFamilyTitle>
    </#if>

    <#if (AssessmentFamily.Version[0])??>
    <Version>${AssessmentFamily.Version}</Version>
     </#if>
</AssessmentFamilyIdentity>
