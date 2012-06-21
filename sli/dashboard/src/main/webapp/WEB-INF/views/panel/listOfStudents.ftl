 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
    <div id="losError" class="hidden"></div>
  </div>
<#if minifyJs?? && minifyJs= false>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/ListOfStudent.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/populationWidget.js"></script>
<#else>
    <script type="text/javascript" src = "${CONTEXT_ROOT_PATH}/static/js/widgets.js"></script>
    <script type="text/javascript" src = "${CONTEXT_ROOT_PATH}/static/js/all.js"></script>
</#if>
<script type="text/javascript">
    function getTableId() {
        return '${id}';
    }
    var instHierarchy=SLC.dataProxy.getData('populationWidget')['root'];
    populateInstHierarchy();
</script>
