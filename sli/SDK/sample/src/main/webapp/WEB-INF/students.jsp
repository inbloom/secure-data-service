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
	<table border="1" style="text-align: center" cellpadding="5px">
		<tr>
			<th id="header.teacher" style="width: 300px">Teacher Name</th>
			<th id="header.realm" style="width: 300px">Realm Name</th>
		</tr>

		<c:forEach var="teacher" items="${tenantMap}">
			<tr>
				<td id="name.${teacher.key}" style="width: 300px">${teacher.key}</td>
				<td id="realm.${teacher.value}" style="width: 300px">${teacher.value}</td>
			</tr>
		</c:forEach>


	</table>
	<p></p>
	<table border="1" style="text-align: center" cellpadding="5px">
		<tr>
			<th id="header.roles" style="width: 300px">Authorized Roles</th>
			<th id="header.accessRights" style="width: 300px">Access Rights</th>
		</tr>
		<tr>
			<td id="roles" style="width: 300px"><c:forEach var="role"
					items="${roles}">
				${role}&nbsp;
		</c:forEach></td>
			<td id="accessRights" style="width: 300px"><c:forEach
					var="right" items="${accessRights}">
				${right}&nbsp;
		</c:forEach></td>
		</tr>
	</table>
	<p></p>
	<table border="1" style="text-align: center" cellpadding="5px">
		<tr>
			<th id="header.Student" style="width: 300px">List of Students</th>
		</tr>
		<c:forEach var="student" items="${grades}">
			<tr>
				<td id="name.${student.key}" style="width: 300px">${student.key}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>