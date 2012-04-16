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
<script type="text/javascript" >
function populateInstHierarchy(){
    var y = "<select id='edOrgSelect' onChange='clearStudentList();populateSchoolMenu()'>"
    y += "<option value='-1'></option>"
    var i = 0;
    for(i = 0;i<instHierarchy.length;i++){
        y += "<option value='" +i +"'>"+ instHierarchy[i].name + "</option>"
    }
    y += "</select>"
    document.getElementById("edorgDiv").innerHTML = y;
}

function populateSchoolMenu(){
    var edorgSelect = document.getElementById("edOrgSelect");
    var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
    var y = '';
    document.getElementById("courseDiv").innerHTML = '';
    document.getElementById("sectionDiv").innerHTML = '';
    if( edorgIndex > -1 ) {
        var temp = instHierarchy[edorgIndex].schools
    
        var y = "<select id='schoolSelect' onChange='clearStudentList();populateCourseMenu()'>"
        y += "<option value='-1'></option>"   
        var i = 0;
        for(i = 0;i<temp.length;i++){
            y += "<option value='" +i +"'>"+ temp[i].nameOfInstitution + "</option>"
        }
        y += "</select>"
    }
    document.getElementById("schoolDiv").innerHTML = y;
}

function populateCourseMenu(){
    var edorgSelect = document.getElementById("edOrgSelect");
    var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
    var schoolSelect = document.getElementById("schoolSelect");
    var schoolIndex = schoolSelect.options[schoolSelect.selectedIndex].value;
    var y = '';
    document.getElementById("sectionDiv").innerHTML = '';
    if( schoolIndex > -1) {
        var temp = instHierarchy[edorgIndex].schools[schoolIndex].courses
     
        y = "<select id='courseSelect' onChange='clearStudentList();populateSectionMenu()'>"
        y += "<option value='-1'></option>"
        var j = 0;
        for(j = 0;j < temp.length;j++){
            y += "<option value='" +j +"'>"+ temp[j].courseTitle + "</option>"
        }
    }
    document.getElementById("courseDiv").innerHTML = y
}

function populateSectionMenu(){
    var edorgSelect = document.getElementById("edOrgSelect");
    var schoolSelect = document.getElementById("schoolSelect");
    var courseSelect = document.getElementById("courseSelect");
    var edorgIndex = edorgSelect.options[edorgSelect.selectedIndex].value;
    var schoolIndex = schoolSelect.options[schoolSelect.selectedIndex].value;
    var courseIndex = courseSelect.options[courseSelect.selectedIndex].value;
    var y = '';
    if( courseIndex > -1) {
        var temp = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections
        y = "<select id='sectionSelect' onChange='clearStudentList();printStudentList();populateFilter()'>"
        y += "<option value='-1'></option>"
        var i = 0
        for(;i < temp.length;i++){
            y += "<option value='" +i +"'>"+ temp[i].sectionName + "</option>"
        }
        y += "</select>"
    }
    document.getElementById("sectionDiv").innerHTML = y;
}
function populateView() {
    var gridId = 'listOfStudents';
    var panelConfig = config[gridId];
    var select = "<select id='viewSelect' onChange='clearStudentList();printStudentList()'>";
    var index=0;
    for(index=0;index<panelConfig.items.length;index++) {
        select += "<option value='"+index+"'>"+panelConfig.items[index].name+"</option>";
    }
    select += "</selection>";
    document.getElementById("viewDiv").innerHTML = select;
} 

function populateFilter() {
    
    var select = "<select id='filterSelect' onChange='clearStudentList();filterStudents()'>";
    var index=0;
    select += "<option value='-1'></option>"
    for(index=0;index<DashboardUtil.widgetConfig.lozenge.items.length; index++) {
        select += "<option value='"+index+"'>"+DashboardUtil.widgetConfig.lozenge.items[index].name+"</option>";
    }
    select += "</select>";
    document.getElementById("filterDiv").innerHTML = select;
    
}

function filterStudents() {
    var filterSelect = document.getElementById("filterSelect");
    filterStudentList(DashboardUtil.widgetConfig.lozenge.items[filterSelect.selectedIndex-1]); 
}

</script>
<script type="text/javascript">
var instHierarchy=dataModel['userEdOrg']['root'];
    populateView();
    populateInstHierarchy();
</script>