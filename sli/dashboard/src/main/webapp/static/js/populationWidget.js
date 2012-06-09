$(document).ready( function() {
    var selectedPopulation=DashboardProxy.getData('populationWidget')['selectedPopulation'];
    var edOrgIndex = -1;
    var schoolIndex = -1;
    var courseIndex = -1;
    var sectionIndex = -1;

    if(selectedPopulation != undefined && selectedPopulation != null) {
    	
    	//find the length of options.
        var edOrgLength = instHierarchy.length;
        DashboardUtil.hideErrorMessage();
        
        //if options is empty the dropdown should be disabled.
        edOrgIndex = edOrgLength == 1 ?  0 : -1;
        if (edOrgLength != 1) {
        	
        	//loop through the options to find the selected option.
            for(var i=0; i < edOrgLength; i++) {
                if(instHierarchy[i].name == selectedPopulation.name) {
                	
                	//select the selected option
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
        
        var coursesLength =  courseSectionData.length;
        courseIndex = coursesLength == 1 ? 0 : -1;
        if (edOrgIndex > -1 && schoolIndex > -1 && coursesLength > 1) {
            for(var i=0; i < coursesLength; i++) {
                if(courseSectionData[i].id == selectedPopulation.section.courseId) {
                    DashboardUtil.selectDropDownOption("course", i, true);
                    courseIndex = i;
                    break;
                }
            } 
        }
      
        // Section is not auto selected. hence we need to select.
        var sectionsLength = courseSectionData[courseIndex].sections.length;
        sectionIndex = sectionsLength == 1 ? 0 : -1;
        if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionsLength > 0) { 
            for(var i=0; i < sectionsLength; i++) {
                if(courseSectionData[courseIndex].sections[i].id == selectedPopulation.section.id) {
                    DashboardUtil.selectDropDownOption("section", i, false);
                    sectionIndex = i;
                    break;
                } 
            }
        }
        
        //Load list of students only when all dropdowns are selected
        if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionIndex > -1) {
            DashboardProxy.load("listOfStudents", selectedPopulation.section.id, function(panel) {
            	var config = DashboardProxy.getConfig("listOfStudents");
                populateView(config.items);
                if (config.items.length > 0) {
                    DashboardUtil.selectDropDownOption("view", 0, false);
                } else {
                    $("#viewSelect").val(-1);
                }
                populateFilter();
                DashboardUtil.selectDropDownOption("filter", -1, false);
                printStudentList();
            });
        }
    }
});

// When a dropdown is unavailable the preceeding dropdowns should be unavailable
// set dropdown option to -1, empty dropdown options , clear dropdown text, disable the dropdown.
function clearSelections(currentSelect) {
	var selects = { "edOrg":1, "school":2, "course":3, "section":4, "filter":5 };
	switch(selects[currentSelect]) {
		case 1: $("#edOrgSelect").val(-1);
				$("#edOrgSelectMenu .optionText").html("");
				$("#edOrgSelectMenu .dropdown-menu").html("");
		case 2: $("#schoolSelect").val(-1);
				$("#schoolSelectMenu .optionText").html("");
				$("#schoolSelectMenu .dropdown-toggle").addClass("disabled");
				$("#schoolSelectMenu .dropdown-menu").html("");
		case 3: $("#courseSelect").val(-1);
				$("#courseSelectMenu .optionText").html("");
				$("#courseSelectMenu .dropdown-toggle").addClass("disabled"); 
				$("#courseSelectMenu .dropdown-menu").html("");
		case 4: $("#sectionSelect").val(-1);
				$("#sectionSelectMenu .optionText").html("");
				$("#sectionSelectMenu .dropdown-toggle").addClass("disabled");
				$("#sectionSelectMenu .dropdown-menu").html("");
		case 5: $("#filterSelect").val(-1);
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

function getCourseSectionData() {
	var edOrgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	DashboardProxy.load('populationCourseSection', instHierarchy[edOrgIndex].schools[schoolIndex].id, function(panel) {	
	    courseSectionData = DashboardProxy.getData('populationCourseSection')['root'];
	});
}

function populateInstHierarchy() {
	
	$("#viewSelection").hide();
	clearSelections("edOrg");
	DashboardUtil.setDropDownOptions("edOrg", null,  instHierarchy, "name", "", true, function() {
		clearSelections("school");
		populateSchoolMenu();
	});
}

function populateSchoolMenu() {
	var edOrgIndex = $("#edOrgSelect").val();
	clearSelections("school");
	DashboardUtil.setDropDownOptions("school", null, instHierarchy[edOrgIndex].schools, "nameOfInstitution", "", true, function() {
		clearSelections("course");
		getCourseSectionData();
		populateCourseMenu();
	});
}

function populateCourseMenu(){
	var edOrgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	clearSelections("course");
	DashboardUtil.setDropDownOptions("course", null, courseSectionData, "courseTitle", "", true, function() {
		clearSelections("section");
		populateSectionMenu();
	});
}

function populateSectionMenu(){
	
	var edOrgIndex = $("#edOrgSelect").val();
	var schoolIndex = $("#schoolSelect").val();
	var courseIndex = $("#courseSelect").val();
	clearSelections("section");
	DashboardUtil.setDropDownOptions("section", null, courseSectionData[courseIndex].sections, "sectionName", "", false, function() {
		getStudentListData();
	});
}

function populateFilter() {
    var defaultOptions = {"-1": "No Filter"};
	DashboardUtil.setDropDownOptions("filter", defaultOptions, DashboardProxy.getWidgetConfig("lozenge").items, "description", "", true, function() {
		clearStudentList();
		filterStudents();
	});
}

function populateView(panelConfigItems) {
	  $("#viewSelection").show();
	  DashboardUtil.setDropDownOptions("view", null, panelConfigItems, "name", "", false, function() {
		  clearStudentList();
		  printStudentList();
	  });
} 