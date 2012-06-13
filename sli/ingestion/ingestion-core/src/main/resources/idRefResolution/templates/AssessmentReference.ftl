<#if (Assessment[0])??>
<#assign assessment=Assessment>
<#elseif (AssessmentReference[0])??>
<#assign assessment=AssessmentReference.AssessmentIdentity>
</#if>

<AssessmentIdentity>
    <#list assessment.AssessmentIdentificationCode as identificationCode>
    <AssessmentIdentificationCode 
    <#if (identificationCode.@IdentificationSystem[0])??>
    IdentificationSystem="${identificationCode.@IdentificationSystem}"
    </#if>
    <#if (identificationCode.@AssigningOrganizationCode[0])??>
    AssigningOrganizationCode="${identificationCode.@AssigningOrganizationCode}"
    </#if>
    >
        <ID>${identificationCode.ID}</ID>
    </AssessmentIdentificationCode>
    </#list>
    <#if (assessment.AssessmentFamilyTitle[0])??>
        <AssessmentFamilyTitle>${assessment.AssessmentFamilyTitle}</AssessmentFamilyTitle>
    <#elseif (assessment.AssessmentFamilyReference[0])??>
        <#if (assessment.AssessmentFamilyReference.AssessmentFamilyIdentity[0])??>
            <#if (assessment.AssessmentFamilyReference.AssessmentFamilyIdentity.AssessmentFamilyTitle[0])??>
                <AssessmentFamilyTitle>${assessment.AssessmentFamilyReference.AssessmentFamilyIdentity.AssessmentFamilyTitle}</AssessmentFamilyTitle>
            </#if>
        </#if>
    </#if>
    <#if (assessment.AssessmentTitle[0])??>
        <AssessmentTitle>${assessment.AssessmentTitle}</AssessmentTitle>
    </#if>
    <#if (assessment.AssessmentCategory[0])??>
        <AssessmentCategory>${assessment.AssessmentCategory}</AssessmentCategory>
    </#if>
    <#if (assessment.AcademicSubject[0])??>
        <AcademicSubject>${assessment.AcademicSubject}</AcademicSubject>
    </#if>
    <#if (assessment.GradeLevelAssessed[0])??>
        <GradeLevelAssessed>${assessment.GradeLevelAssessed}</GradeLevelAssessed>
    </#if>
    <#if (assessment.Version[0])??>
        <Version>${assessment.Version}</Version>
    </#if>
</AssessmentIdentity>