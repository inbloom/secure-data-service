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
				<span class="heading">SLI IDP</span>
			</h1>
		</div>

		<div class='form-container'>
			<div><c:out value="${msg}"/></div>
			<form id="login_form" action="login" method="post"
				class="form-horizontal">
				<fieldset>
					<div class="control-group">
						<label for="userId" class="control-label">UserId:</label>
						<input type="text" id="userId" name="userId" />
					</div>
					<div class="control-group">
						<label for="password" class="control-label">Password:</label>
						<input type="password" id="password" name="password" />
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
