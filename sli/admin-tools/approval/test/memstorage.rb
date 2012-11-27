=begin

Copyright 2012 Shared Learning Collaborative, LLC

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


require 'digest'

class MemoryStorage
  def initialize
    @storage = []
  end


  # user_info = {
  #     :first => "John",
  #     :last => "Doe",
  #     :email => "jdoe@example.com",
  #     :password => "secret",
  #     :vendor => "Acme Inc."
  # }
  #
  # returns user_info with additional fields:
  #   :emailtoken ... hash string
  #   :updated ... datetime
  #   :status  ... "submitted"
  #
  def create_user(user_info)
    # make sure the user does not exist
    raise "User #{user_info[:email]} already exists." if self.user_exists?(user_info[:email])

    # add the missing field and store it internally
    new_user_info = user_info.clone
    new_user_info[:emailtoken] = Digest::MD5.hexdigest(user_info[:email]+user_info[:first]+user_info[:last])
    new_user_info[:updated]    = 10
    new_user_info[:status]     = "pending"
    @storage << new_user_info
    return new_user_info
  end

  # returns extended user_info
  def read_user(email_address)
    idx = @@storage.index {|e| e[:email] == email_address }

  end

  # updates the user status from an extended user_info
  def update_status(user)
  end

  # returns true if the user exists
  def user_exists?(email_address)
    !!(@@storage.index {|e| e[:email] == email_address })
  end

  # returns extended user_info for the given emailtoken (see create_user) or nil
  def read_user_emailtoken(emailtoken)
  end

  # returns array of extended user_info for all users or all users with given status
  # use constants in approval.rb
  def read_users(status=nil)
  end

  # updates the user_info except for the user status
  # user_info is the same input as for create_user
  def update_user_info(user_info)
  end

  # deletes the user entirely
  def delete_user(email_address)
  end
end

