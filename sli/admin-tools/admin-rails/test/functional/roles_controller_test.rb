=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end


require 'test_helper'

class RolesControllerTest < ActionController::TestCase
  setup do
    @roles = Role.all
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
  # 
  # test "should create role" do
  #   assert_difference('Role.count') do
  #     post :create, role: @role.attributes
  #   end
  # 
  #   assert_redirected_to role_path(assigns(:role))
  # end

  test "should show role" do
    get :show, id: @roles[0].id
    assert_response :success
  end

  # test "should get edit" do
  #   get :edit, id: @roles[0].id
  #   assert_response :success
  # end
  # 
  # test "should update role" do
  #   put :update, id:@roles[0].id, role: @role_fixtures["update"]
  #   assert_redirected_to role_path(assigns(:role))
  # end

  # test "should destroy role" do
  #   assert_difference('Role.count', -1) do
  #     delete :destroy, id: @role.to_param
  #   end
  # 
  #   assert_redirected_to roles_path
  # end
end
