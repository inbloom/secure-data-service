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

class AccountManagementsControllerTest < ActionController::TestCase
  
  setup do
    APP_CONFIG['is_sandbox'] = true
    @account_managements = Array.new()
    @account_managements.push(@account_managements_fixtures['account1'])
    @account_managements.collect! do |account|
      am = AccountManagement.new
      am.name = account["name"]
      am.vendor = account["vendor"]
      am.email = account["email"]
      am.lastUpdate = account["lastUpdate"]
      am.status = account["status"]
      am.transitions = account["transitions"]
      am
    end
    $check_slc=false
  end

  test "should get index" do
    @controller.stubs(:get_all).returns(@account_managements)
    get :index
    assert_response :success
    assert_not_nil assigns(:account_managements)
  end

# test "should get new" do
#  get :new
#  assert_response :success
# end

#test "should create account_management" do
#  assert_difference('AccountManagement.count') do
#   post :create, account_management: @account_managements_fixtures['new_account']
#  end
# assert_redirected_to account_management_path(assigns(:account_managements))
#end

# test "should show account_management" do
#  get :show, id: @account_management.to_param
#  assert_response :success
#end

# test "should get edit" do
#   get :edit, id: @account_management.to_param
#   assert_response :success
# end

# test "should update account_management" do
#   put :update, id: @account_management.to_param, account_management: @account_management.attributes
#   assert_redirected_to account_management_path(assigns(:account_management))
# end

# test "should destroy account_management" do
#   assert_difference('AccountManagement.count', -1) do
#    delete :destroy, id: @account_management.to_param
#  end

#  assert_redirected_to account_managements_path
#  end
end
