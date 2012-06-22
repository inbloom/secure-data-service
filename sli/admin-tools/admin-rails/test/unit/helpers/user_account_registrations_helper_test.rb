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
