
<!-- 
 *
 * Teardrop is the template markup for a Teardrop component which leverages CSS styling to provide visual clues such as color-coding and trending.
 * 
 * @author Robert Bloh rbloh@wgen.net
 *
-->

<#if teardrop?? && teardrop.styleName?? && teardrop.value??>
	<div class="${teardrop.styleName}">${teardrop.value}</div>
<#else>
	<!-- The teardrop model is not set properly! -->
</#if>
