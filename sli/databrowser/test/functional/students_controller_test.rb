require 'test_helper'

class StudentsControllerTest < ActionController::TestCase

  def setup
    print("About to start test\n")
    res = RestClient.get("https://devopenam1.slidev.org/idp1/identity/authenticate?username=linda.kim&password=linda.kim1234")
    token = res.split("=")[1]
    print("The token being used for testing is #{token}")
    SessionResource.auth_id = token
    #mock_rest_client = Test::Unit::MockObject(RestClient).new
    #print("The client is #{mock_rest_client.class}\n")
  end

  test "should get index" do
    get :index
    assert_response :success
  end

end
