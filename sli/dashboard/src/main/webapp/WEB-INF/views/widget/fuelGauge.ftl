<#-- widget that displays an assessment data point as a series of rectangles, with color depending on performance level, 
     and rectangles representing the score -->
<#-- Used by: student list -->
<#-- required objects in the model map: 
     field: config info about the data to be displayed
     assessments: contains assessment results for the list of students. Should be AssessmentResolver object
     student: a Student object
     widgetFactory
  -->

<#assign fuelGauge = widgetFactory.createFuelGauge(field, student, assessments)>

<#assign perfLevel = "perfLevel${fuelGauge.getColorIndex()}">
<#assign text = "${fuelGauge.getText()}">

<#assign id = "${field.getValue()}.${student.getId()}">

<#if fuelGauge.getCutpoints()?? && fuelGauge.getScore()??>
 
    <#-- display fuel gauge for assessments with cutpoints and score -->

    <span class="${perfLevel}">
      <span class="fuelGaugeLabel">${text}</span>
      <span id="${id}" class="fuelGauge"></span>
    </span>
 
    <script>
       <#assign cutpoints = fuelGauge.getCutpoints()>

       var cutpoints = [ 
           <#list cutpoints as cutpoint>
             ${cutpoint}<#if cutpoint_has_next>,</#if>
           </#list>  
                       ];

       var score = ${fuelGauge.getScore()};
       var fuelGaugeWidget = new FuelGaugeWidget("${id}", score, cutpoints);
       fuelGaugeWidget.create();
    </script>

<#elseif fuelGauge.getNumRealPerfLevels()?? && fuelGauge.getPerfLevel()??>

    <#-- display fuel gauge for assessments with performance level only -->

    <span class="${perfLevel}">
      <span class="fuelGaugeLabel">${text}</span>
      <span id="${id}" class="fuelGauge"></span>
    </span>
 
    <script>
       var cutpoints = [ 
           <#list 0..fuelGauge.getNumRealPerfLevels() as i>
             ${i+1}<#if i < fuelGauge.getNumRealPerfLevels()>,</#if>
           </#list>  
                       ];

       var score = ${fuelGauge.getPerfLevel()} + 1;
       var fuelGaugeWidget = new FuelGaugeWidget("${id}", score, cutpoints);
       fuelGaugeWidget.create();
    </script>


<#else>
<!-- Cannot display fuel gauge widget for assessments without cutpoints and score, or performance level -->
</#if>
