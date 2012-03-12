<div class="csi">
<div id="CSIImage" style="height:100px;width:100px;float:left;">
<img src="/dashboard/static/images/juggernaut.jpg" WIDTH="100" HEIGHT="100" /></div>
<#assign root = csi>
<#assign panelId = .now>
<div id="CSIcontent">
<b>${root.name.firstName}<#if root.name.middleName != ""> ${root.name.middleName}</#if> ${root.name.lastSurname}</b>
 
<#list programUtil.getProgramCodesForStudent() as program>
<#if programUtil.hasProgramParticipation(csi, program)>
<#assign lozengeConfig = lozengeConfigs.get(program)>
<#assign id = "${panelId}.${root.id}.lozenge.${lozengeConfig.getLabel()}">
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
<tr><td height="1">Grade</td><td><#if root.cohortYears?size != 0>${root.cohortYears[0]} </#if></td> <td>ID</td><td>${root.id}</td><td></td><td></td></tr>
<tr><td>Class</td><td>${root.sectionId}</td><td>Teacher</td><td>${root.teacherId}</td><td></td><td></td></tr>
<tr><td></td><td></td><td /><td /><td /><td></td></tr>
</table>
</div>
</div>