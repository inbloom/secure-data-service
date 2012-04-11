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
  DashboardUtil.getData(
    gridId, 
    'sectionId=da5b4d1a-63a3-46d6-a4f1-396b3308af83', 
    function(panelData){
      DashboardUtil.makeGrid(tableId, panelConfig, panelData, {
      onSelectRow: function(rowid, status) { 
        window.open(DashboardUtil.getPageUrl('student', 'id=' + rowid), "_blank")}})});

   

    </script>

