require 'test_helper'
require "mocha"

class UserAccountRegistrationsHelperTest < ActionView::TestCase
	URL=APP_CONFIG['api_base']
  	URL_HEADER = {
      "Content-Type" => "application/json",
      "content_type" => "json",
      "accept" => "application/json"
    }
    API_GET_SUCCESS_VALIDATE ={
		"code" => 200,
		"body" => [{
			"validated" => true
			}]    
    }
    BODY_VALIDATED ={
			"validated" => true
    }
	def setup
		currEnv=APP_CONFIG["is_sandbox"]? "Sandbox":"Production"
		url_validated_user=URL+"?userName=validated@valid.com"+"&environment="+currEnv
		response = mock('RestClient::Response')
		response.stubs(:code => '200')
		response.stubs(:body => BODY_VALIDATED)
		RestClient.expects(:get).times(5).with(url_validated_user,URL_HEADER).returns(response)
    	#puts "KM Result: " + ApprovalEngine.get_user_emailtoken("km").to_s
		
    end
    def test_register_user_validated_user
    	user_account_registration=UserAccountRegistration.new(
        :email=> 'validated@valid.com' ,
        :firstName => 'test',
        :lastName => 'testLName',
        :password => 'secret',
        :vendor => 'self'
    )
    	res=UserAccountRegistrationsHelper.register_user(user_account_registration)
    	puts "KM Result: " + UserAccountRegistrationsHelper.register_user(user_account_registration).to_s
    	assert(!res["redirect"])
    	assert_equal(res["error"],"User name already exists in record")
    end
end
