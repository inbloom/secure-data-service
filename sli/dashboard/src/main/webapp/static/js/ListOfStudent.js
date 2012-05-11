var listOfStudents = 'listOfStudents';
var selectedPopulation=dataModel['userEdOrg']['selectedPopulation'];
var edOrgIndex = -1
var schoolIndex = -1;
var courseIndex = -1;
var sectionIndex = -1;

if(selectedPopulation != undefined && selectedPopulation != null) {

    $(document).ready( function() {
    	
        var edOrgLength = instHierarchy.length;
        edOrgIndex = edOrgLength == 1 ?  0 : -1;
        if (edOrgLength != 1) {
            for(var i=0; i < edOrgLength; i++) {
                if(instHierarchy[i].name == selectedPopulation.name) {
                    DashboardUtil.selectDropDownOption("edOrg", i, true);
                    edOrgIndex = i;
                    break;
                }
            }
        } 
        
        var schoolsLength = instHierarchy[edOrgIndex].schools.length;
        schoolIndex = schoolsLength == 1 ? 0 : -1;
        if (edOrgIndex > -1 && schoolsLength > 1) {
           for(var i=0; i < schoolsLength; i++) {
                if(instHierarchy[edOrgIndex].schools[i].id == selectedPopulation.section.schoolId) {
                    DashboardUtil.selectDropDownOption("school", i, true);
                    schoolIndex = i;
                    break;
                }
            } 
        }
        
        var coursesLength =  instHierarchy[edOrgIndex].schools[schoolIndex].courses.length;
        courseIndex = coursesLength == 1 ? 0 : -1;
        if (edOrgIndex > -1 && schoolIndex > -1 && coursesLength > 1) {
            for(var i=0; i < coursesLength; i++) {
                if(instHierarchy[edOrgIndex].schools[schoolIndex].courses[i].id == selectedPopulation.section.courseId) {
                    DashboardUtil.selectDropDownOption("course", i, true);
                    courseIndex = i;
                    break;
                }
            } 
        }
        
        var sectionsLength = instHierarchy[edOrgIndex].schools[schoolIndex].courses[courseIndex].sections.length;
        sectionIndex = sectionsLength == 1 ? 0 : -1;
        if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionsLength > 0) { // No autoselection for section
            for(var i=0; i < sectionsLength; i++) {
                if(instHierarchy[edOrgIndex].schools[schoolIndex].courses[courseIndex].sections[i].id == selectedPopulation.section.id) {
                    DashboardUtil.selectDropDownOption("section", i, false);
                    sectionIndex = i;
                    break;
                } 
            }
        }
        
        if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionIndex > -1) {
            DashboardProxy.load("listOfStudents", selectedPopulation.section.id, function(panel) {
                populateView(panel.viewConfig.items);
                if (panel.viewConfig.items.length > 0) {
                    DashboardUtil.selectDropDownOption("view", 0, false);
                } else {
                    $("#viewSelect").val(-1);
                }
                populateFilter();
                DashboardUtil.selectDropDownOption("filter", -1, false);
                printStudentList();
            });
        }
    });
}

function getStudentListData() 
  {
	  var edorgIndex = $("#edOrgSelect").val();
      var schoolIndex = $("#schoolSelect").val();
      var courseIndex = $("#courseSelect").val();
      var selectionIndex = $("#sectionSelect").val();
      
      location.href = contextRootPath + "/service/layout/listOfStudentsPage/" + instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections[selectionIndex].id;
  }
  
  function populateView(panelConfigItems) 
  {
	  $("#viewSelection").show();
	  
	  DashboardUtil.setDropDownOptions("view", null, panelConfigItems, "name", "", false, function() {
		  printStudentList();
	  });
  } 
  
  function printStudentList()
  {
      var tableId = getTableId();
      var panelConfig = DashboardProxy.getConfig(listOfStudents);
      var viewIndex=$("#viewSelect").val();
      var options={};
      if(viewIndex != -1) {
	      jQuery.extend(options, panelConfig, {items:panelConfig.items[viewIndex].items});
	      DashboardUtil.makeGrid(tableId, options, DashboardProxy.getData(listOfStudents), {});
      }
    }
    
    function clearStudentList()
    {
        $('#'+getTableId()).jqGrid("GridUnload");
    }
    
    function clearSelections(currentSelect) {
    	var selects = { "edOrg":1, "school":2, "course":3, "section":4 };
    	switch(selects[currentSelect]) {
    		case 1: $("#schoolSelect").val(-1);
    				$("#schoolSelectMenu .optionText").html("");
    				$("#schoolSelectMenu .dropdown-toggle").addClass("disabled");
    				$("#schoolSelectMenu .dropdown-menu").html("");
    		case 2: $("#courseSelect").val(-1);
    				$("#courseSelectMenu .optionText").html("");
    				$("#courseSelectMenu .dropdown-toggle").addClass("disabled"); 
    				$("#courseSelectMenu .dropdown-menu").html("");
    		case 3: $("#sectionSelect").val(-1);
    				$("#sectionSelectMenu .optionText").html("");
    				$("#sectionSelectMenu .dropdown-toggle").addClass("disabled");
    				$("#sectionSelectMenu .dropdown-menu").html("");
    		case 4: $("#filterSelect").val(-1);
    				$("#filterSelectMenu .optionText").html("");
    				$("#filterSelectMenu .dropdown-toggle").addClass("disabled");
    				$("#filterSelectMenu .dropdown-menu").html("");
    				$("#viewSelect").val(-1);
    				$("#viewSelectMenu .optionText").html("");
    				$("#viewSelectMenu .dropdown-toggle").addClass("disabled");
    				$("#viewSelectMenu .dropdown-menu").html("");
    				$("#viewSelection").hide();
    				clearStudentList();
    	}
    }
    
    function filterStudentList(filterBy)
    {
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
	          filteredStudents = jQuery.grep(filteredStudents, function(n, i){
	            filterValue = n[fieldName];
	            var y = jQuery.inArray(filterValue, fieldValues);
	            return y != -1;
	          });
	
	          filteredData.students = filteredStudents;
	          DashboardUtil.makeGrid(getTableId(), options, filteredData, {});
	      }
      }
    }
function populateInstHierarchy() {
	
	$("#viewSelection").hide();
	clearSelections("edOrg");
	DashboardUtil.setDropDownOptions("edOrg", null,  instHierarchy, "name", "", true, function() {
		populateSchoolMenu();
	});
}

function populateSchoolMenu() {
	var edOrgIndex = $("#edOrgSelect").val();
	clearSelections("school");
	DashboardUtil.setDropDownOptions("school", null, instHierarchy[edOrgIndex].schools, "nameOfInstitution", "", true, function() {
		populateCourseMenu();
	});
}

function populateCourseMenu(){
	var edOrgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	clearSelections("course");
	DashboardUtil.setDropDownOptions("course", null, instHierarchy[edOrgIndex].schools[schoolIndex].courses, "courseTitle", "", true, function() {
		populateSectionMenu();
	});
}

function populateSectionMenu(){
	
	var edOrgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	var courseIndex = $("#courseSelect").val();
	clearSelections("section");
	DashboardUtil.setDropDownOptions("section", null, instHierarchy[edOrgIndex].schools[schoolIndex].courses[courseIndex].sections, "sectionName", "", false, function() {
		getStudentListData();
	});
}

function populateFilter() {
    var defaultOptions = {"-1": "No Filter"};
	DashboardUtil.setDropDownOptions("filter", defaultOptions, DashboardProxy.widgetConfig.lozenge.items, "description", "", true, function() {
		clearStudentList();
		filterStudents();
	});
}

function filterStudents() {
    var filterSelect = $("#filterSelect").val();
    filterStudentList(DashboardProxy.widgetConfig.lozenge.items[filterSelect]); 
}