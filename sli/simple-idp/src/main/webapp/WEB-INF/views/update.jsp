<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="false"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>Working...</title>
</head>

<body>
	<form action="login" method="post" name="hiddenform">
		<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
		<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
		<input type="hidden" name="user_id" value="${fn:escapeXml(userId)}"/>
		<input type="hidden" name="password" value="${fn:escapeXml(password)}"/>
		<noscript>
			<p>Script is disabled. Click Submit to continue.</p>
			<input type="submit" value="Submit" />
		</noscript>
	</form>
	<script type="text/javascript">
		window.setTimeout('document.forms[0].submit()', 0);
	</script>
</body>
</html>
