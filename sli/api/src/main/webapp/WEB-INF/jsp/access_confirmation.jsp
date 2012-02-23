<%-- <%@ page import="org.springframework.security.core.AuthenticationException" %> --%>
<%-- <%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %> --%>
<%-- <%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %> --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %> --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
  <title>Authorization Confirmation</title>
  <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
</head>
<body>
<h1>Authorization</h1>

  <div id="content">

    <sec:authorize ifAllGranted="ROLE_ADMINISTRATOR">
      <h2>Please Confirm</h2>

      <p>You hereby authorize ${client.clientId} to access your protected resources.</p>

      <form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post">
        <input name="user_oauth_approval" value="true" type="hidden"/>
        <label><input name="authorize" value="Authorize" type="submit"></label>
      </form>
      <form id="denialForm" name="denialForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post">
        <input name="user_oauth_approval" value="false" type="hidden"/>
        <label><input name="deny" value="Deny" type="submit"></label>
      </form>
    </sec:authorize>
  </div>

</body>
</html>