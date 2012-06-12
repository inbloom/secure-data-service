<#if (Assessment[0])??>
<#assign assessment=Assessment>
<#elseif (AssessmentReference[0])??>
<#assign assessment=AssessmentReference.AssessmentIdentity>
</#if>

<AssessmentIdentity>
<#list assessment as a>
    <#if (a.AssessmentIdentificationCode[0])??>
    <AssessmentIdentificationCode 
    <#if (a.AssessmentIdentificationCode.@IdentificationSystem[0])??>
    IdentificationSystem="${a.AssessmentIdentificationCode.@IdentificationSystem}"
    </#if>
    <#if (a.AssessmentIdentificationCode.@AssigningOrganizationCode[0])??>
    AssigningOrganizationCode="${a.AssessmentIdentificationCode.@AssigningOrganizationCode}"
    </#if>
    >
        <ID>${a.AssessmentIdentificationCode.ID}</ID>
    </AssessmentIdentificationCode>
    </#if>
    <#if (a.AssessmentFamilyTitle[0])??>
        <AssessmentFamilyTitle>${a.AssessmentFamilyTitle}</AssessmentFamilyTitle>
    </#if>
    <#if (a.AssessmentTitle[0])??>
        <AssessmentTitle>${a.AssessmentTitle}</AssessmentTitle>
    </#if>
    <#if (a.AssessmentCategory[0])??>
        <AssessmentCategory>${a.AssessmentCategory}</AssessmentCategory>
    </#if>
    <#if (a.AcademicSubject[0])??>
        <AcademicSubject>${a.AcademicSubject}</AcademicSubject>
    </#if>
    <#if (a.GradeLevelAssessed[0])??>
        <GradeLevelAssessed>${a.GradeLevelAssessed}</GradeLevelAssessed>
    </#if>
    <#if (a.Version[0])??>
        <Version>${a.Version}</Version>
    </#if>
</#list>

</AssessmentIdentity>