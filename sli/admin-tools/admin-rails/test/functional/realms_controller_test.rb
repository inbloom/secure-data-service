require 'test_helper'
require 'mocha'

class RealmsControllerTest < ActionController::TestCase

  test "should get index" do
    @controller.stubs(:get_user_realm).returns("dc=slidev,dc=org")
    get :index
    assert_redirected_to  "/realms/#{@realm_fixtures['one']['id']}"
  end

  # test "should get new" do
  #   get :new
  #   assert_response :success
  # end
  # 
  # test "should create realm" do
  #   assert_difference('Realm.count') do
  #     post :create, realm: @realm.attributes
  #   end
  # 
  #   assert_redirected_to realm_path(assigns(:realm))
  # end
  # 
   test "should show realm" do
     get :show, id: @realm_fixtures['one']['id']
     assert_response :success
     assert assigns(:realm)
   end
  # 
   test "should get edit" do
     get :edit, id: @realm_fixtures['one']['id']
     assert_response :success
     assert assigns(:realm)
   end
  # 
   test "should update realm" do
     put :update, {id: 1, mappings: @realm_fixtures['one']['mappings'], format: 'json'}
     assert_response :success
     assert assigns(:realm)
   end
  # 
  # test "should destroy realm" do
  #   assert_difference('Realm.count', -1) do
  #     delete :destroy, id: @realm.to_param
  #   end
  # 
  #   assert_redirected_to realms_path
  # end
end
