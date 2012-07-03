<@includePanelModel panelId="populationWidget"/>
<#assign id = getDivId(panelConfig.id)>
<#if minifyJs?? && minifyJs= false>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/populationWidget.js"></script>
</#if>
<script type="text/javascript">
    function getTableId() {
        return '${id}';
    }
</script>

<div id="populationSelect">
    <div id="edorgDiv" class="menuBox">
        <h4> District </h4>
        <input type='hidden' value='' id ='edOrgSelect' /> 
        <div class="btn-toolbar">
            <div class="btn-group" id="edOrgSelectMenu" name="edOrgSelectMenu">
                <a class="btn dropdown-toggle" id="edOrgSelectButton" data-toggle="dropdown" href="#"> 
                    <span class='optionText'> </span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu" id="edOrgSelectOptions">
                </ul>
            </div>
        </div>
    </div>
    <div id="schoolDiv" class="menuBox">
        <h4> School </h4>
        <input type='hidden' value='' id ='schoolSelect' /> 
        <div class="btn-toolbar">
            <div class="btn-group" id="schoolSelectMenu">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" >
                    <span class='optionText'> </span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                </ul>
            </div>
        </div>
    </div>
    <div id="courseDiv" class="menuBox">
        <h4> Course </h4>
        <input type='hidden' value='' id ='courseSelect' /> 
        <div class="btn-toolbar">
            <div class="btn-group" id="courseSelectMenu">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" >
                    <span class='optionText'> </span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                </ul>
            </div>
        </div>
    </div>
    <div id="sectionDiv" class="menuBox">
        <h4> Section </h4>
        <input type='hidden' value='' id ='sectionSelect' /> 
        <div class="btn-toolbar">
            <div class="btn-group" id="sectionSelectMenu">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#" >
                    <span class='optionText'> </span>
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                </ul>
            </div>
        </div>
    </div>
    <div id="dbrd_div_pw_go_btn" class="menuBox">
        <div class="btn-toolbar">
            <div class="btn-group" id="sectionSelectMenu">
               <button id="dbrd_btn_pw_go" class="btn" type="submit">Go</button>
            </div>
        </div>
    </div>
</div>

