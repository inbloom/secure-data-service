<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Choose your realm</title>
<link rel="icon" type="image/x-icon" href="<c:url value="/resources/favicon.ico"/>"/>
</head>
<style>
body {
  background-color: #fff;
  color: #333;
  font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
  font-size: 13px;
  line-height: 18px;
}
</style>
<body>
	<div style="color: red;width: 35%;">${errorMsg}</div>
	<form:form action="/api/oauth/sso" method="GET" commandName="dummy">
		<input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}" />
		<input type="hidden" name="clientId" value="${fn:escapeXml(clientId)}" />
		<c:if test="${not empty state}">
		<input type="hidden" name="state" value="${fn:escapeXml(state)}" />
		</c:if>
		Please choose your State/District:
		<form:select path="" name="realmId" items="${realms}" />
		<input type="submit" value="Go" id="go" />

	</form:form>
</body>
</html>