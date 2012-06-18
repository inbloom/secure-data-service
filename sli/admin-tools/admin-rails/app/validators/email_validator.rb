class EmailValidator < ActiveModel::EachValidator
  @@EXISTING_EMAIL_MSG = "An account with this email already exists"
  
  def validate_each(record, attribute, value)
    # don't validate empty values here, otherwise we get duplicate error messages
    if value == nil or value.length == 0
      return
    elsif not value =~ /^[-a-z0-9_]+([\.]{0,1}[-a-z0-9_]+)*\@([a-z0-9]+([-]*[a-z0-9]+)*\.)*([a-z0-9]+([-]*[a-z0-9]+))+$/i
      record.errors[attribute] << "Please enter a valid email address"
    elsif value.length > 160
      record.errors[attribute] << "Email address is too long"
    else
      record.errors[attribute] << @@EXISTING_EMAIL_MSG if ApplicationHelper.user_exists? value
    end
  end
  
  def self.existing_email_msg
    @@EXISTING_EMAIL_MSG
  end
end