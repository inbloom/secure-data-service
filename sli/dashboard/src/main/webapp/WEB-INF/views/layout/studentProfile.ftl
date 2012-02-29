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
        
    <div id="tabs">
        
      <#-- tabs -->
      <ul>
        <#list pageMap?keys as key>
          <li><a href="#tabs-${key_index}">${key}</a></li>
        </#list>
      </ul>
        
      <#-- tab content -->
      <#list pageMap?keys as key>
        <div id="tabs-${key_index}">
                    
          <#-- panels -->                    
          <#list pageMap[key] as panelId>
            <div class="panel">
              <#include "../panel/" + panelId + ".ftl">
            </div>
          </#list>
                    
        </div>
      </#list>
            
    </div>
  </div>
</div>

</body>
</html>
