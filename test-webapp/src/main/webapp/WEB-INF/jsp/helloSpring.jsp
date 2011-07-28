<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<html>
<head>
	<title>Hello World [ Spring ]</title>
</head>
<body>
	<h1>Hello, Spring World</h1>
<p>You are logged in as the user <c:out value="${princ_name}" />.</p>	
<p>You selected <c:out value="${directory}" /> as your directory.</p>
<p><a href="/slitest/j_spring_security_logout">Logout</a></p>	
<p><a href="/slitest/">Home</a></p>	

</body>
</html>	