require 'test_helper'
require 'mocha'

class EulasControllerTest < ActionController::TestCase
 
  test "should respond to post for accept/reject" do
    Eula.stubs(:accepted?).returns(true)
    get :create
    assert_template :finish

    Eula.stubs(:accepted?).returns(false)
    get :create
    assert_redirected_to APP_CONFIG['redirect_slc_url']
  end

  test "should show eula" do
    assert_response :success
  end
end