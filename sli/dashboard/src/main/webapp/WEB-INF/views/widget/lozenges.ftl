<#-- widget that displays an assessment data point, with color depending on performance level -->
<#-- Used by: student list -->
<#-- required objects in the model map: 
     field: config info from view config about which lozenges to be displayed
     students: contains the list of students to be displayed. Should be StudentResolver object. 
     student: a Student object
     lozengeConfigs: the lozenge display configuration for the user. Should be a lozengeConfigResolver object
  -->

<#-- use Lozenge class to determine what lozenges to display -->

<#assign lozengeWidget = widgetFactory.createLozenge(field, student, students, lozengeConfigs)>


<#if 0 < lozengeWidget.getNumLozenges()>

  <#assign maxLozengeIndx = lozengeWidget.getNumLozenges() - 1>
  
  <#list 0..maxLozengeIndx as lozengeIndx>
    <#assign lozengeConfig = lozengeWidget.get(lozengeIndx)>
  
    <#assign id = "${field.getValue()}.${student.id}.lozenge.${lozengeIndx}">
  
    <#-- drawing code -->
    <span id="${id}" class="lozenge"></span>
    <script>
      var widget = new LozengeWidget("${id}", "${lozengeConfig.getLabel()}", "${lozengeConfig.getColor()}", "${lozengeConfig.getStyle()}");
      widget.create();
    </script>
  
  
  </#list>

</#if>
