<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%
	String realm = request.getParameter("realm");
	if(realm!=null)
	{
		response.sendRedirect("https://devdanil.slidev.org:8080/idp/saml2/jsp/spSSOInit.jsp?metaAlias=/sp&NameIDFormat=transient&idpEntityID=http%3A%2F%2Fdevdanil.slidev.org%3A8080%2Fidp&binding=HTTP-POST&RelayState="+URLEncoder.encode(request.getParameter("return"),"UTF-8"));
	}
%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Realm Chooser</title>
</head>
<body>
	Choose your realm
	<form action="<%=request.getRequestURL()%>">
		<input type="radio" name="realm" value="sliIdp">Sli IDP</input>
		<input type="hidden" name="return" value="<%=request.getParameter("return") %>" />
		<br /><input type="submit">Select</input>
	</form>
</body>
</html>