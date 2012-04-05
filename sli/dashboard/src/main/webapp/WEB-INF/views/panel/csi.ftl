<@includePanelModel panelId="csi"/>
<div class="csi">

<div class="colImage">
    <img src="/dashboard/static/images/sample_student.png" WIDTH="85" HEIGHT="100" />
</div>


<div class="colMain">
    <h1>${panelData.name.firstName}<#if panelData.name.middleName?? &&  panelData.name.middleName != ""> ${panelData.name.middleName}</#if> ${panelData.name.lastSurname}
    <#if panelData.name.generationCodeSuffix?? && panelData.name.generationCodeSuffix != ""> ${panelData.name.generationCodeSuffix}</#if></h1>
    <#if panelData.otherName??>
    <#list panelData.otherName as oName>
    <small>
    <#if oName.otherNameType == "nickname">
    (<#if oName.personalTitlePrefix?? &&  oName.personalTitlePrefix != "">${oName.personalTitlePrefix} </#if>
    ${oName.firstName} 
    <#if oName.middleName?? &&  oName.middleName != "">${oName.middleName} </#if>
    ${oName.lastSurname}
    <#if oName.generationCodeSuffix?? &&  oName.generationCodeSuffix != ""> ${oName.generationCodeSuffix}</#if>)
    </#if>
    </small>
    </#list>
    </#if>

    <#list programUtil.getProgramCodesForStudent() as program>
        <#if programUtil.hasProgramParticipation(panelData, program)>
            <#assign lozengeConfig = lozengeConfigs.get(program)>
            <#assign id = getDivId(panelData.id + "-lozenge-" + lozengeConfig.getLabel())>
    <#-- drawing code -->
    <span id="${id}" class="lozenge"></span>
    <script>
      var widget = new LozengeWidget("${id}", "${lozengeConfig.getLabel()}", "${lozengeConfig.getColor()}", "${lozengeConfig.getStyle()}");
      widget.create();
    </script>
        </#if>
    </#list>
    </div>

    <div class="studentInfo">
        <div class="col1">
            <div class="field"><span>Grade</span><span><#if panelData.gradeLevel?? && panelData.gradeLevel != "Not Available">${panelData.gradeLevelCode}<#else>!</#if></span></div>
            <div class="field"><span>Class</span><span><#if panelData.sectionId?? && panelData.sectionId != "">${panelData.sectionId}<#else>!</#if></span></div>
        </div>

        <div class="col2">
            <div class="field"><span>ID</span><span>${panelData.studentUniqueStateId}</span></div>
            <div class="field"p><span>Teacher</span><span><#if panelData.teacherName??><#if panelData.teacherName.personalTitlePrefix?? &&  panelData.teacherName.personalTitlePrefix != ""> ${panelData.teacherName.personalTitlePrefix}</#if>
${panelData.teacherName.firstName} <#if panelData.teacherName.middleName?? &&  panelData.teacherName.middleName != ""> ${panelData.teacherName.middleName}</#if> ${panelData.teacherName.lastSurname}<#else>!</#if></span></div>
        </div>
    </div>
</div>

</div>
