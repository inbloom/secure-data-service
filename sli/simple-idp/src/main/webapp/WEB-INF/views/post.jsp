<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Working...</title>
</head>

<body>
	<form action="${samlAssertion.redirectUri}" method="POST"
		name="hiddenform">
		<input type="hidden" name="SAMLResponse"
			value="${samlAssertion.samlResponse}" />

		<noscript>
			<p>Script is disabled. Click Submit to continue.</p>
			<input type="submit" value="Submit" />
		</noscript>
	</form>
	<script>
		window.setTimeout('document.forms[0].submit()', 0);
	</script>
</body>
</html>