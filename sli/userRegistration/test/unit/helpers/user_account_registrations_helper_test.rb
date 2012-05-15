require 'test_helper'
require 'proxy'

class UserAccountRegistrationsHelperTest < ActionView::TestCase
	def setup
	@user_account_registration=UserAccountRegistration.new(
        :email=> 'validated@valid.com' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => 'self'
    )		
    end
    def test_register_user_validated_user
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>true,"validated"=>true})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(!res["redirect"])
    	assert_equal(res["error"],"User name already exists in record")
    end
    def test_register_user_new_user
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>false,"validated"=>false})
    	ApprovalEngineProxy.stubs(:submitUser).returns({"status"=>"submitted","verificationToken"=>"1234abcd"})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
        assert_equal(res["verificationToken"],"1234abcd")
    end

    def test_register_user_return_user
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>true,"validated"=>false})
    	ApprovalEngineProxy.stubs(:updateUser).returns({"status"=>"submitted","verificationToken"=>"1234abcd"})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
        assert_equal(res["verificationToken"],"1234abcd")
    end
    def test_register_user_new_user_sandbox
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>false,"validated"=>false})
    	ApprovalEngineProxy.stubs(:submitUser).returns({"status"=>"submitted","verificationToken"=>"1234abcd"})
    	@user_account_registration.vendor=""
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
        assert_equal(res["verificationToken"],"1234abcd")
    end
end
