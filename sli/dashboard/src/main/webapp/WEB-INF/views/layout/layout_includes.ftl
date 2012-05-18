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
      DashboardUtil.makeGrid('${id}', DashboardProxy.getConfig("${gridId}"), DashboardProxy.getData("${gridId}"));

    </script>

</#macro>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/3p/jquery-1.7.1.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/3p/jquery-ui/js/jquery-ui-1.8.18.custom.min.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/3p/jquery-ui/js/bootstrap-dropdown.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/3p/jqGrid/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/3p/raphael-min.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/dashboardUtil.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/fuelGaugeWidget.js"></script>
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/js/3p/jquery-ui/css/custom/jquery-ui-1.8.18.custom.css" media="screen" />
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/js/3p/jqGrid/css/ui.jqgrid.css" media="screen" />
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/css/common.css" media="screen" />
<#noescape>
<script>
  var contextRootPath = '${CONTEXT_ROOT_PATH}';
  DashboardProxy.loadAll(${viewDataConfig});
</script>
</#noescape>
<#include "../panel/studentSearch.ftl">
