<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Programs</title>
</head>
<body>
	<table>
		<tr>
			<th id="header.Student">Program</th>
		</tr>
		<c:forEach var="program" items="${ids}">
			<tr>
				<td id="name.${program}">${program}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>