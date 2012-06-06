class EmailValidator < ActiveModel::EachValidator
  def validate_each(record, attribute, value)
    if not value =~ /^[-a-z0-9_+\.]+\@([-a-z0-9]+\.)+[a-z0-9]{2,4}$/i
      record.errors[attribute] << "Please enter a valid email address"
    else
      record.errors[attribute] << "An account with this email already exists" if ApplicationHelper.user_exists? value
    end
  end
end