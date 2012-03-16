<div class="csi">
<div id="CSIImage" style="height:100px;width:100px;float:left;">
<img src="/dashboard/static/images/sample_student.png" WIDTH="85" HEIGHT="100" /></div>
<#assign config = viewConfigs["csi"]>
<#assign root = data[config.data.alias]>
<div id="CSIcontent">
<b>${root.name.firstName}<#if root.name.middleName?? &&  root.name.middleName != ""> ${root.name.middleName}</#if> ${root.name.lastSurname}
<#if root.name.generationCodeSuffix?? &&   root.name.generationCodeSuffix != ""> ${ root.name.generationCodeSuffix}</#if>
<#if root.otherName??>
<#list root.otherName as oName>
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
<#if programUtil.hasProgramParticipation(root, program)>
<#assign lozengeConfig = lozengeConfigs.get(program)>
<#assign id = root.id + ".lozenge." + lozengeConfig.getLabel() + "." + random.nextInt()>
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
<tr><td height="1">Grade</td><td><#if root.cohortYears?size != 0>${root.cohortYears[0]} </#if></td> <td>ID</td><td>${root.studentUniqueStateId}</td><td></td><td></td></tr>
<tr><td>Class</td><td>${root.sectionId}</td><td>Teacher</td><td><#if root.teacherName.personalTitlePrefix?? &&  root.teacherName.personalTitlePrefix != ""> ${root.teacherName.personalTitlePrefix}</#if>
${root.teacherName.firstName} <#if root.teacherName.middleName?? &&  root.teacherName.middleName != ""> ${root.teacherName.middleName}</#if> ${root.teacherName.lastSurname}</td><td></td><td></td></tr>
<tr><td></td><td></td><td /><td /><td /><td></td></tr>
</table>
</div>
</div>