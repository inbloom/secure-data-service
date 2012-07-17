<html>
<h1>Change Password</h1>
<h3>First time user, please change your password below and start using the SLI.</h3>
		<div class='form-container'>
			<c:if test="${msg!=null}">
				<div class="error-message"><c:out value="${msg}"/></div>
			</c:if>
			<form id="force_password_change_form" name="force_password_change_form" action="changePassword" method="put" class="form-horizontal">
				<fieldset>
					<div class="control-group">
						<label for="old_pass" class="control-label">Old Password:</label>
						<input type="text" id="old_pass" name="old_pass" />
					</div>
					<div class="control-group">
						<label for="new_pass" class="control-label">New Password:</label>
						<input type="password" id="new_pass" name="new_pass" />
					</div>
					<div class="control-group">
						<label for="new_confirm" class="control-label">Confirm New Password:</label>
						<input type="new_confirm" id="new_confirm" name="new_confirm" />
					</div>
					<div class="control-group">
						<div class="controls">
							<input id="submit_new_pass" name="commit" type="submit" value="Save Changes" class="btn" />
						</div>
					</div>
				</fieldset>
			</form>
		</div>
</html>