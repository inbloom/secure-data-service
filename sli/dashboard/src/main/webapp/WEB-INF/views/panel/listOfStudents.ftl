 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  </br>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
  </div>
  <script type="text/javascript">
  var gridId = 'listOfStudents';

  var tableId = '${id}';
  var panelConfig = config[gridId];
  var options={};
  jQuery.extend(options, panelConfig, {items:panelConfig.items[0].items});
  DashboardUtil.getData(
    gridId, 
    'sectionId=95c00f08-ce12-4618-816b-6a305867b2cf', 
    function(panelData){
      DashboardUtil.makeGrid(tableId, options, panelData, {})});

   

    </script>

