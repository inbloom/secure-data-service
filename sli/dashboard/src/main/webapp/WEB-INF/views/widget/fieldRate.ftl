<#-- widget that displays a percentage for a field in a series of objects -->
<#-- Used by: student tardiness -->
<#-- required objects in the model map:
     field: config info about the data to be displayed
     attendances: contains attendance results for the list of students. Should be AttendanceResolver object
     student: a Student object
     widgetFactory
  -->

<#assign fieldCounter = widgetFactory.createFieldRate(field, student, attendances)>
<span class="count-${fieldCounter.getColor()}">${fieldCounter.getText()}</span>
