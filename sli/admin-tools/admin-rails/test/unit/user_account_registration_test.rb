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


require 'test_helper'

class UserAccountRegistrationTest < ActiveSupport::TestCase

  def setup
    @user_account_registration = UserAccountRegistration.new(
        :email => 'validated@valid.com' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => nil
    )
  end

  def test_initialise
    user_account_registration=UserAccountRegistration.new(
        :email => 'test@test.com' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => 'self'
    )
    assert_equal('test@test.com',user_account_registration.email)
    assert_equal('test',user_account_registration.firstName)
    assert_equal('testLName',user_account_registration.lastName)
    assert_equal('secret',user_account_registration.password)
    assert_equal('self',user_account_registration.vendor)

  end
  def test_invalid_email
    user_account_registration=UserAccountRegistration.new(
        :email => '..test@test' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => 'self'
    )
    assert(!user_account_registration.valid?)
    assert(user_account_registration.errors[:email].any?)
  end

  def test_register_user_validated_user
    ApplicationHelper.expects(:add_user).with(@user_account_registration).once()
    @user_account_registration.register
    assert_equal 'None', @user_account_registration.vendor
  end
end
