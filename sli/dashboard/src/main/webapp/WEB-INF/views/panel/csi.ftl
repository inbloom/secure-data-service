<div class="csi">
<div id="CSIImage" style="height:100px;width:100px;float:left;">
<img src="/dashboard/static/images/juggernaut.jpg" WIDTH="100" HEIGHT="100" /></div>
<div id="CSIcontent">
<b>${student.name.firstName}<#if student.name.middleName != ""> ${student.name.middleName}</#if> ${student.name.lastSurname}</b>
 
<#list programUtil.getProgramCodesForStudent() as program>
<#if programUtil.hasProgramParticipation(student, program)>
<#assign lozengeConfig = lozengeConfigs.get(program)>
<#assign id = "${student.id}.lozenge.${lozengeConfig.getLabel()}">
    <#-- drawing code -->
    <span id="${id}" class="lozenge"></span>
    <script>
      var widget = new LozengeWidget("${id}", "${lozengeConfig.getLabel()}", "${lozengeConfig.getColor()}", "${lozengeConfig.getStyle()}");
      widget.create();
    </script>
</#if>
</#list>
<#list student.programs as program>
<#assign lozengeConfig = lozengeConfigs.get(program)>
<#assign id = "${student.id}.lozenge.${lozengeConfig.getLabel()}">
    <#-- drawing code -->
    <span id="${id}" class="lozenge"></span>
    <script>
      var widget = new LozengeWidget("${id}", "${lozengeConfig.getLabel()}", "${lozengeConfig.getColor()}", "${lozengeConfig.getStyle()}");
      widget.create();
    </script>
</#list>
<table border="0" width = "90%">
<col width="50" />
<col width="550" />
<col width="70"/>
<col width="550"/>
<col width="70"/>
<tr><td height="1">Grade</td><td>${student.cohortYear}</td> <td>ID</td><td>${student.id}</td><td></td><td></td></tr>
<tr><td>Class</td><td>${student.sectionId}</td><td>Teacher</td><td>${student.teacherId}</td><td></td><td></td></tr>
<tr><td></td><td></td><td /><td /><td /><td></td></tr>
</table>
</div>
</div>
