=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

class AppsControllerTest < ActionController::TestCase
  setup do
    session[:roles] = ["Application Developer"]
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

  # test "cannot create as operator" do
  #   post :create, {:app => @app_fixtures["new"], :app_behavior => "Full Window App"}, {:roles => ["Operator","IT Administrator"]}
  #   
  # end
  # 
  # test "cannot index as realm admin" do
  #   get :index, {}, {:roles => ["Realm Administrator"]}
  #   
  # end

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
    assert_redirected_to apps_path
  end

  test "should update App" do
    APP_LDAP_CLIENT.stubs(:read_user).returns({:vendor => "waffles"})
    put :update, id:@Apps[0].id, app: @app_fixtures["update"]
    assert_redirected_to apps_path
  end

  test "should get lea with valid state" do
    get :get_local_edorgs, state: "NC", format: :js
    assert_not_nil assigns(:results)
    assert_response :success
  end

  # test "should destroy App" do
  #   assert_difference('App.count', -1) do
  #     delete :destroy, id: @Apps.first.to_param
  #   end
  # 
  #   assert_redirected_to Apps_path
  # end
end
