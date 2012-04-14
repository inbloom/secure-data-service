 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  </br>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
  </div>
  <div id="mydebug" />
  <script type="text/javascript">
  function printStudentList()
  {
      var edorgSelect = document.getElementById("edOrgSelect");
      var schoolSelect = document.getElementById("schoolSelect");
      var courseSelect = document.getElementById("courseSelect");
      var selectionSelect = document.getElementById("sectionSelect");
      var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
      var schoolIndex = schoolSelect.options[schoolSelect.selectedIndex].value;
      var courseIndex = courseSelect.options[courseSelect.selectedIndex].value;
      var selectionIndex = selectionSelect.options[selectionSelect.selectedIndex].value;
      var gridId = 'listOfStudents';
      var tableId = '${id}';
      var panelConfig = config[gridId];
      var viewSelect=document.getElementById("viewSelect");
      var viewIndex=viewSelect.options[viewSelect.selectedIndex].value;
      var sections = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections
      var options={};
          document.getElementById("mydebug").innerHTML = sections[selectionIndex].id;
      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
      DashboardUtil.getData(
        gridId, 
        'sectionId='+sections[selectionIndex].id, 
        function(panelData){
          DashboardUtil.makeGrid(tableId, options, panelData, {})});
    }
    function clearStudentList()
    {
        $('#${id}').jqGrid("GridUnload");
    }
    </script>

