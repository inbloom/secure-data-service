<#if (Assessment[0])??>
<#assign assessment=Assessment>
<#elseif (AssessmentReference[0])??>
<#assign assessment=AssessmentReference.AssessmentIdentity>
</#if>

<AssessmentIdentity>
<#list assessment.AssessmentIdentificationCode.ID as aid>
    <AssessmentIdentificationCode>
        <ID>${aid}</ID>
    </AssessmentIdentificationCode>
</#list>
</AssessmentIdentity>