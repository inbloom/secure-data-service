
<LearningStandardIdentity>
    <LearningStandardId

    <#if (LearningStandard.LearningStandardId.@ContentStandardName[0])??>
    ContentStandardName="${LearningStandard.LearningStandardId.@ContentStandardName}"
    </#if>

    >

    <#if (LearningStandard.LearningStandardId.IdentificationCode[0])??>
        <IdentificationCode>${LearningStandard.LearningStandardId.IdentificationCode}</IdentificationCode>
    </#if>

    </LearningStandardId>
</LearningStandardIdentity>