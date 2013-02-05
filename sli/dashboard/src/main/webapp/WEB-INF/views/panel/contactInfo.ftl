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
<@includePanelModel panelId="contactInfo"/>
<#assign id = getDivId(panelConfig.id)>
<div class="panel-container">
<div id="${id}_student" class="panel-item contactInfoItem">
	<#assign singleContact = panelData>
	<#assign singleContactName = "Student">
	<#include "singleContactInfo.ftl">
</div>
<#assign counter = 2>

<#list panelData.parents as parents>
	<div id="${id}_parent" class="panel-item contactInfoItem">
		<#assign parentContact = parents>
		<#assign parentContactName = "Parent">
		<#include "parentContactInfo.ftl">
	</div>
	<#if counter % 3 = 0>
		<div class="clear"></div>
	</#if>
	<#assign counter = counter + 1>
</#list>
</div>

