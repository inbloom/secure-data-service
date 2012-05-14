require 'test_helper'
require 'proxy'
require 'mocha'

class EulasControllerTest < ActionController::TestCase
 
  test "should respond to post for accept/reject" do
    Eula.stubs(:accepted?).returns(true)
    ApprovalEngineProxy.stubs(:EULAStatus).returns({"token"=>"abcdefg"})
    get :create
    assert_template :finish

    Eula.stubs(:accepted?).returns(false)
    ApprovalEngineProxy.stubs(:EULAStatus).with(anything).returns({"token"=>""})
    get :create
    assert_redirected_to APP_CONFIG['redirect_slc_url']
  end

  test "should show eula" do
    assert_response :success
  end

  test "should check for valid session before rendering eula" do 
    Session.stubs(:valid?).returns(false)
    assert_raise(ActionController::RoutingError) { get :show}

    Session.stubs(:valid?).returns(true)
    assert_nothing_raised { get :show }
  end
end