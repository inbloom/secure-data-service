 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  </br>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
  </div>
  <script type="text/javascript">
  function printStudentList(sections,index)
  {
      var gridId = 'listOfStudents';
      var tableId = '${id}';
      var panelConfig = config[gridId];
      var options={};
      jQuery.extend(options, panelConfig, {items:panelConfig.items[0].items});
      DashboardUtil.getData(
        gridId, 
        'sectionId='+sections[index].id, 
        function(panelData){
          DashboardUtil.makeGrid(tableId, options, panelData, {})});
    }
    function clearStudentList()
    {
        $('#${id}').jqGrid("GridUnload");
    }
    </script>

