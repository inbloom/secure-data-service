<table> 

<tr><th>First Name</th><th>Last Name</th></tr>

<#list students as student>
  <tr><td>${student.getFirstName()}</td><td>${student.getLastName()}</td></tr>
</#list>

</table>
