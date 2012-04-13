<@includePanelModel panelId="populationWidget"/>
<#assign id = getDivId(panelConfig.id)>
<div id="populationSelect">
    <span id="edorgDiv"></span>
    <span id="schoolDiv"></span>
    <span id="courseDiv"></span>
    <span id="sectionDiv"></span>
</div>
<script type="text/javascript" >
function populateInstHierarchy(){
    var y = "<select id=\"edOrgSelect\" onChange=\"clearStudentList();populateSchoolMenu(this.value)\">"
    y += "<option value=\"-1\"></option>"
    var i = 0;
    for(i = 0;i<instHierarchy.length;i++){
        y += "<option value=\"" +i +"\">"+ instHierarchy[i].name + "</option>"
    }
    y += "</select>"
    document.getElementById("edorgDiv").innerHTML = y;
}

function populateSchoolMenu(edorgIndex){
    var temp = instHierarchy[edorgIndex].schools

    var y = "<select id=\"schoolSelect\" onChange=\"clearStudentList();populateCourseMenu("+edorgIndex+",this.value)\">"
    y += "<option value=\"-1\"></option>"   
    var i = 0;
    for(i = 0;i<temp.length;i++){
        y += "<option value=\"" +i +"\">"+ temp[i].nameOfInstitution + "</option>"
    }
    y += "</select>"
    document.getElementById("schoolDiv").innerHTML = y;
}

function populateCourseMenu(edorgIndex,schoolIndex){
    var temp = instHierarchy[edorgIndex].schools[schoolIndex].courses
 
    var y = "<select id=\"courseSelect\" onChange=\"clearStudentList();populateSectionMenu("+edorgIndex+","+schoolIndex+",this.value)\">"
    y += "<option value=\"\"></option>"
    var j = 0;
    for(j = 0;j < temp.length;j++){
        y += "<option value=\"" +j +"\">"+ temp[j].courseTitle + "</option>"
    }
    document.getElementById("courseDiv").innerHTML = y
}

function populateSectionMenu(edorgIndex,schoolIndex, courseIndex){
    var temp = instHierarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections
    var y = "<select id=\"sectionSelect\" onChange=\"clearStudentList();printStudentList(instHierarchy["+edorgIndex+"].schools["+schoolIndex+"].courses["+courseIndex+"].sections,this.value)\">"
    y += "<option value=\"\"></option>"
    var i = 0
    for(;i < temp.length;i++){
        y += "<option value=\"" +i +"\">"+ temp[i].sectionName + "</option>"
    }
    y += "</select>"
    document.getElementById("sectionDiv").innerHTML = y
}



</script>
<script type="text/javascript">
var instHierarchy=dataModel['userEdOrg']['root'];
    populateInstHierarchy();
</script>