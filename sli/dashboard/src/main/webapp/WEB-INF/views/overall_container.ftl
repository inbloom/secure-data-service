<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<#attempt>
<html>
<head>
</head>
<body>
<#include "layout/layout_header.ftl">
<#include "${page_to_include}">


<#include "layout/layout_footer.ftl">
</body>
</html>
<#recover>
${logger.error(.error)}
<#include "layout/layout_header.ftl">
<#include "error.ftl">
<#include "layout/layout_footer.ftl">
</#attempt>