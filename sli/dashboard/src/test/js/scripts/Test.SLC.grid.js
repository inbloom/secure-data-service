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

module("SLC.grid.tablegrid");

	test('Test SLC.grid Namespace', function () {
		ok(SLC.grid !== undefined, 'SLC.grid namespace should be defined');
		ok(typeof SLC.grid === 'object', 'SLC.grid should be an object');
	});
	
module("SLC.grid.tablegrid.create", {
	setup: function () {
		$("body").append("<table id='gridTable'></table>");
	},
	teardown: function () {
		$(".ui-jqgrid").remove();
	}
});
	
	test('Test create method', function () {
		ok(SLC.grid.tablegrid.create !== undefined, 'SLC.grid.tablegrid create method should be defined');
		ok(typeof SLC.grid.tablegrid.create === 'function', 'SLC.grid.tablegrid create method should be function');
		
		panelData = {
			students: [
	           	{
	           		"FallSemester2010-2011": {
	           			courseTitle: "7th Grade English",
	           			letterGrade: "B+"
	           		}
	           	}
			]
		}
		columnItems = {
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
		      {name: "Student", width: 150, field: "name.fullName"},
		      {name: "", width: 60, field: "programParticipation"},
		      {name: "Grade", field: "score.grade", width:50},
		      {name: "Absence Count", field: "attendances.absenceCount", width:100},
		      {name: "Tardy Count", field: "attendances.tardyCount", width:100}
		     ]
		   }
		  ] 
		};
		SLC.grid.tablegrid.create("gridTable", columnItems, panelData);
		deepEqual($(".ui-jqgrid-htable").length, 1, 'Create method should create grid view');
	});
