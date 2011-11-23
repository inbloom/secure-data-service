<html>
<body>
<h1>Student List</h1>
<table>
<tr>
<th>id</th><th> sex</th><th> First Name</th>
</tr>
<#list listOfStudents as stud>
<tr>
<td>${stud_index}</td><td> ${stud.sex}</td><td> ${stud.name_first}</td>
</tr>
 </#list>
</table>
</body>
</html>
