<#-- The body of List of Student view -->
<#-- Used by: StudentListTableController.studentListTable() -->
<#-- required objects in the model map: 
     viewConfig: the view config for the list of students. Should be ViewConfig object
     assessments: contains assessment information for the list of students. Should be AssessmentResolver object
     students: contains the list of students to be displayed. Should be List<Student> object. 
  -->

<table> 

<tr><th>First Name</th><th>Last Name</th></tr>

<#list students as student>
  <tr><td>${student.getFirstName()}</td><td>${student.getLastName()}</td></tr>
</#list>

</table>


