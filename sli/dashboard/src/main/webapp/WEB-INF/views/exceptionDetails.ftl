
<!-- 
 *
 * Exception Details is the template markup for an Exception Panel component which leverages jQuery UI
 * 
 * @author Robert Bloh
 *
-->

<!-- Exception Panel Start -->
<br/>
<div class="ui-widget">
<div class="ui-widget-header ui-corner-all panel-header">
	<#if exception?? && exception.label??>
		<h3 align="center">${exception.label}</h3>
	<#else>
		<h3 align="center">SLI Exception</h3>
    </#if>
</div>

<div class="ui-widget-content panel-contents">

<p>

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
	<tbody>
	<tr>
		<td align="center" ><img src="static/images/exception.jpg" /></td>
	</tr>
	<tr>
		<td align="center" >&nbsp</td>
	</tr>
	<tr>
		<td align="center" >
		<#if exception?? && exception.messageType??>
			<h5>${exception.messageType}</h5>
		<#else>
			<h5>SLI Exception Message</h5>
	    </#if>
		</td>
	</tr>
	<tr>
		<td align="center" >&nbsp</td>
	</tr>
	<tr>
		<td align="center" ><label>Exception Details:</label></td>
	</tr>
	<tr>
		<td align="center" >&nbsp</td>
	</tr>
	<tr>
		<td align="center" >
		<#if exception?? && exception.message??>
            <label>${exception.message}</label>
          <#if debugEnabled?? && debugEnabled>
            <br>
            <br>
            <button align="center" id="stackTraceMore" onclick="$('#stackTrace').show(); $(this).hide();">More Details</button>
            <div id="stackTrace" align="left" style="padding:20px; font-size:80%; float:left; display:none;"> 
                ${stackTrace} 
                <br>
                <br>
                <button id="stackTraceless" onclick="$('#stackTrace').hide(); $(stackTraceMore).show();">Less Details</button>
            </div>
          </#if>
		<#else>
			<label>SLI Exception details are available in the logs...</label>
	    </#if>
		</td>
	</tr>
	<tr>
		<td align="center" >&nbsp</td>
	</tr>
	</tbody>
</table>

</p>
</div>
</div>
<!-- Exception Panel Finish -->
<br/>

