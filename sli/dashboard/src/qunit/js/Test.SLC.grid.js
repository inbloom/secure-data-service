module("SLC.grid");

	test('Test SLC.grid Namespace', function () {
		ok(SLC.grid !== undefined, 'SLC.grid namespace should be defined');
		ok(typeof SLC.grid === 'object', 'SLC.grid should be an object');
	});
	
module("SLC.grid.create", {
	setup: function () {
		$("body").append("<table id='gridTable'></table>");
	},
	teardown: function () {
		$(".ui-jqgrid").remove();
	}
});
	
	test('Test create method', function () {
		ok(SLC.grid.create !== undefined, 'SLC.util create method should be defined');
		ok(typeof SLC.grid.create === 'function', 'SLC.util create method should be function');
		
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
		SLC.grid.create("gridTable", columnItems, panelData);
		deepEqual($(".ui-jqgrid-htable").length, 1, 'Create method should create grid view');
	});