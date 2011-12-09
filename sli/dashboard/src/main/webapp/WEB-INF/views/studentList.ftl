<html>
<head>
<script type="text/javascript">

var dude = "<table><tr><th>id</th><th> sex</th><th> First Name</th></tr>"
            +"<#list listOfStudents as stud><tr><td>${stud_index}</td><td> ${stud.firstName}</td><td> ${stud.lastName}</td></tr> </#list></table> ";
var student = ${damnIt};
var schools = ${schoolList};
function loadMenuItem(){
alert("first");
var i = 0;
var x = "<table><tr><th> Last Name</th><th> First Name</th></tr>";
for(i=0;i<student.length;i++){
x += "<tr><td>" + student[i].lastName + "</td><td>" +  student[i].firstName + "</td></tr>";
} 
x += "</table>";
document.getElementById("failedDiv").innerHTML = x;
}

function populateSchoolMenu(){
var y = "<select onChange=\"populateCourseMenu(this.value)\">"
y += "<option value=\"something\"></option>"
var i = 0;
for(i = 0;i<schools.length;i++){
    y += "<option value=\"" +i +"\">"+ schools[i].id + "</option>"
}
y += "</select>"
document.getElementById("div1").innerHTML = y;
}


function populateCourseMenu(a){
var x = ""
 var temp = schools[a].courses
 
var y = "<select onChange=\"populateSectionMenu("+a+",this.value)\">"
y += "<option value=\"something\"></option>"
var j = 0;
 for(j = 0;j < temp.length;j++)
    y += "<option value=\"" +j +"\">"+ temp[j].category + "</option>"

x += "<h3>"+ temp.courses+"</h3>"
document.getElementById("div2").innerHTML = y
}

function populateSectionMenu(a,b){
var temp = schools[a].courses[b].sections
var y = "<select onChange=\"printStudentList("+a+","+b+", this.value)\">"
y += "<option value=\"something\"></option>"
var i = 0
for(;i < temp.length;i++){
    y += "<option value=\"" +i +"\">"+ temp[i].name + "</option>"
}
y += "</select>"
document.getElementById("div3").innerHTML = y
}

function printStudentList(a, b, c){
var i = 0;
var x = "<table><tr><th> Last Name</th><th> First Name</th></tr>";
temp = schools[a].courses[b].sections[c].studentList 
for(;i<temp.length;i++){
x += "<tr><td>" + temp[i].lastName + "</td><td>" +  temp[i].firstName + "</td></tr>";
} 
x += "</table>";
document.getElementById("failedDiv").innerHTML = x;
}

</script>
</head>
<body>
<h1>Student List</h1>

<button type="button" onclick="populateSchoolMenu()"> Show Menu</button>
<div>
<span id="div1"></span><span id="div2"></span><span id="div3"></span>
</div>
<div>
<span id="failedDiv">
</span>
</div>

</body>
</html>
