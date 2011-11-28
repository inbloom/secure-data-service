<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="/databrowser/resources/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript">
function add(){
	var schoolfullname=$("#schoolfullname").val();
	var format=$("#format").val();
	var query="?action=add&format="+format+"&schoolfullname="+encodeURIComponent(schoolfullname);
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
	var schoolfullname=$("#"+id+"_schoolfullname").val();
	var query="?action=update&format="+format+"&id="+id+"&schoolfullname="+schoolfullname;
	window.location=query;
}

function view_association(id) {
	var query="studentschoolassociationmenu.html?schoolId=" + id;
	window.location = query;
}

</script>

<style type="text/css" >

body {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
}

#schooltable {
	padding:2px;
	text-align:center;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14px;
	font-weight: normal;
	border: 2px solid #000000;
	margin-top: 10px;
}
.schoolfullname, .shortname{
	height: auto;
	width: auto;
	padding: 5px;
	border: 1px solid #000000;
}

input{
	padding: 3px;
	width:300px;
	text-align: center;
	border: 1px solid #ffffff;
	vertical-align: middle;
}
#schoolfullname,#shortname{
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
	<title>School Menu</title>
</head>
<body>
<h1> 
	School Menu
</h1>
<h2 id="error">${errorMessage}</h2>
<table border="0">
<tr>
	<td><button type="button" id="listSchools" onClick="list_click()">List All Schools</button></td>
	<td width="350px">&nbsp;</td>
	<td style='font-size:14px'><b>Select Format&nbsp;&nbsp;</b><select id="format"><option id="jsonOption" value="json" ${ (format=="json") ? "selected='selected'" : "" }>JSON</option><option id="xmlOption" value="xml" ${(format=="xml")?"selected='selected'":"" }>XML</option></select></td>
	<td width="50px">&nbsp;</td>
	<td style='font-size:14px'><b>Total Schools:&nbsp;&nbsp;</b><span id="numSchools">${schoolNumber}</span></td>
</tr>
</table>

<table id="schooltable">

<tr>
	<th class="schoolfullname">Full Name</th><th class="id">School ID</th><th class="action">Action</th>
</tr>

<c:forEach items="${schools}" var="school">
<tr class="schoolRecord">
	<td class="schoolfullname"><input id="${school.schoolId}_schoolfullname" value="${school.fullName}"></input></td>
	<td class="id">${school.schoolId}</td>
	<td class="action">
		<a onClick="delete_click('${school.schoolId}')" href="#">Delete</a>
		/ <a onClick="update_click('${school.schoolId}')" href="#">Update</a>
		/ <a onClick="view_association('${school.schoolId}')" href="#">Associations</a>
	</td>
</tr>
</c:forEach>

<tr id="newSchoolFields">
	<td class="schoolfullname"><input id="schoolfullname"></input></td>
	<td class="id">&nbsp;</td><td class="action"><a id="addNewSchool" onClick="add()" href="#">Add</a></td>
</tr>

</table>

<br>
<br>
<div id="siteNav">
	<button id="navHome" type="button" onClick="window.location.href='/databrowser'">Back to Home</button>
</div>
</body>
</html>