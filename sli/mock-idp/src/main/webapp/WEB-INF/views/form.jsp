<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Mock IDP</title>
	<style type="text/css">
span.header {
	display:inline-block;
	font-size: 18px;
	width: 500px;
	border-style: solid;
	border-width: 1px;
	margin: 30px 30px;
	padding:30px 30px;
}

form {
	margin: 30px 30px;
}

div.logout {
	position: absolute;
	left: 700px;
	top:  45px;
}

#login_button {
	position: relative;
	left: 200px;
}

.error {
	font-size: 18px;
	color: red;
}
	</style>
</head>
<body>

<div class="header">
	<span class="header">${tenant} IDP</span>
</div>

<div class="error">
	<span class="error">${error}</span>
</div>
<div class='user_select'>
	<form id="login_form" action="login" method="post">
		<label for="selected_user">User:</label>
		<select id="selected_user" name="selected_user">
			<c:forEach items="${users}" var="user">
				<option value="${user.id}">${user.lastName}, ${user.firstName} (${user.type})</option>
			</c:forEach>
		</select>
		<br />
		<label for="selected_roles">Roles:</label>
		<select id="selected_roles" multiple="multiple" name="selected_roles">
			<c:forEach items="${roles}" var="role">
				<option value="${role.id}">${role.name}</option>
			</c:forEach>
		</select>
		<br />
		<input id="login_button" name="commit" type="submit" value="Login" />
	</form>
</div>

<br />
<br />
<div class='logout'>
	<form id="logout_form" action="logout" method="post">
		<input id="logout_button" name="commit" type="submit" value="Logout" />
	</form>
</div>

</body>
</html>
