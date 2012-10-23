<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="org.slc.sli.sandbox.idp.service.DefaultUsersService.Dataset" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
<meta http-equiv="Content-Script-Type" content="text/javascript"/>
<title><c:choose>
		<c:when test="${realm=='SLIAdmin'}">
							Shared Learning Collaborative
						</c:when>
		<c:when test="${is_sandbox}">
							SLC Sandbox Environment
						</c:when>
		<c:otherwise>
							SLC Mock IDP for ${fn:escapeXml(realm)}
						</c:otherwise>
	</c:choose></title>
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
<script type="text/javascript">
$(document).ready(function() {
	datasetChanged();
});

  function disableTextbox() {
	  select = document.getElementById("selected_roles")
	  textbox = document.getElementById("customRoles")
	  if (select.selectedIndex > 0) {
		  textbox.value = ""
	  } 
  }
  
  function disableSelect() {
	   select = document.getElementById("selected_roles")
	   textbox = document.getElementById("customRoles")
	   if (textbox.value.length > 0) {
		   select.selectedIndex = 0
	   } 
	  
  }
  
  function datasetChanged(){
	  $(".userListDiv").hide();
	  $(".userList").prop("disabled", true);
	  dataset = $("#datasets").val();
	  $("#" + dataset + "-usersDiv").show();
	  $("#" + dataset).prop("disabled", false);
  }
  
</script>
</head>
<c:if test="${sessionScope.user_session_key==null}">
	<c:set var="onload" value="document.login_form.user_id.focus();"  />
</c:if>
<c:if test="${sessionScope.user_session_key!=null}">
	<c:set var="onload" value="document.login_form.impersonate_user.focus();" />
</c:if>

<body onload="${onload}">
	<div class="container">
		
		<div class="realm-name">
			<h1>
				<span class="heading">
					<img src="resources/default.png" alt="SLC IDP Logo"/>
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
		
		<c:if test="${msg!=null}">
				<div class="error-message"><c:out value="${msg}"/></div>
		</c:if>
		<c:if test="${sessionScope.user_session_key!=null}">
			<form id="logout_form" name="logout_form" action="logout" method="post" class="form-horizontal">
			<div>Currently logged in as: ${sessionScope.user_session_key.userId}. 
			<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
			<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
			<a href="#" onclick="javascript:document.forms['logout_form'].submit();">Logout</a>				
			</div>
			</form>
		</c:if>
		<c:if test="${message!=null}">
			<div>${message}</div>
		</c:if>

		<div class='form-container'>
			<form id="login_form" name="login_form" action="login" method="post" class="form-horizontal">
				<input type="hidden" name="realm" value="${fn:escapeXml(realm)}"/>
				<input type="hidden" name="SAMLRequest" value="${fn:escapeXml(SAMLRequest)}"/>
				<input type="hidden" name="isForgotPasswordVisible" value="${fn:escapeXml(isForgotPasswordVisible)}"/>
				<fieldset>
					<c:if test="${sessionScope.user_session_key==null}">
						<div class="control-group">
							<label for="user_id" class="control-label">User Name:</label>
							<input type="text" id="user_id" name="user_id" />
						</div>
						<div class="control-group">
							<label for="password" class="control-label">Password:</label>
							<input type="password" id="password" name="password" autocomplete="off"/>
						</div>
					</c:if>
					<c:if test="${isForgotPasswordVisible}">
						<div class="control-group">
							<a class="tool-tip-link" id="forgotPassword" name="forgotPassword" href="${fn:escapeXml(adminUrl)}/forgotPassword">Forgot your password?</a>
						</div>
					</c:if>
					<c:if test="${is_sandbox}">
						<div class="control-group">
							<h2>Use a Sample Dataset User</h2>
						</div>
						<div class="control-group">
							<label for="datasets" class="control-label">Datasets:</label>
							<select id="datasets" name="datasets" class="input-xlarge " onchange="datasetChanged()">
                                <option> </option>
								<c:forEach items="${datasets}" var="dataset">
									<option value="${dataset.key}">${dataset.displayName}</option>
								</c:forEach>
							</select>
						</div>
							<c:forEach items="${datasets}" var="dataset">
								<div class="control-group userListDiv" id="${dataset.key}-usersDiv">
									<label for="${dataset.key}" class="control-label">Available Users:</label>
									<select id="${dataset.key}" name="userList" class="input-xlarge userList" onchange="">
										<c:forEach items="<%=request.getAttribute(((Dataset)pageContext.getAttribute("dataset")).getKey())%>" var="userList">
		                                	 <option value="${userList.userId}">${userList.name} - ${userList.role}</option>
										</c:forEach>
									</select>
								</div>
							</c:forEach>
						<div class="control-group">
							<h2>Or Manually Specify a User</h2>
						</div>
						<div class="control-group">
							<label for="impersonate_user" class="control-label">Login as User:</label>
							<input type="text" id="impersonate_user" name="impersonate_user" value="${impersonate_user}"/>
						</div>
						<div class="control-group">
							<label for="selected_roles" class="control-label">Roles:</label>
								<select id="selected_roles" name="selected_roles" class="input-xlarge " onchange="disableTextbox();">
	                                <option> </option>
									<c:forEach items="${roles}" var="role">
										<option value="${role.id}">${role.name}</option>
									</c:forEach>
								</select>
	                            <div class="control-group top-gap">
	                                <label for='customRoles' class="control-label">Or Custom Role:</label>
	                                <input type="text" id="customRoles" name="customRoles" onchange="disableSelect();" />
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
