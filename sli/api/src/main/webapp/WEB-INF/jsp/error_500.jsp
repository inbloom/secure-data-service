<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page isErrorPage="true" %> 
<html>
<head>
<title>Error</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/error.css"/>"/>
</head>

<body>
        <div class="error-container">
                <div class="error-header">ERROR</div>
                <div class="error-content">
                        <h3>We're sorry, an error occurred.</h3>
                        <% if (exception.getCause().getLocalizedMessage() != null) { %>
                            <c:out value="<%=exception.getCause().getLocalizedMessage()%>"/>
                        <% } %>
                </div>
        </div>
</body>
</html>
