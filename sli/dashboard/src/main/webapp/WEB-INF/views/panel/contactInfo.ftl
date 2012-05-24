<@includePanelModel panelId="contactInfo"/>
<#assign id = getDivId(panelConfig.id)>
<div class="panel-container">
<div id="${id}_student" class="panel-item contactInfoItem">
  <#assign singleContact = panelData>
  <#assign singleContactName = "Student">
  <#include "singleContactInfo.ftl">
</div>
<!-- An example for parent panels - please change to the right singleContact and Single Contact name -->
<#list panelData.parents as parents>	
	<div id="${id}_parent" class="panel-item contactInfoItem">
	  <#assign parentContact = parents>
	  <#assign parentContactName = "Parent">
	  <#include "parentContactInfo.ftl">
	</div>
</#list>
</div>

