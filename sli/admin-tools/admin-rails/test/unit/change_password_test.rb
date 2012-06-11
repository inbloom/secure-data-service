require 'test_helper'

class ChangePasswordTest < ActiveSupport::TestCase
  def setup

  end
  def test_initialise
    change_password=ChangePassword.new(
        :old_pass=> 'test1234' ,
        :new_pass => 'test4321',
        :confirmation => 'test4321'
    )
    assert_equal('test1234',change_password.old_pass)
    assert_equal('test4321',change_password.new_pass)
    assert_equal('test4321',change_password.confirmation)
    assert(change_password.valid?)
  end

  def test_confirmation_password
    change_password=ChangePassword.new(
        :old_pass=> 'test1234' ,
        :new_pass => 'test4321',
        :confirmation => 'test431'
    )
    assert(!change_password.valid?)
  end

  def test_confirmation_password
    change_password=ChangePassword.new(
        :old_pass=> 'test1234' ,
        :new_pass => 'test1234',
        :confirmation => 'test1234'
    )
    assert(!change_password.valid?)
  end
end