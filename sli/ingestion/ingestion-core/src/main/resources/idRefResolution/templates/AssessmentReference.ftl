<AssessmentIdentity>
<#list AssessmentReference.AssessmentIdentity.AssessmentIdentificationCode.ID as aid>
    <AssessmentIdentificationCode>
        <ID>${aid}</ID>
    </AssessmentIdentificationCode>
</#list>
</AssessmentIdentity>