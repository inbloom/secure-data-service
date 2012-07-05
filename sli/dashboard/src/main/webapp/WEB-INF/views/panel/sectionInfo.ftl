<@includePanelModel panelId="section"/>
<div class="sectionProfile">
	<#if panelData.uniqueSectionCode??>
		<h1>${panelData.uniqueSectionCode}</h1>
	</#if>
	
	<div class="sectionInfo">
		<div class="section col1 column">
        	<div class="tabular">
        		<table>
        			<tr>
        				<th>Teacher:</th>
        				<td><#if panelData.teacherName??><#if panelData.teacherName.personalTitlePrefix?? &&  panelData.teacherName.personalTitlePrefix != ""> ${panelData.teacherName.personalTitlePrefix}</#if>
						${panelData.teacherName.firstName} <#if panelData.teacherName.middleName?? &&  panelData.teacherName.middleName != ""> ${panelData.teacherName.middleName}</#if> ${panelData.teacherName.lastSurname}<#else>!</#if></td>
        			</tr>
        			<tr>
        			<th>Course:</th>
        			<td><#if panelData.courseTitle?? && panelData.courseTitle != ""> ${panelData.courseTitle}<#else>!</#if></td>
        			</tr>
        			<tr>
        			<th>Subject:</th>
        			<td><#if panelData.subjectArea?? && panelData.subjectArea != ""> ${panelData.subjectArea}<#else>!</#if></td>
        			</tr>        			
        		</table>
			</div>
		</div>
	</div>
</div>