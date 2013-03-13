<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.slc.sli.sandbox.idp.service.DefaultUsersService.Dataset" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Script-Type" content="text/javascript"/>
<title>inBloom Login</title>
<link rel="icon" type="image/x-icon" href="resources/favicon.ico"/>
<script type="text/javascript" src="resources/jquery-1.7.2.min.js"></script>

<link href="resources/bootstrap.css" rel="stylesheet"/>
<link href="resources/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="resources/globalStyles.css" rel="stylesheet"/>

</head>

<body onload="document.login_form.user_id.focus();">
<div class="container">
	<div class="row">
		<div class="span12">
			<div class="brandContainer">
				<div class="row">
					<div class="span2"> 
						<c:if test="${!isSandbox}">
							<img src="resources/inBloom_logo.png"> 
						</c:if>
						 <c:if test="${isSandbox}">
	            			<img src="resources/inBloomSandbox.png">
	            		</c:if>
					</div>
					<div class="span8">
						<h1>${fn:escapeXml(subTitle)}</h1>
					</div>
				</div>
			</div>
		</div>
	</div>
		
		<c:if test="${msg!=null}">
			<div class="alert alert-success"><c:out value="${msg}"/></div>
		</c:if>
		<c:if test="${errorMsg!=null}">
			<div class="alert alert-error"><c:out value="${errorMsg}"/></div>
		</c:if>
		
		<div class='form-container'>
			<form id="login_form" name="login_form" action="login" method="post" class="form-horizontal">
				<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
				<input type="hidden" name="developer" value="${fn:escapeXml(developer)}"/>
      			<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
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
							<div class="controls">
								<a class="tool-tip-link" id="forgotPassword" name="forgotPassword" href="${fn:escapeXml(adminUrl)}/forgotPassword">Forgot your password?</a>
							</div>
						</div>
					</c:if>
					<div class="control-group">
						<div class="controls">
							<input id="login_button" name="commit" type="submit" value="Sign in" class="btn btn-info" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>

	</div>

</body>
</html>
