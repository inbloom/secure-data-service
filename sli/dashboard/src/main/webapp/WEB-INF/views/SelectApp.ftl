<html>
<body>
<h2>${message}</h2>
<#list appToUrl?keys as key>
 <li><a href = ${appToUrl[key]}> ${key} </a></li>
 </#list>
</body>
</html>