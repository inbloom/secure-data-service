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

class AdminDelegationsControllerTest < ActionController::TestCase

  setup do
    @admin_delegation = @admin_delegations_fixtures['one']
    session[:roles] = ['LEA Administrator']
    session[:edOrgId] = 'ID1'
  end


  # test "should get index" do
  #   get :index
  #   assert_response :success
  #   assert_not_nil assigns(:admin_delegations)
  # end


  test "should create admin_delegation" do
    post :create, admin_delegation: @admin_delegation
    assert !flash[:notice].nil?
    assert_redirected_to admin_delegations_path
  end


  #test "should update admin_delegation" do
  #  put :update, id: @admin_delegation.to_param, admin_delegation: @admin_delegation.attributes
  #  assert_redirected_to admin_delegation_path(assigns(:admin_delegation))
  #end
  #
end
