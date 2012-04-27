<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Mock IDP</title>
<style type="text/css">
.tenant {
	/* color: #438746 */
}

.realm-name {
	padding: 30px;
	background-color: #EEE;
	border: thick;
	-webkit-border-radius: 6px;
	-mox-border-radius: 6px;
	border-radius: 6px;
	margin-top: 30px;
}

.form-container {
	margin: 10px;
	margin-top: 30px;
}
</style>
<link href="resources/bootstrap.css" rel="stylesheet">
</head>
<body>

	<div class="container">

		<div class="realm-name">
			<h1>
				<span class="heading">Sandbox</span> <span class="tenant">${tenant}</span> <span class="heading">Realm</span>
			</h1>
		</div>

		<div class='form-container'>
			<form id="login_form" action="login" method="post"
				class="form-horizontal">
				<fieldset>
					<div class="control-group">
						<label for="selected_user" class="control-label">User:</label>
						<div class="controls">
							<select id="selected_user" name="selected_user"
								class="input-xlarge">
								<c:forEach items="${users}" var="user">
									<option value="${user.id}">${user.lastName},
										${user.firstName} (${user.type})</option>
								</c:forEach>
							</select>
							<p class="help-block">Staff and Teachers available to this
								tenant</p>
						</div>
					</div>
					<div class="control-group">
						<label for="selected_roles" class="control-label">Roles:</label>
						<div class="controls">
							<select id="selected_roles" multiple="multiple"
								name="selected_roles" class="input-xlarge">
								<c:forEach items="${roles}" var="role">
									<option value="${role.id}">${role.name}</option>
								</c:forEach>
							</select>
							<p class="help-block">Select one or more roles using Ctl/Apple+Click</p>
						</div>
					</div>
					<div class="control-group">
						<div class="controls">
							<input id="login_button" name="commit" type="submit" value="Login" class="btn" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>

	</div>

</body>
</html>
