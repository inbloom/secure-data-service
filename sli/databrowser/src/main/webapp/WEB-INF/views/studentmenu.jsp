<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="/databrowser/resources/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript">
function add(){
	var firstname=$("#firstname").val();
	var lastname=$("#lastname").val();
	var format=$("#format").val();
	var query="?action=add&format="+format+"&firstname="+encodeURIComponent(firstname)+"&lastname="+encodeURIComponent(lastname);
	window.location=query;	
}

function list_click(){
	var format=$("#format").val();
	var query="?action=list&format="+format;
	window.location=query;
	
}

function delete_click(id){
	var format=$("#format").val();
	var query="?action=delete&format="+format+"&id="+id;
	window.location=query;
}

function update_click(id){
	var format=$("#format").val();
	var firstname=$("#"+id+"_firstname").val();
	var lastname=$("#"+id+"_lastname").val();
	var query="?action=update&format="+format+"&id="+id+"&firstname="+firstname+"&lastname="+lastname;;
	window.location=query;
}

function view_association(id) {
	var query="studentschoolassociationmenu.html?studentId=" + id;
	window.location = query;
}

</script>

<style type="text/css" >

body {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
}

#studenttable {
	padding:2px;
	text-align:center;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-weight: normal;
	border: 2px solid #000000;
	margin-top: 10px;
}
.firstname, .lastname{
	height: auto;
	width: auto;
	padding: 5px;
	border: 1px solid #000000;
}
input{
	padding: 3px;
	width:160px;
	text-align: center;
	border: 1px solid #ffffff;
	vertical-align: middle;
}
#firstname,#lastname{
border: 1px solid #000000;
}
input:focus{
   background-color: yellow;
   border: 1px solid #000000;
   }
.id {
    height: auto;
	width: 300px;
	padding: 5px;
	text-align: center;
	border: 1px solid #000000;
}
.action {
	height: auto;
	width: 150px;
	padding: 5px;
	text-align: center;
	border: 1px solid #000000;
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	
	
}
.action a{
text-decoration: none;
}
.action a:hover{
color:red;
text-decoration: underline;
}
#error{

color:red;
}

</style>
	<title>Student Menu</title>
</head>
<body>
<h1> 
	Student Menu
</h1>
<h2 id="error">${errorMessage}</h2>
<table border="0">
<tr>
	<td><button type="button" id="listStudents" onClick="list_click()">List All Students</button></td>
	<td width="350px">&nbsp;</td>
	<td style='font-size:14px'><b>Select Format&nbsp;&nbsp;</b><select id="format"><option id="jsonOption" value="json" ${(format=="json")?"selected='selected'":"" }>JSON</option><option id="xmlOption" value="xml" ${(format=="xml")?"selected='selected'":"" }>XML</option></select></td>
	<td width="50px">&nbsp;</td>
	<td style='font-size:14px'><b>Total Students:&nbsp;&nbsp;</b><span id="numStudents">${studentNumber}</span></td>
</tr>
</table>

<table id="studenttable">
<tr>
<th class="firstname">First Name</th><th class="lastname">Last Name</th><th class="id">Student ID</th><th class="action">Action</th>
<tr>
<c:forEach items="${students}" var="student">
	<tr class="studentRecord">
		<td class="firstname"><input id="${student.studentId}_firstname" value="${student.firstName}"></input></td>
		<td class="lastname"><input id="${student.studentId}_lastname" value="${student.lastSurname}"></input></td>
		<td class="id">${student.studentId}</td>
		<td class="action">
			<a onClick="delete_click('${student.studentId}')" href="#">Delete</a>
			/ <a onClick="update_click('${student.studentId}')" href="#">Update</a>
			/ <a onClick="view_association('${student.studentId}')" href="#">Associations</a>
		</td>
	</tr>
</c:forEach>

<tr id="newStudentFields">
	<td class="firstname"><input id="firstname"></input></td><td class="lastname"><input id="lastname"></input></td><td class="id">&nbsp;</td><td class="action"><a id="addNewStudent" onClick="add()" href="#">Add</a></td>
</tr>

</table>

<br>
<br>
<div id="siteNav">
	<button id="navHome" type="button" onClick="window.location.href='/databrowser'">Back to Home</button>
</div>
</body>
</html>