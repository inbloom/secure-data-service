<#-- widget that displays an assessment data point, with color depending on performance level -->
<#-- Used by: student list -->
<#-- required objects in the model map: 
     field: config info about the data to be displayed
     assessments: contains assessment results for the list of students. Should be AssessmentResolver object
     student: a Student object
     assessmentInfo: assessment meta-data
  -->

<#-- use ColorByPerfLevel class to determine display text and color -->


<#--${assessments.get(dataPointId, student)} -->

<#assign colorByPerfLevel = widgetFactory.createColorByPerfLevel(field, student, assessments)>

<span class="perfLevel${colorByPerfLevel.getColorIndex()}">${colorByPerfLevel.getText()}</span>
