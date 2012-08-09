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


require 'test_helper'
require "mocha"

class UserAccountRegistrationsHelperTest < ActionView::TestCase
  def setup
    @user_account_registration=UserAccountRegistration.new(
    :email=> 'validated@valid.com' ,
    :firstName => 'test',
    :lastName => 'testLName',
    :password => 'secret',
    :vendor => nil
    )
  end
  
  def test_register_user_validated_user
    ApplicationHelper.expects(:add_user).with(@user_account_registration).once()
    UserAccountRegistrationsHelper.register_user(@user_account_registration)
    assert_equal 'None', @user_account_registration.vendor
  end
end
