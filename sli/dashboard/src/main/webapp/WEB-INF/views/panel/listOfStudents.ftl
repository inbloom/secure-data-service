 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  </br>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
  </div>
  <script type="text/javascript">
  
  function getStudentListData() 
  {
      var edorgSelect = document.getElementById("edOrgSelect");
      var schoolSelect = document.getElementById("schoolSelect");
      var courseSelect = document.getElementById("courseSelect");
      var selectionSelect = document.getElementById("sectionSelect");
      var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
      var schoolIndex = schoolSelect.options[schoolSelect.selectedIndex].value;
      var courseIndex = courseSelect.options[courseSelect.selectedIndex].value;
      var selectionIndex = selectionSelect.options[selectionSelect.selectedIndex].value;
      var sections = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections
      var gridId = 'listOfStudents';
      DashboardUtil.getData(
          gridId,
          'sectionId='+sections[selectionIndex].id,
          function(panelData) {
              //window.panelData = panelData;
              populateView();
              printStudentList();
          });
          
  }
  
  function populateView() 
  {
    var gridId = 'listOfStudents';
    var panelConfig = config[gridId];
    var select = "<select id='viewSelect' onChange='clearStudentList();printStudentList()'>";
    filterViews();
    var index=0;
    for(index=0;index<panelConfig.items.length;index++) {
        select += "<option value='"+index+"'>"+panelConfig.items[index].name+"</option>";
    }
    select += "</selection>";
    document.getElementById("viewDiv").innerHTML = select;
  } 
  
  function filterViews() 
  {
    var gridId = 'listOfStudents';
    var panelConfig = config[gridId];
    var filteredViews = [];
    
    for (var i=0; i < panelConfig.items.length; i++) {
        if (DashboardUtil.checkCondition(DashboardProxy[gridId], panelConfig.items[i].condition)) {
            filteredViews.push(panelConfig.items[i]);
        }
    }
    
    config[gridId].items = filteredViews;
  }
  
  function printStudentList()
  {
      var gridId = 'listOfStudents';
      var tableId = '${id}';
      var panelConfig = config[gridId];
      var viewSelect=document.getElementById("viewSelect");
      var viewIndex=viewSelect.options[viewSelect.selectedIndex].value;
      var options={};
      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
      DashboardUtil.makeGrid(tableId, options, DashboardProxy[gridId], {});
    }
    
    function clearStudentList()
    {
        $('#${id}').jqGrid("GridUnload");
    }
    </script>

