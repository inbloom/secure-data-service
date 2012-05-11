<@includePanelModel panelId="populationWidget"/>
<#assign id = getDivId(panelConfig.id)>
<div id="populationSelect">
    <span id="edorgDiv"></span>
    <span id="schoolDiv"></span>
    <span id="courseDiv"></span>
    <span id="sectionDiv"></span>
    <br>
    <p><span id="viewDiv"></span>   <span id="filterDiv"></span></p>
</div>
<Script> 
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
                for(i=0; i < edOrgLength; i++) {
                    if(instHierarchy[i].name == selectedPopulation.name) {
                        $("#edOrgSelect").val(i);
                        edOrgIndex = i
                        populateSchoolMenu();
                        break;
                    }
                }
            } 
            
            var schoolsLength = instHierarchy[edOrgIndex].schools.length;
            schoolIndex = schoolsLength == 1 ? 0 : -1;
            if (edOrgIndex > -1 && schoolsLength > 1) {
               for(i=0; i < schoolsLength; i++) {
                    if(instHierarchy[edOrgIndex].schools[i].id == selectedPopulation.section.schoolId) {
                        $("#schoolSelect").val(i);
                        schoolIndex = i;
                        populateCourseMenu();
                        break;
                    }
                } 
            }
            
            var coursesLength =  instHierarchy[edOrgIndex].schools[schoolIndex].courses.length;
            courseIndex = coursesLength == 1 ? 0 : -1;
            if (edOrgIndex > -1 && schoolIndex > -1 && coursesLength > 1) {
                for(i=0; i < coursesLength; i++) {
                    if(instHierarchy[edOrgIndex].schools[schoolIndex].courses[i].id == selectedPopulation.section.courseId) {
                        $("#courseSelect").val(i);
                        courseIndex = i;
                        populateSectionMenu();
                        break;
                    }
                } 
            }
            
            var sectionsLength = instHierarchy[edOrgIndex].schools[schoolIndex].courses[courseIndex].sections.length;
            sectionIndex = sectionsLength == 1 ? 0 : -1;
            if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionsLength > 0) { // No autoselection for section
                for(i=0; i < sectionsLength; i++) {
                    if(instHierarchy[edOrgIndex].schools[schoolIndex].courses[courseIndex].sections[i].id == selectedPopulation.section.id) {
                        $("#sectionSelect").val(i);
                        sectionIndex = i;
                        break;
                    } 
                }
            }
            
            if (edOrgIndex > -1 && schoolIndex > -1 && courseIndex > -1 && sectionIndex > -1) {
                DashboardProxy.load("listOfStudents", selectedPopulation.section.id, function(panel) {
                    populateView(panel.viewConfig);
                    populateFilter();
                    printStudentList();
                });
            }
        });
   }
    
</Script>