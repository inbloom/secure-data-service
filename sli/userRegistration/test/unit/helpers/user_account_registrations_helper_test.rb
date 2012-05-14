require 'test_helper'

class UserAccountRegistrationsHelperTest < ActionView::TestCase
	def setup
	@user_account_registration=UserAccountRegistration.new(
        :email=> 'validated@valid.com' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => 'self'
    )
    if APP_CONFIG["is_sandbox"] == true
        @currEnvironment=true
       else
        @currEnvironment=false
       end
       ApprovalEngineProxy.init(APP_CONFIG['approval_uri'],@currEnvironment)		
    end
    def test_register_user_validated_user
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>true,"validated"=>true})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(!res["redirect"])
    	assert_equal(res["error"],"User name already exists in record")
    end
    def test_register_user_new_user
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>false,"validated"=>false})
    	ApprovalEngineProxy.stubs(:submitUser).returns({"status"=>"submitted"})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
    end

    def test_register_user_return_user
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>true,"validated"=>false})
    	ApprovalEngineProxy.stubs(:updateUser).returns({"status"=>"submitted"})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
    end
    def test_register_user_new_user_sandbox
    	ApprovalEngineProxy.stubs(:doesUserExist).returns({"exists"=>false,"validated"=>false})
    	ApprovalEngineProxy.stubs(:submitUser).returns({"status"=>"submitted"})
    	@user_account_registration.vendor=""
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
    end
end
