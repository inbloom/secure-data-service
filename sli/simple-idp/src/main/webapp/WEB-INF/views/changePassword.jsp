<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="false"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Script-Type" content="text/javascript"/>
<title>Simple IDP</title>
<link rel="icon" type="image/x-icon" href="resources/favicon.ico"/>
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
<link href="resources/bootstrap.css" rel="stylesheet"/>
</head>
<body onload="document.login_form.old_pass.focus();">

	<div class="container">
		
		<div class="realm-name">
			<h1>
				<span class="heading">
					<c:choose>
						<c:when test ="${realm=='SLIAdmin'}">
							<img src="resources/SLIAdmin.png" alt="SLC IDP Logo"/>
						</c:when>
						<c:when test="${is_sandbox}">
							<img src="resources/sandbox.png" alt="Sandbox IDP Logo"/>
						</c:when>
						<c:when test="${realm!=null}">
							<img src="resources/${fn:escapeXml(realm)}.png" alt="${fn:escapeXml(realm)} IDP Logo"/>
						</c:when>
						<c:otherwise>
							<img src="resources/default.png" alt="IDP Logo"/>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test ="${realm=='SLIAdmin'}">
							Shared Learning Collaborative
						</c:when>
						<c:when test="${is_sandbox}">
							SLC Sandbox Environment
						</c:when>
						<c:otherwise>
							SLC Mock IDP for ${fn:escapeXml(realm)}
						</c:otherwise>
					</c:choose>
				</span>
			</h1>
		</div>
		<div>
			<h1>Change Password</h1>
			<h3>First time user, please change your password below and start using the SLI.</h3>
		</div>
		<div class='form-container'>
			<c:if test="${msg!=null}">
				<div class="error-message"><c:out value="${msg}"/></div>
			</c:if>
			<form id="force_password_change_form" name="force_password_change_form" action="saveChanges" method="post" class="form-horizontal">
				<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
				<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
				<input type="hidden" name="userId" value="${fn:escapeXml(userId)}"/>
				<fieldset>
					<div class="control-group">
						<label for="old_pass" class="control-label">Old Password:</label>
						<input type="password" id="old_pass" name="old_pass" />
					</div>
					<div class="control-group">
						<label for="new_pass" class="control-label">New Password:</label>
						<input type="password" id="new_pass" name="new_pass" />
					</div>
					<div class="control-group">
						<label for="new_confirm" class="control-label">Confirm New Password:</label>
						<input type="password" id="new_confirm" name="new_confirm" />
					</div>
					<div class="control-group">
						<div class="controls">
							<input id="save_changes_button" name="commit" type="submit" value="Save Changes" class="btn" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>