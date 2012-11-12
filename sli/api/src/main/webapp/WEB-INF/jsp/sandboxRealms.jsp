<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Choose your realm</title>
<link href="<c:url value="/resources/bootstrap.css"/>" rel="stylesheet">
<link rel="icon" type="image/x-icon" href="<c:url value="/resources/favicon.ico"/>"/>
</head>
<style>
body {
  background-color: #fff;
  color: #333;
  font-family: verdana, arial, helvetica, sans-serif;
  font-size: 13px;
  line-height: 18px;
}
h1 {
	font-size: 20px;
}
.box {
	float: left;
	width: 400px;
	padding: 20px;
}
.boxRight {
	border-left: 1px solid #C0C0C0;
}
.buttonDiv {
	padding-top: 50px;
}
</style>

<body>
	<div class="container">
      <div class="hero-unit">
      	<div class="row">
      		<div class="span2">
      			<img src="<c:url value="/resources/slc.png"/>" alt="SLC Logo"/>
      		</div><!-- end span2 -->
      		<div class="span8">
      			<h1>Shared Learning Collaborative</h1>
      			<h2>Application Developer Sandbox</h2>
      		</div><!-- end span7 -->
      	</div><!-- end row -->
      </div><!-- end hero-unit -->
      
	<div style="color: red;width: 35%;">${errorMsg}</div>
	<form:form action="/api/oauth/sso" name="adminRealmForm" id="adminRealmForm">
		<input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}" />
		<input type="hidden" name="clientId" value="${fn:escapeXml(clientId)}" />
		<c:if test="${not empty state}">
			<input type="hidden" name="state" value="${fn:escapeXml(state)}" />
		</c:if>
		<input type="hidden" id="realmId" name="realmId" value ="${adminRealm}"/>
		
		<div class="box">
			<h1>Administer My Sandbox</h1>
			<p>Administering your sandbox allows you to ingest test data, register applications, and manage accounts on your sandbox.</p>
			<div class="buttonDiv">
				<input type="submit" value="Next" id="adminLink" class="btn btn-primary"/>
			</div>	
		</div>
	</form:form>
	
	<form:form action="/api/oauth/sso" name="sandboxRealmForm" id="sandboxRealmForm">
		<input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}" />
		<input type="hidden" name="clientId" value="${fn:escapeXml(clientId)}" />
		<c:if test="${not empty state}">
			<input type="hidden" name="state" value="${fn:escapeXml(state)}" />
		</c:if>
		<input type="hidden" id="realmId" name="realmId" value ="${sandboxRealm}"/>
		<div class="box boxRight">
			<h1>Test Applications in My Sandbox</h1>
			<p>The sandbox gives you a safe place to test your applications while you are developing them.</p>
			<div class="buttonDiv">
				<input type="submit" id="sandboxLink" value="Next" class="btn btn-primary"/>
			</div>
		</div>
	</form:form>
	</div>
    
</body>
</html>