<h1> Hello </h1>
<#list panelData.attendanceList as attendances>
	<#if attendances.date ??>
		${attendances.date}
	</#if>
</#list>