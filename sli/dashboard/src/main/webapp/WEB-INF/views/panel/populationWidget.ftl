<@includePanelModel panelId="populationWidget"/>
<#assign id = getDivId(panelConfig.id)>
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
<div id="viewSelection">
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
</div>
