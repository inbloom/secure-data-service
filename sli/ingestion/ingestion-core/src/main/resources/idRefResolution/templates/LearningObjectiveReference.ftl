
<LearningObjectiveIdentity>
    <LearningObjectiveId 
    
    <#if (LearningObjective.LearningObjectiveId.@ContentStandardName[0])??>
    ContentStandardName="${LearningObjective.LearningObjectiveId.@ContentStandardName}"
    </#if>
    >
    
    <#if (LearningObjective.LearningObjectiveId.IdentificationCode[0])??>
        <IdentificationCode>${LearningObjective.LearningObjectiveId.IdentificationCode}</IdentificationCode>
    </#if>
    
    </LearningObjectiveId>
    
    <#if (LearningObjective.Objective[0])??>
    <Objective>${LearningObjective.Objective}</Objective>
    </#if>
</LearningObjectiveIdentity>