<html>
<head>
<#include "layout_includes.ftl">

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
    
    <div class="panel">
       <#include "../panel/csi.ftl">
    </div>

  
    <#-- create tab div -->
    <div id="tabs">
    <ul>
      
    <#-- create individual tabs -->
    <#list viewConfigs as page>
      <li><a href="#page-${page_index}">${page.name}</a></li>
    </#list>
      
    </ul>
      
    <#-- create pages -->
    <#list viewConfigs as page>
      <div id="page-${page_index}">
      
      <#-- create panels -->
      <#list page.getDisplaySet() as panel>
        
        <div class="panel">
          <#include "../panel/" + panel.displayName + ".ftl">
        </div>
        
      </#list>
      
      </div>
        
    </#list>
     
  </div>
</div>

</body>
</html>
