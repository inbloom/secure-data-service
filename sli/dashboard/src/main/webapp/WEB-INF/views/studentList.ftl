<html>
<head>
<script type="text/javascript" src="static/js/3p/jquery-1.7.1.js"></script>
<script type="text/javascript">
var schools = ${schoolList};

$.ajaxSetup ({cache: false});

function populateSchoolMenu(){
    var y = "<select id=\"schoolSelect\" onChange=\"populateCourseMenu(this.value)\">"
    y += "<option value=\"-1\"></option>"
    var i = 0;
    for(i = 0;i<schools.length;i++){
        y += "<option value=\"" +i +"\">"+ schools[i].schoolId + "</option>"
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
        y += "<option value=\"" +i +"\">"+ temp[i].section + "</option>"
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
</script>
<link rel="stylesheet" type="text/css" href="static/css/common.css" media="screen" />
</head>
<body onLoad="populateSchoolMenu()">
<div id="container">

    <div id="header">
		<#include "header.ftl">
    </div>
    
    <div id="banner">
        <h1>
            SLI Dashboard - List of Students
        </h1>
    </div>
    <div id="content">
    
        <div id="populationSelect">
            <span id="schoolDiv"></span><span id="courseDiv"></span><span id="sectionDiv"></span>
        </div>
    
        <div id="listView">
            <span id="studentDiv"></span>
        </div>
    </div>
    
</div>

</body>
</html>
