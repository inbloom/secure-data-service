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
<#include "layout_includes.ftl">
<#assign layoutConfig = viewConfigs>
<script>
    $(document).ready( function() {
    	if (SLC.dataProxy.checkTabPanel()) {
        	SLC.util.makeTabs("#tabs");
        }
    });
</script>
<div id="container">

  <div id="content">
    <#-- create header panels -->
    <#list layout as item>
      <@includePanelContent panel=item/>
    </#list>
	<#if entityId?? >
    <#-- create tab div -->
    <div id="tabs">
	    <ul>

	    <#-- create individual tabs -->
	     <#list layout as item>
	      <#if item.type == "TAB">
	      <li><a href="#page-${item.id}">${item.name}</a></li>
	      </#if>
	    </#list>

	    </ul>

	    <#-- create pages -->
	   <#list layout as item>
	      <#if item.type == "TAB">
	        <div id="page-${item.id}">
		        <#-- create panels -->
		        <#list item.items as panel>

		          <div class="panel">
		              <#if viewConfigs[panel.id].name??>
			          <div class="panel-header">
			            <h7>${viewConfigs[panel.id].name}</h7>
			          </div>
			          </#if>
			          <div class="panel-content">
			          <@includePanelContent panel=panel/>
			          </div>
		          </div>
		        </#list>
	        </div>
	       </#if>

	    </#list>
	 </#if>
     </div>
  </div>
</div>
