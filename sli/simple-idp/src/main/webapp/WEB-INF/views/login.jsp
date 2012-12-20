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

<link href="resources/bootstrap.css" rel="stylesheet"/>
<link href="resources/bootstrap-responsive.min.css" rel="stylesheet"/>
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
	color:rgb(0, 102, 153);
	font-size:14px; 
}

.custom-role {
    margin-left: 140px;
    margin-top: 20px;
}

.top-gap {
    margin-top: 10px;
}

.brandContainer {
    background-color: #F6F3EA;
    border-color: #ECE7D8;
    border-left: 3px solid #ECE7D8;
    border-radius: 6px 6px 6px 6px;
    border-style: solid;
    border-width: 3px;
    color: #007096;
    margin-top: 60px;
    margin-bottom: 30px;
    padding: 30px;
}
.brandContainerTop {
    background-color: #F6F3EA;
    border-left: 3px solid #ECE7D8;
    border-radius: 6px 6px 0 0;
    border-right: 3px solid #ECE7D8;
    border-top: 3px solid #ECE7D8;
    padding: 30px;
}
.brandContainerBottom {
    background-color: #FFFFFF;
    border: 3px solid #ECE7D8;
    border-radius: 0 0 6px 6px;
    padding: 30px;
}

.brandContainer h1 { font-size: 36px; }
.brandContainer h1.prodTitle { color: #007096; }
.brandContainer h1.sandboxTitle { color: #512B73;}

</style>

<link href="resources/bootstrap.css" rel="stylesheet"/>
<link href="resources/globalStyles.css" rel="stylesheet"/>
</head>

<body onload="document.login_form.user_id.focus();">
<div class="container">
	<div class="row">
		<div class="span12">
			<div class="brandContainer sandBanner">
				<div class="row">
					<div class="span2"> <img src="resources/SLC-Logo-text.png"> </div>
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
							<input id="login_button" name="commit" type="submit" value="Sign in" class="btn btn-primary" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>

	</div>

</body>
</html>
