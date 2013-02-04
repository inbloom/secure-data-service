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
 <#assign id = getDivId(panelConfig.id)>
<div id="LOS${id}">
     <div id="listOfStudents">
       <div class="viewDiv menuBox">
            <h4> View </h4>
            <input type='hidden' value='' class='viewSelect' />
            <div class="btn-toolbar">
                <div class="btn-group viewSelectMenu">
                    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                        <span class='optionText'> </span>
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                    </ul>
                </div>
            </div>
        </div>
        <div class="filterDiv menuBox">
            <h4> Filter </h4>
            <input type='hidden' value='' class='filterSelect' />
            <div class="btn-toolbar">
                <div class="btn-group filterSelectMenu">
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
    </div>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/panels/SLC.studentList.js"></script>
    <script type="text/javascript">
        SLC.studentList.create('${id}');
    </script>
</div>
