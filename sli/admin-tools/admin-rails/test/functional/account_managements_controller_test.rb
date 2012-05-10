require 'test_helper'

class AccountManagementsControllerTest < ActionController::TestCase
  setup do
    @account_managements = Array.new()
    @account_managements.push(@account_managements_fixtures['account1'])
    $check_slc=false
  end

 test "should get index" do
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
