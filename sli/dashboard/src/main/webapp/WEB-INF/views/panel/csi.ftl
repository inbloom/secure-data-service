<#--
  Copyright 2012-2013 inBloom, Inc. and its affiliates.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<@includePanelModel panelId="csi"/>


<div class="csi">

<div id="csi_colMain" class="colMain">
    <h1>${panelData.name.firstName}<#if panelData.name.middleName?? &&  panelData.name.middleName != ""> ${panelData.name.middleName}</#if> ${panelData.name.lastSurname}
    	<#if panelData.name.generationCodeSuffix?? && panelData.name.generationCodeSuffix != ""> ${panelData.name.generationCodeSuffix}</#if>
        <#if panelData.otherName??>
		    <#list panelData.otherName as oName>
			    <small>
				    <#if oName.otherNameType == "Nickname">
					    (<#if oName.personalTitlePrefix?? &&  oName.personalTitlePrefix != "">${oName.personalTitlePrefix} </#if>${oName.firstName}
					    <#if oName.middleName?? &&  oName.middleName != "">${oName.middleName} </#if>${oName.lastSurname}<#if oName.generationCodeSuffix?? &&  oName.generationCodeSuffix != ""> ${oName.generationCodeSuffix}</#if>)
					</#if>
			    </small>
		    </#list>
	    </#if>
    </h1>
    <script>$("#csi_colMain").append(SLC.util.renderLozenges(SLC.dataProxy.getData("csi")));</script>
    <div class="studentInfo">
        <div class="col1 column">
        	<div class="tabular">
        		<table>
        			<tr>
        				<th>Grade:</th>
        				<td><#if panelData.gradeLevel?? && panelData.gradeLevel != "Not Available">${panelData.gradeLevelCode}<#else>!</#if></td>
        			</tr>
        			<tr>
        				<th>Class:</th>
        				<td><#if panelData.sectionId?? && panelData.sectionId != "">${panelData.sectionId}<#else>!</#if></td>
        			</tr>
        			<tr>
        				<th>ID:</th>
        				<td>${panelData.studentUniqueStateId}</td>
        			</tr>
        			<!--<tr>
        				<th>Teacher:</th>
        				<td><#if panelData.teacherName??><#if panelData.teacherName.personalTitlePrefix?? &&  panelData.teacherName.personalTitlePrefix != ""> ${panelData.teacherName.personalTitlePrefix}</#if>
${panelData.teacherName.firstName} <#if panelData.teacherName.middleName?? &&  panelData.teacherName.middleName != ""> ${panelData.teacherName.middleName}</#if> ${panelData.teacherName.lastSurname}<#else>!</#if></td>
        			</tr>-->
        		</table>
			</div>
		</div>

        <div class="col2 column">
        </div>
    </div>
</div>

</div>


