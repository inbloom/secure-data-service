 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
  </br>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
  </div>
<script type="text/javascript" src="/dashboard/static/js/ListOfStudent.js">
</script>
<script type="text/javascript">
    function getTableId() {
        return '${id}';
    }
    var instHierarchy=dataModel['userEdOrg']['root'];
    populateInstHierarchy();
</script>
