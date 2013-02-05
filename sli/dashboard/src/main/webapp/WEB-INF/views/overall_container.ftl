<#--
  Copyright 2012-2013 inBloom, Inc. and its affiliates.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#attempt>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/js/libs/jquery-ui/css/custom/jquery-ui-1.8.18.custom.css" media="screen" />
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/js/libs/jqGrid/css/ui.jqgrid.css" media="screen" />
<link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/css/common.css" media="screen" />

<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/libs/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/libs/jquery-ui/js/jquery-ui-1.8.18.custom.min.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/libs/bootstrap-dropdown.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/libs/jqGrid/js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/libs/raphael-min.js"></script>

<#if minifyJs?? && minifyJs= false>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/controller/SLC.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/controller/SLC.util.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/controller/SLC.dataProxy.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/SLC.loadingMask.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.tablegrid.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.repeatHeaderGrid.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.tree.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.fuelGauge.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.formatters.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.sorters.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.teardrop.js"></script>
    <script type="text/javascript" src="${CONTEXT_ROOT_PATH}/static/js/widgets/grid/SLC.grid.cutPoints.js"></script>
<#else>
    <script type="text/javascript" src = "${CONTEXT_ROOT_PATH}/static/js/SLC.js"></script>
    <script type="text/javascript" src = "${CONTEXT_ROOT_PATH}/static/js/widgets.js"></script>
</#if>

</head>
<body>
<div class="wrapper">
	<#include "layout/layout_header.ftl">
	<#include "${page_to_include}">
	<div class="clear"></div>
</div>
<#include "layout/layout_footer.ftl">

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '${googleAnalyticsTrackerId}']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
</body>
</html>
<#recover>
${logger.error(.error)}
<#include "layout/layout_header.ftl">
<#include "error.ftl">
<#include "layout/layout_footer.ftl">
</#attempt>
