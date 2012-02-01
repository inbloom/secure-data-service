function populateSchoolMenu(){
    var y = "<select id=\"schoolSelect\" onChange=\"populateCourseMenu(this.value)\">"
    y += "<option value=\"-1\"></option>"
    var i = 0;
    for(i = 0;i<schools.length;i++){
        y += "<option value=\"" +i +"\">"+ schools[i].nameOfInstitution + "</option>"
    }
    y += "</select>"
    document.getElementById("schoolDiv").innerHTML = y;
}

function populateCourseMenu(schoolIndex){
    var temp = schools[schoolIndex].courses
 
    var y = "<select id=\"courseSelect\" onChange=\"populateSectionMenu("+schoolIndex+",this.value)\">"
    y += "<option value=\"\"></option>"
    var j = 0;
    for(j = 0;j < temp.length;j++){
        y += "<option value=\"" +j +"\">"+ temp[j].course + "</option>"
    }
    document.getElementById("courseDiv").innerHTML = y
}

function populateSectionMenu(schoolIndex, courseIndex){
    var temp = schools[schoolIndex].courses[courseIndex].sections
    var y = "<select id=\"sectionSelect\" onChange=\"printStudentList("+schoolIndex+","+courseIndex+", this.value)\">"
    y += "<option value=\"\"></option>"
    var i = 0
    for(;i < temp.length;i++){
        y += "<option value=\"" +i +"\">"+ temp[i].sectionName + "</option>"
    }
    y += "</select>"
    document.getElementById("sectionDiv").innerHTML = y
}

function printStudentList(schoolIndex, courseIndex, sectionIndex){
    var i = 0;
    var temp = schools[schoolIndex].courses[courseIndex].sections[sectionIndex].studentUIDs; 
    // This is going to change when we figure out what the API should be. 
    var studentUIDs = temp.join(',');
    var studentContentUrl = "studentlistcontent?population=" + studentUIDs + "&username=" + "${username}"; 
    $("#studentDiv").load(studentContentUrl);
}
