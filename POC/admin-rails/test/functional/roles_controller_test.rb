require 'test_helper'

class RolesControllerTest < ActionController::TestCase
  setup do
    @role_admin    = {:id => 0, :name => "IT Administrator", :rights => ["One", "Two"], :mappings => nil}
    @role_educator = {:id => 1, :name => "Educator", :rights => ["Three", "Four"], :mappings => ["teacher"]}
    
    ActiveResource::HttpMock.respond_to do |mock|
      mock.get "roles", {"Accept" => "applciation/json"}, [@role_admin, @role_educator].to_json
      mock.get "roles/1", {"Accept" => "applicaiton/json"}, @role_educator.to_json
      mock.get "roles/0", {"Accept" => "applicaiton/json"}, @role_admin.to_json
    end
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:roles)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create role" do
    assert_difference('Role.count') do
      post :create, role: @role.attributes
    end

    assert_redirected_to role_path(assigns(:role))
  end

  test "should show role" do
    get :show, id: @role.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @role.to_param
    assert_response :success
  end

  test "should update role" do
    put :update, id: @role.to_param, role: @role.attributes
    assert_redirected_to role_path(assigns(:role))
  end

  test "should destroy role" do
    assert_difference('Role.count', -1) do
      delete :destroy, id: @role.to_param
    end

    assert_redirected_to roles_path
  end
end
