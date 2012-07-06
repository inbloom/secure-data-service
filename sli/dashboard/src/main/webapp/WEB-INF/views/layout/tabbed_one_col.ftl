<#include "layout_includes.ftl">
<#assign layoutConfig = viewConfigs>
<script>
    $(document).ready( function() {
    	var checkTab = SLC.dataProxy.checkTabPanel();
    	if (checkTab) {
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
     </div>
  </div>
</div>
