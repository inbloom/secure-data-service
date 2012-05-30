require 'test_helper'
require 'mocha'

class EulasControllerTest < ActionController::TestCase
  setup do
    @request.env['SERVER_PROTOCOL']='HTTP/1.1'
  end
 
  test "should respond to post for accept/reject" do
    Eula.stubs(:accepted?).returns(true)
    ApplicationHelper.stubs(:send_user_verification_email).returns(true)
    get :create
    assert_template :finish

    ApplicationHelper.stubs(:send_user_verification_email).returns(false)
    get :create
    assert_template :account_error

    Eula.stubs(:accepted?).returns(false)
    ApplicationHelper.stubs(:remove_user_account)
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
