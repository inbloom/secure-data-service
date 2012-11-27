<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.slc.sli.sandbox.idp.service.DefaultUsersService.Dataset" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Script-Type" content="text/javascript"/>
<title>${title}</title>
<link rel="icon" type="image/x-icon" href="resources/favicon.ico"/>
<script type="text/javascript" src="resources/jquery-1.7.2.min.js"></script>

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

.tool-tip-link {
	margin-left:140px;
	color:rgb(0, 102, 153);
	font-size:11px; 
}

.custom-role {
    margin-left: 140px;
    margin-top: 20px;
}

.top-gap {
    margin-top: 10px;
}


</style>
<link href="resources/bootstrap.css" rel="stylesheet"/>
</head>

<body onload="document.login_form.user_id.focus();">
	<div class="container">
		
      <div class="hero-unit">
      	<div class="row">
      		<div class="span2">
      			<img src="resources/default.png" alt="SLC IDP Logo"/>
      		</div><!-- end span2 -->
      		<div class="span8">
      			<h1>Shared Learning Collaborative</h1>
      			<h2>${subTitle}</h2>
      		</div><!-- end span7 -->
      	</div><!-- end row -->
      </div><!-- end hero-unit -->
		
		<c:if test="${msg!=null}">
			<div class="alert alert-success"><c:out value="${msg}"/></div>
		</c:if>
		<c:if test="${errorMsg!=null}">
			<div class="alert alert-error"><c:out value="${errorMsg}"/></div>
		</c:if>
		
		<div class='form-container'>
			<form id="login_form" name="login_form" action="login" method="post" class="form-horizontal">
				<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
				<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
				<input type="hidden" name="isForgotPasswordVisible" value="${fn:escapeXml(isForgotPasswordVisible)}"/>
				<fieldset>
					<div class="control-group">
						<label for="user_id" class="control-label">Email Address:</label>
						<div class="controls">
							<input type="text" id="user_id" name="user_id" />
						</div>
					</div>
					<div class="control-group">
						<label for="password" class="control-label">Password:</label>
						<div class="controls">
							<input type="password" id="password" name="password" autocomplete="off"/>
						</div>
					</div>
					<c:if test="${isForgotPasswordVisible}">
						<div class="control-group">
							<a class="tool-tip-link" id="forgotPassword" name="forgotPassword" href="${fn:escapeXml(adminUrl)}/forgotPassword">Forgot your password?</a>
						</div>
					</c:if>
					<div class="control-group">
						<div class="controls">
							<input id="login_button" name="commit" type="submit" value="Login" class="btn btn-primary" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>

	</div>

</body>
</html>
