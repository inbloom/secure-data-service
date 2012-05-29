require 'test_helper'

class AppsControllerTest < ActionController::TestCase
  setup do
    @Apps = App.all
  end

  test "should get index" do
    session[:roles] = ["Application Developer"]
    get :index
    assert_response :success
    assert_not_nil assigns(:apps)
  end

  test "should get new" do
    session[:roles] = ["Application Developer"]
    get :new
    assert_response :success
  end

  test "cannot create as operator" do
    post :create, {:app => @app_fixtures["new"], :app_behavior => "Full Window App"}, {:roles => ["Operator","IT Administrator"]}
    !assigns @app
    assert !flash.nil?
    assert @response.body =~ /Not Authorized/
  end
  
  test "cannot index as realm admin" do
    get :index, {}, {:roles => ["Realm Administrator"]}
    !assigns @apps
    assert !flash.nil?
    assert @response.body =~ /Not Authorized/
  end

  # test "should create app" do
  #   assert_difference("#{@Apps.count}") do
  #     post :create, app: @app_fixtures["new"]
  #   end
  # 
  #   assert_redirected_to App_path(assigns(:apps))
  # end

  test "should show App" do
    session[:roles] = ["Application Developer"]
    get :show, id: @Apps[0].id
    assert_response :success
  end
  
  # test "should update App" do
  #   put :update, id:@Apps[0].id, App: @app_fixtures["update"]
  #   assert_redirected_to App_path(assigns(:App))
  # end

  # test "should destroy App" do
  #   assert_difference('App.count', -1) do
  #     delete :destroy, id: @Apps.first.to_param
  #   end
  # 
  #   assert_redirected_to Apps_path
  # end
end
