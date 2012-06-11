class EmailValidator < ActiveModel::EachValidator
  @@EXISTING_EMAIL_MSG = "An account with this email already exists"
  
  def validate_each(record, attribute, value)
    # don't validate empty values here, otherwise we get duplicate error messages
    if value == nil or value.length == 0
      return
    elsif not value =~ /^[-a-z0-9_+\.]+\@([-a-z0-9]+\.)+[a-z0-9]{2,4}$/i
      record.errors[attribute] << "Please enter a valid email address"
    else
      record.errors[attribute] << @@EXISTING_EMAIL_MSG if ApplicationHelper.user_exists? value
    end
  end
  
  def self.existing_email_msg
    @@EXISTING_EMAIL_MSG
  end
end