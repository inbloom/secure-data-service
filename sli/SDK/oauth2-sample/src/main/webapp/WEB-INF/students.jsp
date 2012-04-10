<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.stream.JsonWriter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>List of Students</title>
</head>
<body>
<p></p>
	<table border="1" style="text-align:center" cellpadding="5px">
	   <tr>
			<th id="header.teacher">Teacher Name</th>
			<th id="header.realm">Realm Name</th>
		</tr>
	   <c:forEach var="teacher" items="${tenantMap}">
			<tr>
				<td id="name.${teacher.key}">${teacher.key}</td>
				<td id="name.${teacher.value}">${teacher.value}</td>
			</tr>
		</c:forEach>
		</table>
		<p></p>
		<table border="1" style="text-align:center" cellpadding="5px">
		<tr>
			<th id="header.Student">List of Students</th>
		</tr>
		<c:forEach var="student" items="${grades}">
			<tr>
				<td id="name.${student.key}">${student.key}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>