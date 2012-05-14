require 'test_helper'

class UserAccountRegistrationTest < ActiveSupport::TestCase
    def setup
        
    end
   def test_initialise
    user_account_registration=UserAccountRegistration.new(
        :email=> 'test@test.com' ,
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
        :email=> 'test@test' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => 'self'
    )
    assert(!user_account_registration.valid?)
    assert(user_account_registration.errors[:email].any?)
   end
end
