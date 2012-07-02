<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<title>Working...</title>
</head>

<body>
	<form action="${samlAssertion.redirectUri}" method="post"
		name="hiddenform">
		<input type="hidden" name="SAMLResponse"
			value="${samlAssertion.samlResponse}" />

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
