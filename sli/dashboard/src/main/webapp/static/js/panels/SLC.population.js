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
 * SLC population
 * Handles all the methods related population dropdowns
 */
/*global SLC $ */

SLC.namespace('SLC.population', (function () {

		var students = SLC.studentList,
			instHierarchy = SLC.dataProxy.getData('populationWidget').root,
			courseSectionData;
		
		function getCourseData() {
			return courseSectionData;
		}
		// When a dropdown is unavailable the preceeding dropdowns should be unavailable
		// set dropdown option to -1, empty dropdown options , clear dropdown text, disable the dropdown.
		function clearSelections(currentSelect) {
			var selects = { "edOrg":1, "school":2, "course":3, "section":4, "filter":5 };
			
			switch(selects[currentSelect]) {
				
				case 1: $("#edOrgSelect").val(-1);
						$("#edOrgSelectMenu .optionText").html("");
						$("#edOrgSelectMenu .dropdown-menu").html("");
						break;
						
				case 2: $("#schoolSelect").val(-1);
						$("#schoolSelectMenu .optionText").html("");
						$("#schoolSelectMenu .dropdown-toggle").addClass("disabled");
						$("#schoolSelectMenu .dropdown-menu").html("");
						break;
						
				case 3: $("#courseSelect").val(-1);
						$("#courseSelectMenu .optionText").html("");
						$("#courseSelectMenu .dropdown-toggle").addClass("disabled");
						$("#courseSelectMenu .dropdown-menu").html("");
						break;
						
				case 4: $("#sectionSelect").val(-1);
						$("#sectionSelectMenu .optionText").html("");
						$("#sectionSelectMenu .dropdown-toggle").addClass("disabled");
						$("#sectionSelectMenu .dropdown-menu").html("");
						break;
						
				case 5: $("#filterSelect").val(-1);
						$("#filterSelectMenu .optionText").html("");
						$("#filterSelectMenu .dropdown-toggle").addClass("disabled");
						$("#filterSelectMenu .dropdown-menu").html("");
						$("#viewSelect").val(-1);
						$("#viewSelectMenu .optionText").html("");
						$("#viewSelectMenu .dropdown-toggle").addClass("disabled");
						$("#viewSelectMenu .dropdown-menu").html("");
						$("#viewSelection").hide();
						students.clearStudentList();
			}
		}
		
		function getCourseSectionData() {
			var edOrgIndex = $("#edOrgSelect").val(),
				schoolIndex = $("#schoolSelect").val();
				
			SLC.dataProxy.load('populationCourseSection', instHierarchy[edOrgIndex].schools[schoolIndex].id, function(panel) {
				courseSectionData = SLC.dataProxy.getData('populationCourseSection').root;
			});
		}
		
		function populateSectionMenu(){
			var edOrgIndex = $("#edOrgSelect").val(),
				schoolIndex = $("#schoolSelect").val(),
				courseIndex = $("#courseSelect").val();
				
			clearSelections("section");
			SLC.util.setDropDownOptions("section", null, courseSectionData[courseIndex].sections, "sectionName", "", false, function() {
				students.getStudentListData();
			});
		}
		
		function populateCourseMenu(){
			var edOrgIndex = $("#edOrgSelect").val(),
				schoolIndex = $("#schoolSelect").val();
				
			clearSelections("course");
			SLC.util.setDropDownOptions("course", null, courseSectionData, "courseTitle", "", true, function() {
				clearSelections("section");
				populateSectionMenu();
			});
		}
		
		function populateSchoolMenu() {
			var edOrgIndex = $("#edOrgSelect").val();
			
			clearSelections("school");
			SLC.util.setDropDownOptions("school", null, instHierarchy[edOrgIndex].schools, "nameOfInstitution", "", true, function() {
				clearSelections("course");
				getCourseSectionData();
				populateCourseMenu();
			});
		}
		
		function populateInstHierarchy() {
			$("#viewSelection").hide();
			clearSelections("edOrg");
			SLC.util.setDropDownOptions("edOrg", null, instHierarchy, "name", "", true, function() {
				clearSelections("school");
				populateSchoolMenu();
			});
		}
		
		function populateFilter() {
			var defaultOptions = {"-1": "No Filter"};
			
			SLC.util.setDropDownOptions("filter", defaultOptions, SLC.dataProxy.getWidgetConfig("lozenge").items, "description", "", true, function() {
				students.clearStudentList();
				students.filterStudents();
			});
		}
		
		function populateView(panelConfigItems) {
			$("#viewSelection").show();
			SLC.util.setDropDownOptions("view", null, panelConfigItems, "name", "", false, function() {
				students.clearStudentList();
				students.printStudentList();
			});
		} 
		
		
		$(document).ready( function() {
			var selectedPopulation = SLC.dataProxy.getData('populationWidget').selectedPopulation,
				edOrgIndex = -1,
				schoolIndex = -1,
				courseIndex = -1,
				sectionIndex = -1,
				i;
				
			if (selectedPopulation !== undefined && selectedPopulation !== null) {
				//find the length of options.
				var edOrgLength = instHierarchy.length;
				
				SLC.util.hideErrorMessage();
				
				//if options is empty the dropdown should be disabled.
				edOrgIndex = edOrgLength == 1 ? 0 : -1;
				
				if (edOrgLength !== 1) {
				//loop through the options to find the selected option.
					for (i = 0; i < edOrgLength; i++) {
						if (instHierarchy[i].name === selectedPopulation.name) {
						//select the selected option
							SLC.util.selectDropDownOption("edOrg", i, true);
							edOrgIndex = i;
							break;
						}
					}
				}
				
				var schoolsLength = instHierarchy[edOrgIndex].schools.length;
				schoolIndex = schoolsLength == 1 ? 0 : -1;
				
				if (edOrgIndex > -1 && schoolsLength > 1) {
					for (i = 0; i < schoolsLength; i++) {
						if (instHierarchy[edOrgIndex].schools[i].id == selectedPopulation.section.schoolId) {
							SLC.util.selectDropDownOption("school", i, true);
							schoolIndex = i;
							break;
						}
					}
				}
				
				var coursesLength = courseSectionData.length;
				courseIndex = coursesLength == 1 ? 0 : -1;
				
				if (edOrgIndex > -1 && schoolIndex > -1 && coursesLength > 1) {
					for (i = 0; i < coursesLength; i++) {
						if (courseSectionData[i].id === selectedPopulation.section.courseId) {
							SLC.util.selectDropDownOption("course", i, true);
							courseIndex = i;
							break;
						}
					}
				}
				
				// Section is not auto selected. hence we need to select.
				var sectionsLength = courseSectionData[courseIndex].sections.length;
				sectionIndex = sectionsLength == 1 ? 0 : -1;
				
				if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionsLength > 0) {
					for (i = 0; i < sectionsLength; i++) {
						if (courseSectionData[courseIndex].sections[i].id === selectedPopulation.section.id) {
							SLC.util.selectDropDownOption("section", i, false);
							sectionIndex = i;
							break;
						}
					}
				}
				
				//Load list of students only when all dropdowns are selected
				if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionIndex > -1) {
					SLC.dataProxy.load("listOfStudents", selectedPopulation.section.id, function(panel) {
						var config = SLC.dataProxy.getConfig("listOfStudents");
						populateView(config.items);
						
						if (config.items.length > 0) {
							SLC.util.selectDropDownOption("view", 0, false);
						} else {
							$("#viewSelect").val(-1);
						}
						populateFilter();
						SLC.util.selectDropDownOption("filter", -1, false);
						students.printStudentList();
					});
				}
			}
		}); 
		
		return {
			populateInstHierarchy: populateInstHierarchy,
			getCourseData: getCourseData
		};

	}())
);