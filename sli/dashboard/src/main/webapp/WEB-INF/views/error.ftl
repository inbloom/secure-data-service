<html>
    <head>Error
        <link rel="stylesheet" type="text/css" href="static/css/common.css" media="screen" />
    </head>
    <body>
        <div id="container">

        <div id="header">
            <#include "header.ftl">
        </div>
    
        <div id="banner">
            <h1>
                SLI Dashboard Technical Difficulties
            </h1>
        </div>
	<div class="csi">
	   <#if debugEnabled?? && debugEnabled>
            <h3> ${errorMessage} </h3>
            <div > ${stackTrace} </div>
       <#else>
            <h3> OOPS!! This isn't looking right </h3>
            <span> Please try again and if this is the result of a second try please report this </span>
       </#if>
  		
  		<!-- TODO: return {error} var to this page -->
	</div>
</div>
</body></html>