var listOfStudents = 'listOfStudents';

function getStudentListData() {
	var edorgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	var courseIndex = $("#courseSelect").val();
	var selectionIndex = $("#sectionSelect").val();
      
	location.href = contextRootPath + "/service/list/" + 
					instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections[selectionIndex].id;
}
  
function printStudentList() {
	var tableId = getTableId();
	var panelConfig = SLC.dataProxy.getConfig(listOfStudents);
	var viewIndex=$("#viewSelect").val();
	var options={};
	if(viewIndex != -1) {
		jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
		SLC.grid.create(tableId, options, SLC.dataProxy.getData(listOfStudents), {});
	}
}
    
function clearStudentList() {
	$('#'+getTableId()).jqGrid("GridUnload");
}
    
function filterStudentList(filterBy) {
	var panelConfig = SLC.dataProxy.getConfig(listOfStudents);
	var options={};
	var viewIndex = $("#viewSelect").val();
	if(viewIndex != -1) {
		jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
	      
		if (filterBy == undefined) {
			SLC.grid.create(getTableId(), options, SLC.dataProxy.getData(listOfStudents), {});
		} else {
	         
			var filteredData = jQuery.extend({}, SLC.dataProxy.getData(listOfStudents));
			filteredStudents = filteredData.students;
			fieldName = filterBy['condition']['field'];
			fieldValues = filterBy['condition']['value'];
			filteredStudents = jQuery.grep(filteredStudents, function(n, i) {
				filterValue = n[fieldName];
	            var y = jQuery.inArray(filterValue, fieldValues);
	            return y != -1;
			});
	
			filteredData.students = filteredStudents;
			SLC.grid.create(getTableId(), options, filteredData, {});
		}
	}
}


function filterStudents() {
    var filterSelect = $("#filterSelect").val();
    filterStudentList(SLC.dataProxy.widgetConfig.lozenge.items[filterSelect]); 
}

