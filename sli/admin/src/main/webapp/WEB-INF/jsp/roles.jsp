<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>SLI Default Roles - Admin Page</title>
</head>
<body>
	<div style="float:right">
		Welcome ${USER_NAME} | <a href="/admin/logout">Logout</a>
	</div>
	
	<h1>SLI Admin Tool - Default Roles and Permissions</h1>
	<table border="1">
      <tr>
      	<th>SLI Role</th>
      	<th>Aggregate</th>
      	<th>Individual Record Data</th>
      	<th>General Student Data</th>
      	<th>Restricted Student Data</th>
      </tr>
      <c:forEach var="role" items="${roleJsonData}">
        <tr>
          <td><c:out value="${role.name}"/></td>
          <td>
          	<c:if test="${role.aggregate==true}">
          		<c:out value="Yes"/>
          	</c:if>
          	<c:if test="${role.aggregate==false}">
          		<c:out value="No"/>
          	</c:if>
          	</td>
          	<td><c:out value="${role.individual}"/></td>
          	<td><c:out value="${role.general}"/></td>
          	<td><c:out value="${role.restricted}"/></td>
        </tr>
      </c:forEach>
    </table>
    <p>More info on <a href="https://thesli.onconfluence.com/display/sli/SLI+User+Role+and+Permission+Management">Default SLI Roles and Permissions</a></p>
</body>
</html>
