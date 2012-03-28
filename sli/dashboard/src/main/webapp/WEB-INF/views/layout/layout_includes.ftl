<#macro includePanelModel panelId>
  <#assign panelConfig = viewConfigs[panelId]>
  <#assign panelData = data[panelConfig.data.alias]>
</#macro>

<#function getDivId panelId>
  <#return panelId + "-" + random.nextInt(99999)?string("#####")>
</#function>

<#macro includeGrid gridId>
  
  <#assign id = getDivId(panelConfig.id)>

  <div>
    <table id="${id}"></table>

    <script type="text/javascript">


    var tableId = '${id}';
    var panelConfig = config["${gridId}"];
    var data = dataModel[panelConfig.data.alias];
    if (panelConfig.root) {
      data = data[panelConfig.root];
    }

      <#-- make grid -->
      DashboardUtil.makeGrid(tableId, panelConfig, data);

    </script>

  </div>
</#macro>

<script type="text/javascript" src="/dashboard/static/js/3p/jquery-1.7.1.js"></script>
<script type="text/javascript" src="/dashboard/static/js/3p/jquery-ui/js/jquery-ui-1.8.18.custom.min.js"></script>
<script type="text/javascript" src="/dashboard/static/js/3p/jqGrid/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="/dashboard/static/js/3p/raphael-min.js"></script>
<script type="text/javascript" src="/dashboard/static/js/dashboardUtil.js"></script>
<script type="text/javascript" src="/dashboard/static/js/lozengeWidget.js"></script>
<link rel="stylesheet" type="text/css" href="/dashboard/static/js/3p/jquery-ui/css/ui-lightness/jquery-ui-1.8.18.custom.css" media="screen" />
<link rel="stylesheet" type="text/css" href="/dashboard/static/js/3p/jqGrid/css/ui.jqgrid.css" media="screen" />
<link rel="stylesheet" type="text/css" href="/dashboard/static/css/common.css" media="screen" />
<script>
  var dataModel = ${dataJson};
  var config = ${viewConfigsJson};
</script>