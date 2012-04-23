require 'test_helper'

class EducationOrganizationsControllerTest < ActionController::TestCase
  setup do
    @education_organization = education_organizations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:education_organizations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create education_organization" do
    assert_difference('EducationOrganization.count') do
      post :create, education_organization: @education_organization.attributes
    end

    assert_redirected_to education_organization_path(assigns(:education_organization))
  end

  test "should show education_organization" do
    get :show, id: @education_organization.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @education_organization.to_param
    assert_response :success
  end

  test "should update education_organization" do
    put :update, id: @education_organization.to_param, education_organization: @education_organization.attributes
    assert_redirected_to education_organization_path(assigns(:education_organization))
  end

  test "should destroy education_organization" do
    assert_difference('EducationOrganization.count', -1) do
      delete :destroy, id: @education_organization.to_param
    end

    assert_redirected_to education_organizations_path
  end
end
