var listOfStudents = 'listOfStudents';

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
      var sections = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections;
      DashboardProxy.load(
    	  listOfStudents,
          sections[selectionIndex].id,
          function(panel) {
              populateView(panel.viewConfig);
              populateFilter();
              printStudentList();
          });
          
  }
  
  function populateView(panelConfig) 
  {
    var select = "<select id='viewSelect' onChange='clearStudentList();printStudentList()'>";
    var index=0;
    if(panelConfig.items.length == 0) {
	    select += "<option value='-1'></option>";
    }
    else {
	    for(index=0;index<panelConfig.items.length;index++) {
	        select += "<option value='"+index+"'>"+panelConfig.items[index].name+"</option>";
	    }
    }
    select += "</selection>";
    document.getElementById("viewDiv").innerHTML = select;
  } 
  
  function printStudentList()
  {
      var tableId = getTableId();
      var panelConfig = DashboardProxy.getConfig(listOfStudents);
      var viewSelect=document.getElementById("viewSelect");
      var viewIndex=viewSelect.options[viewSelect.selectedIndex].value;
      var options={};
      if(viewIndex != -1) {
	      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
	      DashboardUtil.makeGrid(tableId, options, DashboardProxy.getData(listOfStudents), {});
      }
    }
    
    function clearStudentList()
    {
        $('#'+getTableId()).jqGrid("GridUnload");
    }
    
    function filterStudentList(filterBy)
    {
      var panelConfig = DashboardProxy.getConfig(listOfStudents);
      var options={};
      var viewSelect=document.getElementById("viewSelect");
      var viewIndex=viewSelect.options[viewSelect.selectedIndex].value;      
      if(viewIndex != -1) {
	      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
	      
	      if (filterBy == undefined) {
	        DashboardUtil.makeGrid(getTableId(), options, DashboardProxy.getData(listOfStudents), {});
	      }else {
	         
	          var filteredData = jQuery.extend({}, DashboardProxy.getData(listOfStudents));
	          filteredStudents = filteredData.students;
	          fieldName = filterBy['condition']['field'];
	          fieldValues = filterBy['condition']['value'];
	          filteredStudents = jQuery.grep(filteredStudents, function(n, i){
	            filterValue = n[fieldName];
	            var y = jQuery.inArray(filterValue, fieldValues);
	            return y != -1;
	          });
	
	          filteredData.students = filteredStudents;
	          DashboardUtil.makeGrid(getTableId(), options, filteredData, {});
	      }
      }
    }
function populateInstHierarchy(){
    var y = "<select id='edOrgSelect' onChange='clearStudentList();populateSchoolMenu()'>";
    y += "<option value='-1'></option>";
    var i = 0;
    for(i = 0;i<instHierarchy.length;i++){
	y += "<option value='" +i +"'>"+ instHierarchy[i].name + "</option>";
    }
    y += "</select>";
    document.getElementById("edorgDiv").innerHTML = y;
}

function populateSchoolMenu(){
    var edorgSelect = document.getElementById("edOrgSelect");
    var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
    var y = '';
    document.getElementById("courseDiv").innerHTML = '';
    document.getElementById("sectionDiv").innerHTML = '';
    if( edorgIndex > -1 ) {
        var temp = instHierarchy[edorgIndex].schools;
    
        y = "<select id='schoolSelect' onChange='clearStudentList();populateCourseMenu()'>";
        y += "<option value='-1'></option>"   ;
        var i = 0;
        for(i = 0;i<temp.length;i++){
            y += "<option value='" +i +"'>"+ temp[i].nameOfInstitution + "</option>";
        }
        y += "</select>";
    }
    document.getElementById("schoolDiv").innerHTML = y;
}

function populateCourseMenu(){
    var edorgSelect = document.getElementById("edOrgSelect");
    var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
    var schoolSelect = document.getElementById("schoolSelect");
    var schoolIndex = schoolSelect.options[schoolSelect.selectedIndex].value;
    var y = '';
    document.getElementById("sectionDiv").innerHTML = '';
    if( schoolIndex > -1) {
        var temp = instHierarchy[edorgIndex].schools[schoolIndex].courses;
     
        y = "<select id='courseSelect' onChange='clearStudentList();populateSectionMenu()'>";
        y += "<option value='-1'></option>";
        var j = 0;
        for(j = 0;j < temp.length;j++){
            y += "<option value='" +j +"'>"+ temp[j].courseTitle + "</option>";
        }
    }
    document.getElementById("courseDiv").innerHTML = y;
}

function populateSectionMenu(){
    var edorgSelect = document.getElementById("edOrgSelect");
    var schoolSelect = document.getElementById("schoolSelect");
    var courseSelect = document.getElementById("courseSelect");
    var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
    var schoolIndex = schoolSelect.options[schoolSelect.selectedIndex].value;
    var courseIndex = courseSelect.options[courseSelect.selectedIndex].value;
    var y = '';
    if( courseIndex > -1) {
        var temp = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections;
        y = "<select id='sectionSelect' onChange='clearStudentList();getStudentListData()'>";
        y += "<option value='-1'></option>";
        var i = 0;
        for(;i < temp.length;i++){
            y += "<option value='" +i +"'>"+ temp[i].sectionName + "</option>";
        }
        y += "</select>";
    }
    document.getElementById("sectionDiv").innerHTML = y;
}

function populateFilter() {
    
    var select = "<select id='filterSelect' onChange='clearStudentList();filterStudents()'>";
    var index=0;
    select += "<option value='-1'>No Filter</option>";
    var lozengeConfig = DashboardProxy.widgetConfig.lozenge;
    for(index=0;index<lozengeConfig.items.length; index++) {
        select += "<option value='"+index+"'>"+lozengeConfig.items[index].description+"</option>";
    }
    select += "</select>";
    document.getElementById("filterDiv").innerHTML = select;
    
}

function filterStudents() {
    var filterSelect = document.getElementById("filterSelect");
    filterStudentList(DashboardProxy.widgetConfig.lozenge.items[filterSelect.selectedIndex-1]); 
}
