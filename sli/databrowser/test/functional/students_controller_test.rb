require 'test_helper'
require 'test/unit'
require 'mocha'

class StudentsControllerTest < ActionController::TestCase

  def setup
    #res = RestClient.get("https://devopenam1.slidev.org/idp1/identity/authenticate?username=linda.kim&password=linda.kim1234")
    #token = res.split("=")[1]
    #print("The token being used for testing is #{token}")
    #SessionResource.auth_id = token
    #mock_rest_client = Test::Unit::MockObject(RestClient).new
    #print("The client is #{mock_rest_client.class}\n")

    SessionResource.auth_id = "test_auth"
    @api_url = "https://devapp1.slidev.org/api/rest"
    @accepts = {"Accept" => "application/json"}

    schools_link =  "#{@api_url}/schools"
    first_school_link = "#{@api_url}/schools/test-school-1"
    get_students_link = "#{@api_url}/schools/test-school-1/getStudents"


    RestClient.expects(:get).with("#{@api_url}/home?sessionId=test_auth", @accepts).returns('{"links" : [{"rel" : "getFoo"}, {"rel" : "getSchools", "href" : "' + schools_link + '"}]}')
    RestClient.expects(:get).with(schools_link + "?sessionId=#{SessionResource.auth_id}", @accepts).returns('[{"link" : {"href" : "' +first_school_link + '"}}]')
    RestClient.expects(:get).with(first_school_link + "?sessionId=#{SessionResource.auth_id}", @accepts).returns('{"links" : [{"rel" : "getStudents", "href": "' + get_students_link + '"}]}')
    RestClient.expects(:get).with(get_students_link + "?sessionId=#{SessionResource.auth_id}", @accepts).returns('[{"id" : "first-student", "link" : {"href":"http://example.com"}}]')

  end

  test "should get index" do
    get :index
    assert_response :success
  end

end
