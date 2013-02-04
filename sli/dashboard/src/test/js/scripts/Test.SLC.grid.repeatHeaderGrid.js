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

/*global SLC module ok test deepEqual $*/

module("SLC.grid.repeatHeaderGrid");

	test('Test SLC.grid.repeatHeaderGrid Namespace', function () {
		ok(SLC.grid.repeatHeaderGrid !== undefined, 'SLC.grid.repeatHeaderGrid namespace should be defined');
		ok(typeof SLC.grid.repeatHeaderGrid === 'object', 'SLC.grid.repeatHeaderGrid should be an object');
	});


module("SLC.grid.repeatHeaderGrid.create", {
	setup: function () {
		$("body").append('<div id="repeatHeaderGrid1"></div>');
		$("body").append('<div id="repeatHeaderGrid2"></div>');
	},
	teardown: function () {
		$("#repeatHeaderGrid1").remove();
		$("#repeatHeaderGrid2").remove();
	}
});
	
	test('Test create method', function () {
		var panelData,
			columnItems;

		ok(SLC.grid.repeatHeaderGrid.create !== undefined, 'SLC.grid.repeatHeaderGrid create method should be defined');
		ok(typeof SLC.grid.repeatHeaderGrid.create === 'function', 'SLC.grid.repeatHeaderGrid create method should be function');
		
		panelData = {
			attendance: [
				{
					absenceCount: "0",
					earlyDepartureCount: "0",
					excusedAbsenceCount: "3",
					gradeLevel: "1",
					inAttendanceCount: "62",
					present: "93",
					schoolName: "South Daybreak Elementary",
					tardyCount: "7",
					term: "2011-2012",
					totalAbsencesCount: "5",
					unexcusedAbsenceCount: "2"
				},

				{
					absenceCount: "0",
					earlyDepartureCount: "0",
					excusedAbsenceCount: "2",
					gradeLevel: "1",
					inAttendanceCount: "62",
					present: "96",
					schoolName: "South Daybreak Elementary",
					tardyCount: "3",
					term: "2010-2011",
					totalAbsencesCount: "2",
					unexcusedAbsenceCount: "1"
				}
			]
		};

		columnItems = {
			id : "attendanceHist",
			type : "REPEAT_HEADER_GRID",
			name : "Attendance History",
			data :{
				entity: "studentAttendance",
				cacheKey: "studentAttendance",
				params: {
					yearsBack: "3"
				}
			},
			params: {
				layout: ["student"]
			},
			root: 'attendance',
			items : [
				{id: "col0", name: "Term", type:"FIELD", datatype: "string", field: "term", width: 100},
				{id: "col1", name: "School", type:"FIELD", datatype: "string", field: "schoolName", width: 210},
				{id: "col1", name: "Grade Level", type:"FIELD", datatype: "string", field: "gradeLevel", align: "right", width: 100},
				{id: "col2", name: "% Present", type:"FIELD", datatype: "string", field: "present", align: "right", width: 75, formatter: 'CutPoint', params:{cutPoints:{89:{style:'color-widget-red'}, 94:{style: 'color-widget-yellow'}, 98:{style:'color-widget-green'}, 100:{style:'color-widget-darkgreen'}}}},
				{id: "col3", name: "Total Absences", type:"FIELD", datatype: "string", field: "totalAbsencesCount", align: "right", width: 100},
				{id: "col4", name: "Excused", type:"FIELD", datatype: "string", field: "excusedAbsenceCount", style:"color-column-blue", align: "right", width: 70},
				{id: "col5", name: "Unexcused", type:"FIELD", datatype: "string", field: "unexcusedAbsenceCount", style:"color-column-red", align: "right", width: 80},
				{id: "col6", name: "Tardy", type:"FIELD", datatype: "string", field: "tardyCount", style:"color-column-orange", align: "right", width: 60}
			]
		};

		SLC.grid.repeatHeaderGrid.create("1", columnItems, panelData);
		deepEqual($("#repeatHeaderGrid1 .repeatHeaderTable1").length, 1, 'First instance of repeat header grid created.');

		SLC.grid.repeatHeaderGrid.create("2", columnItems, panelData);
		deepEqual($("#repeatHeaderGrid2 .repeatHeaderTable1").length, 1, 'Second instance of repeat header grid created.');

	});
