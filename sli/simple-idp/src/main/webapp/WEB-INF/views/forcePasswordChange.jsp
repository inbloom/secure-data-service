<html>
<h1>Change Password</h1>
<h3>First time user, please change your password below and start using the SLI.</h3>
		<div class='form-container'>
			<c:if test="${msg!=null}">
				<div class="error-message"><c:out value="${msg}"/></div>
			</c:if>
			<form id="force_password_change_form" name="force_password_change_form" action="login" method="post" class="form-horizontal">
				<fieldset>
					<div class="control-group">
						<label for="user_id" class="control-label">User Name:</label>
						<input type="text" id="user_id" name="user_id" />
					</div>
					<div class="control-group">
						<label for="password" class="control-label">Password:</label>
						<input type="password" id="password" name="password" />
					</div>
					<div class="control-group">
						<div class="controls">
							<input id="login_button" name="commit" type="submit" value="Save Changes" class="btn" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>
</html>