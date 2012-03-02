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
      SLI Dashboard - Student Profile
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
    <#list viewConfigs as tab>
      <li><a href="#tabs-${tab_index}">${tab.name}</a></li>
    </#list>
      
    </ul>
      
    <#-- create tab pages -->
    <#list viewConfigs as tab>
      <div id="tabs-${tab_index}">
      
      <#-- create panels -->
      <#list tab.getDisplaySet() as panel>
        
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
