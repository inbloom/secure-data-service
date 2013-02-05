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

/*global module SLC ok equal test $*/
module("SLC.studentList");

	test('Test SLC.studentList Namespace', function () {
		ok(SLC.studentList !== undefined, 'SLC.studentList namespace should be defined');
		ok(typeof SLC.studentList === 'object', 'SLC.studentList should be an object');
	});
	
module("SLC.studentList", {
	setup: function () {

		var testData = {
			data: {
				listOfStudents: {
					students: [
						{
							"FallSemester2010-2011": {
								courseTitle: "7th Grade English",
								letterGrade: "B+"
							},
							name: {
								firstName: "Alton",
								fullName: "Alton Ausiello",
								lastSurname: "Ausiello"
							},
							gradeLevel: "Eighth grade",
							schoolFoodServicesEligibility: "Free"
						}
					]
				}
			},
			config: {
				listOfStudents: {
					id : "listOfStudents",
					type : "PANEL",
					data :{
						lazy: true,
						entity: "listOfStudents",
						cacheKey: "listOfStudents"
					},
					root: 'students',
					items : [
						{name: "Default View",
							items: [
								{name: "Student", width: 150, field: "name.fullName", style:'ui-ellipsis'},
								{name: "", width: 60, field: "programParticipation"},
								{name: "Grade", field: "score.grade", width:50},
								{name: "Absence Count", field: "attendances.absenceCount", width:100, sorter: 'int'},
								{name: "Tardy Count", field: "attendances.tardyCount", width:100, sorter: 'int'}
							]
						}
					]
				},
				listOfStudentsPage: {
					type: "LAYOUT"
				},
				populationWidget: {
					type: "PANEL"
				},
				studentProfile: {
					type: "LAYOUT",
					items: [{type: "TAB"}]
				}
			},
			widgetConfig: [
				{
					id:"lozenge",
					type:"WIDGET",
					items:[
						{
							description: "English Language Learner",
							name: "ELL",
							type: "FIELD",
							condition: {
								field: "limitedEnglishProficiency",
								value: ["Limited"]
							}
						}
					]
				},
				{id:"population",type:"WIDGET"}
			]
		};

		SLC.dataProxy.loadAll(testData);

		var studentTable1 = '<div id="LOS1234"><div class="viewDiv menuBox"><h4> View </h4><input type="hidden" value="" class="viewSelect" /><div class="btn-toolbar"><div class="btn-group viewSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div><div class="filterDiv menuBox"><h4> Filter </h4><input type="hidden" value="" class="filterSelect" /><div class="btn-toolbar"><div class="btn-group filterSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div></div><div class="ui-widget-no-border"><table id="1234"></table></div></div>';
		$("body").append(studentTable1);
	},
	teardown: function () {
		$("#LOS1234").remove();
		$("#LOS12345").remove();
		$(".ui-widget-no-border").remove();
	}
});

test('Test Student List grid', function () {
	SLC.studentList.create("1234");
	equal($(".ui-jqgrid-htable").length, 1, "One student list should be created");

	var studentTable2 = '<div id="LOS12345"><div class="viewDiv menuBox"><h4> View </h4><input type="hidden" value="" class="viewSelect" /><div class="btn-toolbar"><div class="btn-group viewSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div><div class="filterDiv menuBox"><h4> Filter </h4><input type="hidden" value="" class="filterSelect" /><div class="btn-toolbar"><div class="btn-group filterSelectMenu"><a class="btn dropdown-toggle" data-toggle="dropdown" href="#"><span class="optionText"> </span><span class="caret"></span></a><ul class="dropdown-menu"></ul></div></div></div></div><div class="ui-widget-no-border"><table id="12345"></table></div></div>';
	$("body").append(studentTable2);

	SLC.studentList.create("12345");
	equal($(".ui-jqgrid-htable").length, 2, "Two student lists should be created");

});
