              
	<#if CONTEXT_ROOT_PATH??>
        <link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/js/3p/jquery-ui/css/custom/jquery-ui-1.8.18.custom.css" media="screen" />
        <link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/js/3p/jqGrid/css/ui.jqgrid.css" media="screen" />
        <link rel="stylesheet" type="text/css" href="${CONTEXT_ROOT_PATH}/static/css/common.css" media="screen" />
	<#else>
        <link rel="stylesheet" type="text/css" href="/dashboard/static/css/common.css" media="screen" />
	</#if>

    
	<div class="error-container"> 
	
	<div class="error-header">
		<#if errorHeading??>
			<h10>${errorHeading}</h10>
		<#else>
			<h10>ERROR</h10>
		</#if>
    </div>
    <div>      
      <p>&nbsp;</p>
      <p>&nbsp;</p>
    </div>
    
	<div class="error-content">
		<#if errorContent??>
			<h3>${errorContent}</h3> 
		<#else>
			<h3>We're sorry, the page that you were looking for could not be found.</h3>
		</#if>
	</div>
	
	<div class="error-advice">
		<p>&nbsp;</p>
		<#if debugEnabled?? && debugEnabled && errorDetails??>
			<label>Error Details:</label>
			<br/>
			<textarea>${errorDetails}</textarea>
		<#else>
			<#if CONTEXT_PREVIOUS_PATH?? && CONTEXT_ROOT_PATH??>
				<p>You can revisit the <a href="${CONTEXT_PREVIOUS_PATH}">previous page</a> or return <a href="${CONTEXT_ROOT_PATH}/"> home</a>.</p>
				<p>To report the problem, click on your name.</p>
			<#else>
				<p>You can revisit the previous page or return home</a>.</p>
				<p>To report the problem, click on your name.</p>
			</#if>
		</#if>
	</div>
    
	</div>
