<html>
<body>
<h2>${message}</h2>
<form method="post" action="login" name="loginForm">
Username: <input type="text" name="username"/><br/>
Password: <input type="password" name="pwd"/><br/>
<input type="submit" value="Submit"/>
<span name="errorMessage" style="display:${displayError};color:red">${errorMessage}</span>
</form>
</body>
</html>
