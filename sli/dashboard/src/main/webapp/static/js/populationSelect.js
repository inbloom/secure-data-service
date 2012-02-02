// This should get cleaned up. 
// Put these under its onwn namespace, at least. 

function populateInstHeirarchy(){
    var y = "<select id=\"edOrgSelect\" onChange=\"populateSchoolMenu(this.value)\">"
    y += "<option value=\"-1\"></option>"
    var i = 0;
    for(i = 0;i<instHeirarchy.length;i++){
        y += "<option value=\"" +i +"\">"+ instHeirarchy[i].name + "</option>"
    }
    y += "</select>"
    document.getElementById("edorgDiv").innerHTML = y;
}

function populateSchoolMenu(edorgIndex){
    var temp = instHeirarchy[edorgIndex].schools

    var y = "<select id=\"schoolSelect\" onChange=\"populateCourseMenu("+edorgIndex+",this.value)\">"
    y += "<option value=\"-1\"></option>"
    var i = 0;
    for(i = 0;i<temp.length;i++){
        y += "<option value=\"" +i +"\">"+ temp[i].nameOfInstitution + "</option>"
    }
    y += "</select>"
    document.getElementById("schoolDiv").innerHTML = y;
}

function populateCourseMenu(edorgIndex,schoolIndex){
    var temp = instHeirarchy[edorgIndex].schools[schoolIndex].courses
 
    var y = "<select id=\"courseSelect\" onChange=\"populateSectionMenu("+edorgIndex+","+schoolIndex+",this.value)\">"
    y += "<option value=\"\"></option>"
    var j = 0;
    for(j = 0;j < temp.length;j++){
        y += "<option value=\"" +j +"\">"+ temp[j].course + "</option>"
    }
    document.getElementById("courseDiv").innerHTML = y
}

function populateSectionMenu(edorgIndex,schoolIndex, courseIndex){
    var temp = instHeirarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections
    var y = "<select id=\"sectionSelect\" onChange=\"printStudentList("+edorgIndex+","+schoolIndex+","+courseIndex+", this.value)\">"
    y += "<option value=\"\"></option>"
    var i = 0
    for(;i < temp.length;i++){
        y += "<option value=\"" +i +"\">"+ temp[i].sectionName + "</option>"
    }
    y += "</select>"
    document.getElementById("sectionDiv").innerHTML = y
}

function printStudentList(edorgIndex,schoolIndex, courseIndex, sectionIndex){
    var i = 0;
    var temp = instHeirarchy[edorgIndex].schools[schoolIndex].courses[courseIndex].sections[sectionIndex].studentUIDs; 
    // This is going to change when we figure out what the API should be. 
    var studentUIDs = temp.join(',');
    var studentContentUrl = "studentlistcontent?population=" + studentUIDs + "&username=" + "${username}"; 
    $("#studentDiv").load(studentContentUrl);
}
