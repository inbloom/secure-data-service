<@includePanelModel panelId="contactInfo"/>
<#assign id = getDivId(panelConfig.id)>
<div class="panel-container">
<div id="${id}_student" class="panel-item contactInfoItem">
  <#assign singleContact = panelData>
  <#assign singleContactName = "Student">
  <#include "singleContactInfo.ftl">
</div>
<!-- An example for parent panels - please change to the right singleContact and Single Contact name -->
<div id="${id}_parent" class="panel-item contactInfoItem">
  <#assign singleContact = panelData>
  <#assign singleContactName = "Parent">
  <#include "singleContactInfo.ftl">
</div>
</div>
