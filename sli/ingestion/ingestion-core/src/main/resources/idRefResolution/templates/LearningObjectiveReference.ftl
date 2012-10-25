
<LearningObjectiveIdentity>
    <#if (LearningObjective.LearningObjectiveId.IdentificationCode[0])??>

    <LearningObjectiveId 
    
    <#if (LearningObjective.LearningObjectiveId.@ContentStandardName[0])??>
    ContentStandardName="${LearningObjective.LearningObjectiveId.@ContentStandardName}"
    </#if>
    >
    
    <#if (LearningObjective.LearningObjectiveId.IdentificationCode[0])??>
        <IdentificationCode>${LearningObjective.LearningObjectiveId.IdentificationCode}</IdentificationCode>
    </#if>
    
    </LearningObjectiveId>
    </#if>

    <#if (LearningObjective.Objective[0])??>
    <Objective>${LearningObjective.Objective}</Objective>
    </#if>
    <#if (LearningObjective.AcademicSubject[0])??>
    <AcademicSubject>
        ${LearningObjective.AcademicSubject}
    </AcademicSubject>
    </#if>
    <#if (LearningObjective.ObjectiveGradeLevel[0])??>
    <ObjectiveGradeLevel>
        ${LearningObjective.ObjectiveGradeLevel}
    </ObjectiveGradeLevel>
    </#if>
</LearningObjectiveIdentity>