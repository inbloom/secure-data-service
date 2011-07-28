<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>

<html>
  <head>
    <title>SLI Test</title>
  </head>

  <body onload="document.f.j_username.focus();">
    <h1>SLI Test Login</h1>

    <%-- this form-login-page form is also used as the
         form-error-page to ask for a login again.
         --%>
    <% if (session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null) { %>
      <font color="red">
        Your login attempt was not successful, try again.<br/><br/>
        Reason: <%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
      </font>
    <% } %>

    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
      <table>	
      <tr><td>Directory:</td><td><select name="authDirectory">
		       <option value="slitest">slitest.ec2.internal Domain</option>
		       <option value="local">Local Webapp</option>
		</select></td></tr>
        <tr><td>User:</td><td><input type='text' name='j_username' /></td></tr>
        <tr><td>Password:</td><td><input type='password' name='j_password'/></td></tr>

        <tr><td ><input name="submit" type="submit"></td><td><input name="reset" type="reset"></td></tr>
      </table>
    </form>
  </body>
</html>