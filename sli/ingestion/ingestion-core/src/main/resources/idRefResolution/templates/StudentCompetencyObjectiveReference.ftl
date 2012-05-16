<StudentCompetencyObjectiveIdentity>
    <StudentCompetencyObjectiveId>

    <#if (StudentCompetencyObjective.StudentCompetencyObjectiveId[0])??>
        <IdentificationCode>${StudentCompetencyObjective.StudentCompetencyObjectiveId}</IdentificationCode>
    </#if>

    </StudentCompetencyObjectiveId>

    <#if (StudentCompetencyObjective.Objective[0])??>
    <Objective>${StudentCompetencyObjective.Objective}</Objective>
    </#if>

</StudentCompetencyObjectiveIdentity>
