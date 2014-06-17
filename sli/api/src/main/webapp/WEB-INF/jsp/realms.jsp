<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/assets/bootstrap/3.1.1/css/bootstrap.min.css" media="all"/>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Choose your realm</title>
<link rel="icon" type="image/x-icon" href="<c:url value="/resources/favicon.ico"/>"/>
</head>
<body>

<nav class="navbar navbar-inverse navbar-static-top" role="navigation">
    <div class="container">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle Navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="/">inBloom Portal</a>
    </div>
</nav>
<div class="container">

    <div class="row">
        <div class="col-lg-12">
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger">${errorMsg}</div>
            </c:if>

            <form:form action="/api/oauth/sso" method="GET" commandName="dummy" class="form-horizontal">
                <input type="hidden" name="redirect_uri" value="${fn:escapeXml(redirect_uri)}" />
                <input type="hidden" name="clientId" value="${fn:escapeXml(clientId)}" />
                <c:if test="${not empty state}">
                    <input type="hidden" name="state" value="${fn:escapeXml(state)}" />
                </c:if>
            <div class="form-group">
                <label class="col-sm-3 control-label">Please choose your State/District:</label>
                <div class="col-md-6">
                    <form:select path="" name="realmId" items="${realms}" class="form-control"/>
                </div>
                <div class="com-md-1">
                    <input type="submit" value="Go" id="go" class="btn btn-primary"/>
                </div>
            </div>
            </form:form>

        </div>
    </div>
</div>

</body>
</html>