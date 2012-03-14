<html>
<head>
<#include "layout_includes.ftl">
<#assign layoutConfig = viewConfigs>
<script>
    $(document).ready( function() {
        DashboardUtil.makeTabs("#tabs");
    });
</script>
</head>
<body>

<div id="container">

  <div id="header">
      <#include "../header.ftl">
  </div>
    
  <div id="banner">
    <h1>
      SLI Dashboard
    </h1>
  </div>
    
  <div id="content">
    <#-- create header panels -->
    <#list layout as item>     
      <#if item.type == "PANEL">
        <div class="panel">
          <#include "../panel/" + item.id + ".ftl">
        </div>
      </#if>
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
         <#if panel.type == "PANEL">
          <div class="panel">
            <#include "../panel/" + panel.id + ".ftl">
          </div>
         </#if>        
        </#list>
        </div>
       </#if>
        
    </#list>
     
  </div>
</div>

</body>
</html>
