<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Shared Learning Collaborative</title>
<link href="<c:url value="/resources/bootstrap.css"/>" rel="stylesheet">
<link href="<c:url value="/resources/bootstrap-responsive.min.css"/>" rel="stylesheet">
<link rel="icon" type="image/x-icon" href="<c:url value="/resources/favicon.ico"/>"/>
</head>
<style>
.box {
	float: left;
	width: 400px;
	padding: 20px;
}
.buttonDiv {
	padding-top: 50px;
}
.brandContainer{
	background-color: #000000;
    margin-bottom: 30px;
    padding: 30px;
    margin-top: 60px;
    color: #fff;
	-webkit-border-radius: 6px 6px 6px 6px;
   	   -moz-border-radius: 6px 6px 6px 6px;
            border-radius: 6px 6px 6px 6px;
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
</style>

<body>
	<div class="container">
	     <div class="brandContainer">
	    	<div class="row">
	    	    <div class="span2">
		            <img src="<c:url value="/resources/inBloomSandbox.png"/>">
		        </div>
		        <div class="span8">
		            <h1></h1>
		        </div>
		    </div>
	    </div>
	      
		<div style="color: red;width: 35%;">${errorMsg}</div>

		<div class="row">
			<div class="span6">
				<form:form action="/api/oauth/sso" name="adminRealmForm" id="adminRealmForm">
					<input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}" />
					<input type="hidden" name="clientId" value="${fn:escapeXml(clientId)}" />
					<c:if test="${not empty state}">
						<input type="hidden" name="state" value="${fn:escapeXml(state)}" />
					</c:if>
					<input type="hidden" id="realmId" name="realmId" value ="${adminRealm}"/>

					<h3>Administer my Sandbox</h3>
					<p>Administering your sandbox allows you to ingest test data, register applications, and manage accounts on your sandbox.</p>
					<div class="buttonDiv">
						<input type="submit" value="Next" id="adminLink" class="btn btn-info"/>
					</div>	
				</form:form>
			</div>

			<div class="span6">
				<form:form action="/api/oauth/sso" name="sandboxRealmForm" id="sandboxRealmForm">
					<input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}" />
					<input type="hidden" name="clientId" value="${fn:escapeXml(clientId)}" />
					<c:if test="${not empty state}">
						<input type="hidden" name="state" value="${fn:escapeXml(state)}" />
					</c:if>
					<input type="hidden" id="realmId" name="realmId" value ="${sandboxRealm}"/>
					<h3>Test Applications in my Sandbox</h3>
					<p>The sandbox gives you a safe place to test your applications while you are developing them.</p>
					<div class="buttonDiv">
						<input type="submit" id="sandboxLink" value="Next" class="btn btn-info"/>
					</div>
				</form:form>
			</div>
		</div>
	</div>

	
    
</body>
</html>