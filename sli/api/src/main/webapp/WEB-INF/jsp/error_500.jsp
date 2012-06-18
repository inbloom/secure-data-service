<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page isErrorPage="true" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Error</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/error.css"/>"/>
</head>

<body>
        <div class="error-container">
                <div class="error-header">ERROR</div>
                <div class="error-content">
                        <h3>We're sorry, an error occurred.</h3>
                        <c:if test="${not empty pageContext.exception.localizedMessage}" >
                            <c:out value="${pageContext.exception.localizedMessage}"/>
                        </c:if>
                </div>
        </div>
</body>
</html>