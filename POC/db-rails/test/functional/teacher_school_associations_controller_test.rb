require 'test_helper'

class TeacherSchoolAssociationsControllerTest < ActionController::TestCase
  setup do
    @teacher_school_association = teacher_school_associations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:teacher_school_associations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create teacher_school_association" do
    assert_difference('TeacherSchoolAssociation.count') do
      post :create, teacher_school_association: @teacher_school_association.attributes
    end

    assert_redirected_to teacher_school_association_path(assigns(:teacher_school_association))
  end

  test "should show teacher_school_association" do
    get :show, id: @teacher_school_association.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @teacher_school_association.to_param
    assert_response :success
  end

  test "should update teacher_school_association" do
    put :update, id: @teacher_school_association.to_param, teacher_school_association: @teacher_school_association.attributes
    assert_redirected_to teacher_school_association_path(assigns(:teacher_school_association))
  end

  test "should destroy teacher_school_association" do
    assert_difference('TeacherSchoolAssociation.count', -1) do
      delete :destroy, id: @teacher_school_association.to_param
    end

    assert_redirected_to teacher_school_associations_path
  end
end
