<#--
  Copyright 2012-2013 inBloom, Inc. and its affiliates.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/panels/SLC.population.js"></script>
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
    <div id="dbrd_div_pw_go_btn" class="menuBox">
        <div class="btn-toolbar">
            <div class="btn-group" id="sectionSelectMenu">
               <button id="dbrd_btn_pw_go" class="btn" type="submit">Go</button>
            </div>
        </div>
    </div>
</div>
  <div class="ui-widget-no-border">
    <table id="${id}"></table>
    <div id="dsh_dv_error" class="hidden"></div>
  </div>
