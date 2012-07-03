/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


var listOfStudents = 'listOfStudents';

$(document).ready( function() {
	var config = SLC.dataProxy.getConfig("listOfStudents");
    populateView(config.items);
    if (config.items.length > 0) {
        SLC.util.selectDropDownOption("view", 0, false);
    } else {
        $("#viewSelect").val(-1);
    }
    populateFilter();
    SLC.util.selectDropDownOption("filter", -1, false);
    printStudentList();
});
  
function printStudentList() {
	var tableId = getTableId();
	var panelConfig = SLC.dataProxy.getConfig(listOfStudents);
	var viewIndex=$("#viewSelect").val();
	var options={};
	var studentListData = SLC.dataProxy.getData(listOfStudents);
	if(viewIndex != -1) {
		jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
		SLC.grid.tablegrid.create(tableId, options, studentListData, {});
	}
	if (studentListData == undefined)
	    SLC.util.hideErrorMessage();
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
			SLC.grid.tablegrid.create(getTableId(), options, SLC.dataProxy.getData(listOfStudents), {});
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
			SLC.grid.tablegrid.create(getTableId(), options, filteredData, {});
		}
	}
}


function filterStudents() {
    var filterSelect = $("#filterSelect").val(),
    	widgetConfig = SLC.dataProxy.getWidgetConfig("lozenge");
    	
    filterStudentList(widgetConfig.items[filterSelect]); 
}


function populateFilter() {
    var defaultOptions = {"-1": "No Filter"};
	SLC.util.setDropDownOptions("filter", defaultOptions, SLC.dataProxy.getWidgetConfig("lozenge").items, "description", "", true, function() {
		clearStudentList();
		filterStudents();
	});
}

function populateView(panelConfigItems) {
	  $("#viewSelection").show();
	  SLC.util.setDropDownOptions("view", null, panelConfigItems, "name", "", false, function() {
		  clearStudentList();
		  printStudentList();
	  });
} 

