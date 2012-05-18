require 'test_helper'
require "mocha"

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
    	
    	RestClient.stubs(:get).returns(MockResponse.new(200,true))
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(!res["redirect"])
    	assert_equal(res["error"],"User name already exists in record")
    end
    def test_register_user_new_user
    	
    	RestClient.stubs(:get).returns(MockResponse.new(200,false,"[]"))
    	RestClient.stubs(:post).returns(MockResponse.new(201,false))
    	ApplicationHelper.stubs(:add_user).returns({:email=>"sweet@itworks.com"})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
    	assert_equal(res["guuid"],"1234567890")
    end

    def test_register_user_return_user
    	
    	RestClient.stubs(:get).returns(MockResponse.new(200,false))
    	RestClient.stubs(:put).returns(MockResponse.new(204,false))
    	ApplicationHelper.stubs(:update_user_info).returns({:email=>"sweet@itworks.com"})
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
    	assert_equal(res["guuid"],"1234567890")
    end
    def test_register_user_persist_error
    	
    	RestClient.stubs(:get).returns(MockResponse.new(200,false))
    	RestClient.stubs(:put).returns(MockResponse.new(404,false))
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(!res["redirect"])
    	assert_equal(res["error"],"Error occurred while storing record")
    end
    def test_register_user_api_error
    	
    	RestClient.stubs(:get).returns(MockResponse.new(500,false))
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(!res["redirect"])
    	assert_equal(res["error"],"Error occurred while storing record")
    end
    def test_register_user_new_user_sandbox
    	
    	RestClient.stubs(:get).returns(MockResponse.new(200,false,"[]"))
    	RestClient.stubs(:post).returns(MockResponse.new(201,false))
    	ApplicationHelper.stubs(:add_user).returns({:email=>"sweet@itworks.com"})
    	@user_account_registration.vendor=""
    	res=UserAccountRegistrationsHelper.register_user(@user_account_registration)
    	assert(res["redirect"])
    	assert_equal(res["error"],"")
    	assert_equal(res["guuid"],"1234567890")
    end
end
