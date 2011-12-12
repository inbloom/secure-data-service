<html>
<head>
<script type="text/javascript">
var schools = ${schoolList};

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
    var temp = schools[schoolIndex].courses[courseIndex].sections[sectionIndex].students;
    var x = "<table><tr><th> Last Name</th><th> First Name</th></tr>";

    for(;i<temp.length;i++){
        x += "<tr><td>" + temp[i].lastName + "</td><td>" +  temp[i].firstName + "</td></tr>";
    } 
    x += "</table>";
    document.getElementById("studentDiv").innerHTML = x;
}
</script>
</head>
<body onLoad="populateSchoolMenu()">
<h1>Student List</h1>

<div>
<span id="schoolDiv"></span><span id="courseDiv"></span><span id="sectionDiv"></span>
</div>
<div>
<span id="studentDiv">
</span>
</div>

</body>
</html>
