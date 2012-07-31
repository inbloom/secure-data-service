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
require 'mocha'

class RealmsControllerTest < ActionController::TestCase

  test "should get index" do
    @controller.stubs(:get_user_realm).returns("#{@realm_fixtures['one']['id']}")
    get :index
    #assert_redirected_to  "/realms/#{@realm_fixtures['one']['id']}"
    assert_response 404
  end


  test "should get index - no matching realm" do
    @controller.stubs(:get_user_realm).returns("blah")
    get :index
    assert_response 404
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

   test "bad show - invalid realm" do
     get :show, id: "5a4bfe96-1724-4565-9db1-35b3796e3ce2"
     assert_response :not_found
   end
   
   test "should get edit" do
     get :edit, id: @realm_fixtures['one']['id']
     assert_response :success
     assert assigns(:realm)
   end

   test "bad get edit - invalid realm" do
     get :edit, id: "5a4bfe96-1724-4565-9db1-35b3796e3ce2"
     assert_response :not_found
   end
   
   test "should update realm" do
     put :update, {id: 1, mappings: @realm_fixtures['one'], format: 'json'}
     assert_response :success
     assert assigns(:realm)
     #mappings = Realm.find(1).mappings.to_json
     # puts mappings.inspect
   end

   test "bad update - invalid realm" do
     put :update, {id: "5a4bfe96-1724-4565-9db1-35b3796e3ce2", mappings: {}, format: 'json'}
     assert_response :not_found
     #mappings = Realm.find(1).mappings.to_json
     # puts mappings.inspect
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
