<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="false"%>
<html>
<head>
<title>Simple IDP</title>
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
<body OnLoad="document.login_form.user_id.focus();">

	<div class="container">
		
		<div class="realm-name">
			<h1>
				<span class="heading">
					<img src="resources/${fn:escapeXml(realm)}.png"/>
					<c:choose>
						<c:when test ="${realm=='SLIAdmin'}">
							SLI Admin IDP
						</c:when>
						<c:when test="${is_sandbox}">
							SLI Sandbox IDP
						</c:when>
						<c:otherwise>
							SLI Mock IDP for ${fn:escapeXml(realm)}
						</c:otherwise>
					</c:choose>
				</span>
			</h1>
		</div>

		<div class='form-container'>
			<c:if test="${msg!=null}">
				<div class="error-message"><c:out value="${msg}"/></div>
			</c:if>
			<form id="login_form" name="login_form" action="login" method="post" class="form-horizontal">
				<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
				<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
				<fieldset>
					<div class="control-group">
						<label for="user_id" class="control-label">User Name:</label>
						<input type="text" id="user_id" name="user_id" />
					</div>
					<div class="control-group">
						<label for="password" class="control-label">Password:</label>
						<input type="password" id="password" name="password" />
					</div>
					<c:if test="${is_sandbox}">
					<div class="control-group">
						<label for="impersonate_user" class="control-label">Login as User:</label>
						<input type="text" id="impersonate_user" name="impersonate_user" value="${impersonate_user}"/>
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
					</c:if>
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
