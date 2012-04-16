 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  </br>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
  </div>
  <script type="text/javascript">
  
  function getTableId() {
    return '${id}';
  }
  
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
      var tableId = getTableId();
      var panelConfig = config[gridId];
      var viewSelect=document.getElementById("viewSelect");
      var viewIndex=viewSelect.options[viewSelect.selectedIndex].value;
      var sections = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections
      var options={};
      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
      DashboardUtil.getData(
        gridId, 
        'sectionId='+sections[selectionIndex].id, 
        function(panelData){
          DashboardProxy.listOfStudents = panelData;
          DashboardUtil.makeGrid(tableId, options, panelData, {})});
    }
    function clearStudentList()
    {
        $('#'+getTableId()).jqGrid("GridUnload");
    }
    
    function filterStudentList(filterBy)
    {
      var panelConfig = config['listOfStudents'];
      var options={};
      var viewSelect=document.getElementById("viewSelect");
      var viewIndex=viewSelect.options[viewSelect.selectedIndex].value;      
      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
      
      
      if (filterBy == undefined) {
        DashboardUtil.makeGrid(getTableId(), options, DashboardProxy.listOfStudents, {});
      }else {
         
          var filteredData = jQuery.extend({}, DashboardProxy.listOfStudents);
          filteredStudents = filteredData.students
          fieldName = filterBy['condition']['field']
          fieldValues = filterBy['condition']['value']
          filteredStudents = jQuery.grep(filteredStudents, function(n, i){
            filterValue = n[fieldName]
            var y = jQuery.inArray(filterValue, fieldValues)
            return y != -1;
          });

          filteredData.students = filteredStudents
          DashboardUtil.makeGrid(getTableId(), options, filteredData, {});
      }
    }
    </script>

