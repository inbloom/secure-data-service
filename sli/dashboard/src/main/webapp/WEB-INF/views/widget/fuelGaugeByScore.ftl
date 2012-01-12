<#-- widget that displays an assessment data point as a series of rectangles, with color depending on performance level, 
     and rectangles representing the score -->
<#-- Used by: student list -->
<#-- required objects in the model map: 
     field: config info about the data to be displayed
     assessments: contains assessment results for the list of students. Should be AssessmentResolver object
     student: a Student object
     widgetFactory
  -->

<#assign fuelGaugeByScore = widgetFactory.createFuelGaugeByScore(field, student, assessments)>

<#assign perfLevel = "perfLevel${fuelGaugeByScore.getColorIndex()}">
<#assign text = "${fuelGaugeByScore.getText()}">

<#assign id = "${field.getValue()}.${student.getUid()}">

<#if fuelGaugeByScore.getCutpoints()?? && fuelGaugeByScore.getScore()??>
 
    <span class="${perfLevel}">
      <span class="fuelGaugeLabel">${text}</span>
      <span id="${id}" class="fuelGauge"></span>
    </span>
 
    <script>
       <#assign cutpoints = fuelGaugeByScore.getCutpoints()>

       var cutpoints = [ 
           <#list cutpoints as cutpoint>
             ${cutpoint}<#if cutpoint_has_next>,</#if>
           </#list>  
                       ];

       var score = ${fuelGaugeByScore.getScore()};
       var fuelGaugeWidget = new FuelGaugeWidget("${id}", score, cutpoints);
       fuelGaugeWidget.create();
    </script>

<#else>
<!-- Cannot display fuel gauge widget for assessments without cutpoints or scores -->
</#if>
