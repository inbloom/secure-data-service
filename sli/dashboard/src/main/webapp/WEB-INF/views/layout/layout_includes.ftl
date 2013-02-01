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
<#assign NUL = {"NUL":true}>
<#macro includePanelModel panelId>
  <#assign panelConfig = viewConfigs[panelId]>
  <#assign panelData = data[panelConfig.data.cacheKey]!NUL>
</#macro>

<#macro includePanelContent panel>
  <#if panel.type == "PANEL">
    <@includePanelModel panelId=panel.id/>
    <#if !panelData.NUL?? >
      <#include "../panel/" + panel.id + ".ftl">
    </#if>
  </#if> 
  <#if panel.type == "GRID">
    <@includeGrid gridId=panel.id/>
  </#if>
  <#if panel.type == "REPEAT_HEADER_GRID">
    <@includeRepeatHeaderGrid gridId=panel.id/>
  </#if>  
  <#if panel.type == "TREE">
    <@includeTree treeId=panel.id/>
  </#if>   
</#macro>

<#function getDivId panelId>
  <#return panelId + "-" + random.nextInt(99999)?string("#####")>
</#function>

<#function getContextPath>
    <#return contextRootPath>
</#function>

<#macro includeGrid gridId>

  <#assign id = getDivId(gridId)>
  </br>
<div class="ui-widget-no-border">
    <table id="${id}"></table>
</div>
    <script type="text/javascript">
      <#-- make grid -->
      SLC.grid.tablegrid.create('${id}', SLC.dataProxy.getConfig("${gridId}"), SLC.dataProxy.getData("${gridId}"));

    </script>

</#macro>

<#macro includeTree treeId>
  
  <#assign id = getDivId(treeId)>
  </br>
<div class="ui-widget-no-border">
    <table id="${id}"></table>
</div>
    <script type="text/javascript">
      <#-- make tree -->
      SLC.grid.tree.create('${id}', SLC.dataProxy.getConfig("${treeId}"), SLC.dataProxy.getData("${treeId}"));

    </script>

</#macro>

<#noescape>
<#macro includeRepeatHeaderGrid gridId>

  <#assign id = getDivId(gridId)>
  <br/>
  <div id="repeatHeaderGrid${id}">
    <script type="text/javascript">
      <#-- make grid -->
      $(function() {
         SLC.grid.repeatHeaderGrid.create('${id}', SLC.dataProxy.getConfig("${gridId}"), SLC.dataProxy.getData("${gridId}"));
      });
    </script>
  </div>

</#macro>




<script>
	
	SLC.util.setContextRootPath('${CONTEXT_ROOT_PATH}');
		
	SLC.dataProxy.loadAll(${viewDataConfig});

	if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
		document.createElement("footer");
	}
	
	document.title = SLC.dataProxy.getLayoutName();

	setTimeout(SLC.util.placeholderFix, 500);

</script>
</#noescape>
<#include "../panel/studentSearch.ftl">
