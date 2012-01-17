require 'test_helper'

class RolesControllerTest < ActionController::TestCase
  setup do
    @admin = {:id => 0, :name => "IT Administrator", :rights => ["Something", "something"], :mappings => "NC Teacher"}
    @educator = {:id => 1, :name => "Educator", :rights => ["else", "else"], :mappings => []}
    
    ActiveResource::HttpMock.respond_to do |mock|
      mock.get "/api/rest/admin/roles?sessionId=", {"Accept" => "application/json"}, [@admin, @educator].to_json
      mock.get "/api/rest/admin/roles/1?sessionId=", {"Accept" => "application/json"}, @educator.to_json
      mock.get "/api/rest/admin/roles/0?sessionId=", {"Accept" => "application/json"}, @admin.to_json
    end
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:roles)
  end

  # test "should get new" do
  #   get :new
  #   assert_response :success
  # end

  # test "should create role" do
  #   assert_difference('Role.count') do
  #     post :create, role: @role.attributes
  #   end
  # 
  #   assert_redirected_to role_path(assigns(:role))
  # end

  test "should show role" do
    get :show, {'id' => '0'}
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @educator[:id].to_s
    assert_response :success
  end

  test "should update role" do
    put :update, id: @educator[:id].to_s, role: @role.attributes
    assert_redirected_to role_path(assigns(:role))
  end

  # test "should destroy role" do
  #   assert_difference('Role.count', -1) do
  #     delete :destroy, id: @educator[:id].to_s
  #   end
  # 
  #   assert_redirected_to roles_path
  # end
end
