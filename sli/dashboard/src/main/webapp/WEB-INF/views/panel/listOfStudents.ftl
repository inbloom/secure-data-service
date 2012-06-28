 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
    <div id="losError" class="hidden"></div>
  </div>
  
<#if minifyJs?? && minifyJs= false>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/panels/SLC.studentList.js"></script>
	<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/panels/SLC.population.js"></script>
</#if>

<script type="text/javascript">
    SLC.util.setTableId('${id}');
    SLC.population.populateInstHierarchy();
</script>
