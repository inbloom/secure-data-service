<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Choose your realm</title>
</head>
<body>
	<h2>Choose your Realm</h2>
	<hr />
	<div style="color: red;border: 1px solid red;width: 35%;">${errorMsg}</div>
	<form:form action="/disco/realms/sso.do" method="GET" commandName="dummy">
		<table style="border: 1px solid black;">
			<tr>
				<td>Realm:</td>
				<td><form:radiobuttons path="" name="realmId" items="${realms}" /></td>
			</tr>
			<tr>
				<td>
				<input type="hidden" name="RelayState" value="${relayState}" /> 
				<input type="submit" value="Choose Your Destiny" /></td>
			</tr>
		</table>
	</form:form>
</body>
</html>