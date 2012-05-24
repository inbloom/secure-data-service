 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  <div id="sli-loadingSection">
  		<div class="message">Loading....</div>
  </div>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
    <div id="losError" class="hidden"></div>
  </div>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/ListOfStudent.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/populationWidget.js"></script>
<script type="text/javascript">
    function getTableId() {
        return '${id}';
    }
    var instHierarchy=DashboardProxy.getData('populationWidget')['root'];
    populateInstHierarchy();
</script>
