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

/*
 * SLC studentList
 * Handles all the methods related to students list like print, filter, get student list data
 */
/*global SLC $ */

SLC.namespace('SLC.studentList', (function () {
	
	var listOfStudents = 'listOfStudents',
		instHierarchy = SLC.dataProxy.getData('populationWidget').root,
		util = SLC.util,
		contextRootPath = util.getContextRootPath();
    
	function getStudentListData() {
		var edorgIndex = $("#edOrgSelect").val(),
			schoolIndex = $("#schoolSelect").val(),
			courseIndex = $("#courseSelect").val(),
			selectionIndex = $("#sectionSelect").val(),
			courseSectionData = SLC.population.getCourseData();
	      
		location.href = contextRootPath + "/service/list/" + courseSectionData[courseIndex].sections[selectionIndex].id;
	}
	  
	function printStudentList() {
		var tableId = util.getTableId(),
			panelConfig = SLC.dataProxy.getConfig(listOfStudents),
			viewIndex = $("#viewSelect").val(),
			options = {};
			
		if (viewIndex !== -1) {
			$.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
			SLC.grid.tablegrid.create(tableId, options, SLC.dataProxy.getData(listOfStudents), {});
		}
	}
	    
	function clearStudentList() {
		$('#'+util.getTableId()).jqGrid("GridUnload");
	}
	    
	function filterStudentList(filterBy) {
		var panelConfig = SLC.dataProxy.getConfig(listOfStudents),
			options = {},
			viewIndex = $("#viewSelect").val(),
			filteredData = $.extend({}, SLC.dataProxy.getData(listOfStudents)),
			filteredStudents = filteredData.students,
			fieldName,
			fieldValues,
			filterValue;
			
		if (viewIndex !== -1) {
			$.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
		      
			if (filterBy === undefined) {
				SLC.grid.tablegrid.create(util.getTableId(), options, SLC.dataProxy.getData(listOfStudents), {});
			} else {
		         
				fieldName = filterBy.condition.field;
				fieldValues = filterBy.condition.value;
					
				filteredStudents = $.grep(filteredStudents, function(n, i) {
					filterValue = n[fieldName];
		            var y = $.inArray(filterValue, fieldValues);
		            return y != -1;
				});
		
				filteredData.students = filteredStudents;
				SLC.grid.tablegrid.create(util.getTableId(), options, filteredData, {});
			}
		}
	}
	
	
	function filterStudents() {
	    var filterSelect = $("#filterSelect").val(),
			widgetConfig = SLC.dataProxy.getWidgetConfig("lozenge");
			
	    filterStudentList(widgetConfig.items[filterSelect]); 
	}
	
	return {
		getStudentListData: getStudentListData,
		printStudentList: printStudentList,
		clearStudentList: clearStudentList,
		filterStudentList: filterStudentList,
		filterStudents: filterStudents
	};
	
	}())
);