<#-- widget that displays a count of a field in a series of objects -->
<#-- Used by: student list -->
<#-- required objects in the model map:
     field: config info about the data to be displayed
     attendances: contains attendance results for the list of students. Should be AttendanceResolver object
     student: a Student object
     widgetFactory
  -->

<#assign fieldCounter = widgetFactory.createFieldCounter(field, student, attendances, [0, 5, 10, 11])>
<span class="countLevel${fieldCounter.getColorIndex()}">${fieldCounter.getText()}</span>
