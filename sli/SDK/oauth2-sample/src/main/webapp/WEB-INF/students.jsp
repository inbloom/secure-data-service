<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.stream.JsonWriter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Student Grades</title>
<script type="text/javascript" src="graph.js"></script>
</head>
<body>
    <table>
        <tr>
            <th>Student</th><th>Grade</th>
        </tr>
        <c:forEach var="student" items="${grades}">
            <tr>
                <td>${student.key}</td><td>${student.value}</td>
            </tr>
        </c:forEach>
    </table>
    <canvas id="gradeGraph" width="500" height="300"></canvas>
    <script>
    var gradeData = <%= (new Gson()).toJson(request.getAttribute("grades"))  %>
    graph(document.getElementById('gradeGraph'), gradeData);
    </script>
</body>
</html>