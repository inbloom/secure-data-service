<html>
<head>
<link rel="stylesheet" type="text/css" href="static/css/common.css" media="screen" />
</head>

<body>

<div id="container">
    <div id="header">
        <#include "header.ftl">
    </div>

    <div id="banner">
        <h1>
            ${message}
        </h1>
    </div>
</div>
<div id="selectapp">
<#list appToUrl?keys as key>
<li><a href = ${appToUrl[key]}>${key}</a></li>
 </#list>
</body>
</html>
