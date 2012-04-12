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
    'sectionId=e107127f-e91f-4424-bb5a-309515e5e656', 
    function(panelData){
      DashboardUtil.makeGrid(tableId, options, panelData, {})});

   

    </script>

