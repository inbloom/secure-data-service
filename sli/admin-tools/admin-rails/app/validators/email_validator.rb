=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


class EmailValidator < ActiveModel::EachValidator
  @@EXISTING_EMAIL_MSG = "An account with this email already exists"

  def validate_each(record, attribute, value)
    # don't validate empty values here, otherwise we get duplicate error messages
    if value == nil or value.length == 0
      return
    elsif EmailValidator.is_valid_email?(record, attribute, value)
      record.errors[attribute] << @@EXISTING_EMAIL_MSG if ApplicationHelper.user_exists? value
    end
  end

  def self.is_valid_email?(record, attribute, value)
    if not value =~ /^[-a-z0-9_]+([\.]{0,1}[-a-z0-9_]+)*\@([a-z0-9]+([-]*[a-z0-9]+)*\.)*([a-z0-9]+([-]*[a-z0-9]+))+$/i
      record.errors[attribute] << "Please enter a valid email address"
    elsif value.length > 160
      record.errors[attribute] << "Email address is too long"
    end
    return record.errors[attribute].empty?
  end

  def self.existing_email_msg
    @@EXISTING_EMAIL_MSG
  end
end
