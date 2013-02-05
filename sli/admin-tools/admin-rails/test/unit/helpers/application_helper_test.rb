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


class ApplicationHelperTest < ActionView::TestCase
  USER_ACCOUNT = {
    :firstName      => "bob",
    :lastName       => "bobAgain",
    :email      => "email",
    :password   => "secret",
    :vendor     => "whoCares!"
  }
  MOCK_EMAIL_INFO = {
    "email_address" => "bob@bob.com",
    "first_name" => "Bob",
    "last_name" => "Roberts"
  }
  MOCK_EMAIL_INFO_FROM_API = "{\"userName\" : \"bob@bob.com\",\"firstName\" : \"Bob\",\"lastName\" : \"Roberts\"}"

  test "removal of user account associated to nil does not error" do
    ApplicationHelper.remove_user_account nil
  end

  test "removal of user account and removal from LDAP" do
    RestClient.stubs(:get).returns(MockResponse.new(200,true,"{\"userName\":\"bob@bob.com\"}"))
    RestClient.stubs(:delete).returns(MockResponse.new(200,true))
    ApprovalEngine.stubs(:remove_user).returns({})
    ApplicationHelper.remove_user_account("1234567890")
  end

  test "send user verification email where there is a problem" do
    user = { :first => 'First', :email => 'email@wgen.net', :emailtoken => 'token' }
    ApprovalEngine.expects(:change_user_status).with(user[:email], "accept_eula").once()
    ApprovalEngine.expects(:get_user).with(user[:email]).returns(user).once()
    msg = mock()
    msg.expects(:deliver).once()
    ApplicationMailer.expects(:verify_email).with( user[:email], user[:first], anything()).returns(msg).once()
    
    assert_equal true, ApplicationHelper.send_user_verification_email("unused junk", user[:email])
  end

  def test_get_user_with_emailtoken
    ApprovalEngine.stubs(:get_user_emailtoken).returns({:email=>"test@test.com"})
    res=ApplicationHelper.get_user_with_emailtoken("12345")
    assert_equal(res[:email],"test@test.com")
  end
  def test_verify_email
    ApprovalEngine.stubs(:verify_email).returns("test@test.com")
    res=ApplicationHelper.verify_email("test@test.com")
  end
  def test_get_email_token
    new_user = {
      :first      => "bob",
      :last       => "bobAgain",
      :email      => "email",
      :password   => "secret",
      :vendor     => "whoCares!",
      :status     => "submitted",
      :emailtoken => "junk"
    }
    ApprovalEngine.stubs(:get_user).returns(new_user)
    res=ApplicationHelper.get_email_token("test@test.com")
    assert_equal(res,"junk")
  end
  def test_update_user_info
    ApprovalEngine.stubs(:update_user_info).returns({})
    res=ApplicationHelper.update_user_info(MockUser.new())
  end
  def test_add_user
    ApprovalEngine.stubs(:add_disabled_user).returns({})
    res=ApplicationHelper.add_user(MockUser.new())
  end
end
class MockUser
  @firstName
  @lastName
  @email
  @password
  @vendor

  def initialize(newfirstName="bob",newlastname="bobL",newEmail="goToJunk@junk.com",newPassword="secret",newVendor="none")
    @firstName=newfirstName
    @lastName = newlastname
    @email =newEmail
    @password = newPassword
    @vendor =newVendor
  end

  def firstName
    return @firstName
  end
  def lastName
    return @lastName
  end
  def email
    return @email
  end
  def password
    return @password
  end
  def vendor
    return @vendor
  end
end
