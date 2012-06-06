module ChangePasswordsHelper
  # Gets the email address for the supplied GUID and sends them a confirmation-request
  # email.
  #
  # Input Prameters:
  #   - guid : ID of record containing email address to send to
  #
  # Returns:
  #     Nothing
  #
  def self.send_notification_email(validate_base, guid)

    email_message = "Your SLI account has been created pending email verification.\n" <<
        "\n\nPlease visit the following link to confirm your account:\n" <<
        "\n\n#{userEmailValidationLink}\n\n"

    if (email_token.nil?)
      email_message = "There was a problem creating your account. Please try again."
    end

    APP_EMAILER.send_approval_email(
        user[:email],
        user[:firstName],
        user[:lastName],
        EMAIL_SUBJECT,
        email_message)
    true
  end
end
