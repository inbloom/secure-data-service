<html>
<head>
<link rel="stylesheet" type="text/css" href="/dashboard/static/css/common.css" media="screen" />
<link rel="stylesheet" type="text/css" href="/dashboard/static/css/profile.css" media="screen" />
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
        <#list panelIds as panelId>
            <div class="panel">
            <#include "../panel/" + panelId + ".ftl">
            </div>
        </#list>
    </div>
</div>

</body>
</html>
