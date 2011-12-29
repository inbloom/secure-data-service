<html>
<body>
	<h2>${message}</h2>
		<form method="post" action="login" name="loginForm">
			Username: <input type="text" id="username" name="username"/><br/>
			Password: <input type="password" id="password" name="pwd"/><br/>
			<input type="submit" id="submit" value="Submit"/>
			<span name="errorMessage" style="display:${displayError};color:red">${errorMessage}</span>
		</form>
	</body>
</html>
