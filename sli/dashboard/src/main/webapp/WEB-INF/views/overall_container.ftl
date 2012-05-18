<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<#attempt>
<html>
<head>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', ${googleAnalyticsTrackerId}]);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
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