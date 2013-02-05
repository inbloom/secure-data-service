/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

	var studentLists = {};

	function StudentList(identifier) {
		var tableId = identifier,
			listOfStudents = 'listOfStudents',
			util = SLC.util,
			contextRootPath = util.getContextRootPath(),
			wrapper = "LOS"+tableId,
			view = wrapper+" .view",
			filter = wrapper+" .filter",
			viewSelect = "#"+view+"Select";

		function printStudentList() {
			var panelConfig = SLC.dataProxy.getConfig(listOfStudents),
				viewIndex = $(viewSelect).val(),
				options = {},
				studentListData = SLC.dataProxy.getData(listOfStudents),
				i;

			if (viewIndex !== -1) {
				$.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
				SLC.grid.tablegrid.create(tableId, options, studentListData, {});
			}
			if (studentListData === undefined) {
				SLC.util.hideErrorMessage();
			}
		}

		function clearStudentList() {
			$('#'+tableId).jqGrid("GridUnload");
		}

		function filterStudentList(filterBy) {
			var panelConfig = SLC.dataProxy.getConfig(listOfStudents),
				options = {},
				viewIndex = $(viewSelect).val(),
				filteredData = $.extend({}, SLC.dataProxy.getData(listOfStudents)),
				filteredStudents = filteredData.students,
				fieldName,
				fieldValues,
				filterValue;

			if (viewIndex !== -1) {
				$.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});

				if (filterBy === undefined) {
					SLC.grid.tablegrid.create(tableId, options, SLC.dataProxy.getData(listOfStudents), {});
				} else {

					fieldName = filterBy.condition.field;
					fieldValues = filterBy.condition.value;

					filteredStudents = $.grep(filteredStudents, function(n, i) {
						filterValue = n[fieldName];
						var y = $.inArray(filterValue, fieldValues);
						return y !== -1;
					});

					filteredData.students = filteredStudents;
					SLC.grid.tablegrid.create(tableId, options, filteredData, {});
				}
			}
		}


		function filterStudents(obj) {
			var filterSelect = $(obj).val(),
				widgetConfig = SLC.dataProxy.getWidgetConfig("lozenge");

			filterStudentList(widgetConfig.items[filterSelect]);
		}

		function populateFilter() {
			var defaultOptions = {"-1": "No Filter"};
			SLC.util.setDropDownOptions(filter, defaultOptions, SLC.dataProxy.getWidgetConfig("lozenge").items, "description", "", true, function(obj) {
				clearStudentList();
				filterStudents(obj);
			});
		}

		function populateView(panelConfigItems) {
			$("#viewSelection").show();
			SLC.util.setDropDownOptions(view, null, panelConfigItems, "name", "", false, function() {
				clearStudentList();
				printStudentList();
			});
		}

		$(document).ready( function() {
			var config = SLC.dataProxy.getConfig("listOfStudents");
			populateView(config.items);
			if (config.items.length > 0) {
				SLC.util.selectDropDownOption(view, 0, false);
			} else {
				$(viewSelect).val(-1);
			}
			populateFilter();
			SLC.util.selectDropDownOption(filter, -1, false);
			printStudentList();
		});
	}

	return {
		create : function(id) {
			if(typeof id === "string" && id !== "") {
				if(studentLists[id]) {
					return studentLists[id];
				}
				studentLists[id] = new StudentList(id);
				return studentLists[id];
			}
			return false;
		}
	};

}())
);
