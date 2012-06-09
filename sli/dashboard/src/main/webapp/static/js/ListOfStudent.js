var listOfStudents = 'listOfStudents';

function getStudentListData() {
	var edorgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	var courseIndex = $("#courseSelect").val();
	var selectionIndex = $("#sectionSelect").val();
      
	location.href = contextRootPath + "/service/list/" + 
					courseSectionData[courseIndex].sections[selectionIndex].id;
}
  
function printStudentList() {
	var tableId = getTableId();
	var panelConfig = DashboardProxy.getConfig(listOfStudents);
	var viewIndex=$("#viewSelect").val();
	var options={};
	if(viewIndex != -1) {
		jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
		DashboardUtil.makeGrid(tableId, options, DashboardProxy.getData(listOfStudents), {});
	}
}
    
function clearStudentList() {
	$('#'+getTableId()).jqGrid("GridUnload");
}
    
function filterStudentList(filterBy) {
	var panelConfig = DashboardProxy.getConfig(listOfStudents);
	var options={};
	var viewIndex = $("#viewSelect").val();
	if(viewIndex != -1) {
		jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
	      
		if (filterBy == undefined) {
			DashboardUtil.makeGrid(getTableId(), options, DashboardProxy.getData(listOfStudents), {});
		} else {
	         
			var filteredData = jQuery.extend({}, DashboardProxy.getData(listOfStudents));
			filteredStudents = filteredData.students;
			fieldName = filterBy['condition']['field'];
			fieldValues = filterBy['condition']['value'];
			filteredStudents = jQuery.grep(filteredStudents, function(n, i) {
				filterValue = n[fieldName];
	            var y = jQuery.inArray(filterValue, fieldValues);
	            return y != -1;
			});
	
			filteredData.students = filteredStudents;
			DashboardUtil.makeGrid(getTableId(), options, filteredData, {});
		}
	}
}


function filterStudents() {
    var filterSelect = $("#filterSelect").val();
    filterStudentList(DashboardProxy.widgetConfig.lozenge.items[filterSelect]); 
}

