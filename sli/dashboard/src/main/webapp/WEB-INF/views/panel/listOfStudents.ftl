 <@includePanelModel panelId="listOfStudents"/>
 <#assign id = getDivId(panelConfig.id)>
    <div id="viewDiv" class="menuBox">
        <input type='hidden' value='' id ='viewSelect' /> 
        <div class="btn-toolbar">
            <div class="btn-group" id="viewSelectMenu">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    <span class='optionText'> </span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                </ul>
            </div>
        </div>
  </div>
   <div id="listOfStudents">
  <div id="filterDiv" class="menuBox">
        <h4> Filter </h4>
        <input type='hidden' value='' id ='filterSelect' /> 
        <div class="btn-toolbar">
            <div class="btn-group" id="filterSelectMenu">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    <span class='optionText'> </span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                </ul>
            </div>
        </div>
    </div>
</div>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
    <div id="losError" class="hidden"></div>
  </div>
<#if minifyJs?? && minifyJs= false>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/ListOfStudent.js"></script>
</#if>

<script type="text/javascript">
    function getTableId() {
        return '${id}';
    }
</script>