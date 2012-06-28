<#macro includePanelModel panelId>
  <#assign panelConfig = viewConfigs[panelId]>
  <#if !panelConfig.data.lazy>
    <#assign panelData = data[panelConfig.data.cacheKey]>
  </#if>
</#macro>

<#macro includePanelContent panel>
  <#if panel.type == "PANEL">
    <#include "../panel/" + panel.id + ".ftl">
  </#if> 
  <#if panel.type == "GRID">
    <@includeGrid gridId=panel.id/>
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

<#noescape>


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
