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
    'sectionId=c6df5e32-e5da-4e6e-a19a-d0f5c889bb72', 
    function(panelData){
      DashboardUtil.makeGrid(tableId, panelConfig, panelData, {
      onSelectRow: function(rowid, status) { 
        window.open(DashboardUtil.getPageUrl('student', 'id=' + rowid), "_blank")}})});

   

    </script>

