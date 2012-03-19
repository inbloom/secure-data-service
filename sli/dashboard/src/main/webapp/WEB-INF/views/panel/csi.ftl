<div class="csi">
<div id="CSIImage" style="height:100px;width:100px;float:left;">
<img src="/dashboard/static/images/sample_student.png" WIDTH="85" HEIGHT="100" /></div>
<@includePanelModel panelId="csi"/>

<div id="CSIcontent">
<b>${panelData.name.firstName}<#if panelData.name.middleName?? &&  panelData.name.middleName != ""> ${panelData.name.middleName}</#if> ${panelData.name.lastSurname}
<#if panelData.name.generationCodeSuffix?? &&   panelData.name.generationCodeSuffix != ""> ${ panelData.name.generationCodeSuffix}</#if>
<#if panelData.otherName??>
<#list panelData.otherName as oName>
<#if oName.otherNameType == "nickname">
(<#if oName.personalTitlePrefix?? &&  oName.personalTitlePrefix != "">${oName.personalTitlePrefix} </#if>
${oName.firstName} 
<#if oName.middleName?? &&  oName.middleName != "">${oName.middleName} </#if>
${oName.lastSurname}
<#if oName.generationCodeSuffix?? &&  oName.generationCodeSuffix != ""> ${oName.generationCodeSuffix}</#if>)
</#if>
</#list>
</#if>
</b>
 
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
<table border="0" width = "90%">
<col width="50" />
<col width="550" />
<col width="70"/>
<col width="550"/>
<col width="70"/>
<tr><td height="1">Grade</td><td><#if panelData.gradeLevel?? && panelData.gradeLevel != "Not Available">${panelData.gradeLevel}<#else>!</#if></td> <td>ID</td><td>${panelData.studentUniqueStateId}</td><td></td><td></td></tr>
<tr><td>Class</td><td>${panelData.sectionId}</td><td>Teacher</td><td><#if panelData.teacherName.personalTitlePrefix?? &&  panelData.teacherName.personalTitlePrefix != ""> ${panelData.teacherName.personalTitlePrefix}</#if>
${panelData.teacherName.firstName} <#if panelData.teacherName.middleName?? &&  panelData.teacherName.middleName != ""> ${panelData.teacherName.middleName}</#if> ${panelData.teacherName.lastSurname}</td><td></td><td></td></tr>
<tr><td></td><td></td><td /><td /><td /><td></td></tr>
</table>
</div>
</div>