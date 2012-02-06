require 'test_helper'

class TeacherSectionAssociationsControllerTest < ActionController::TestCase
  setup do
    @teacher_section_association = teacher_section_associations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:teacher_section_associations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create teacher_section_association" do
    assert_difference('TeacherSectionAssociation.count') do
      post :create, teacher_section_association: @teacher_section_association.attributes
    end

    assert_redirected_to teacher_section_association_path(assigns(:teacher_section_association))
  end

  test "should show teacher_section_association" do
    get :show, id: @teacher_section_association.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @teacher_section_association.to_param
    assert_response :success
  end

  test "should update teacher_section_association" do
    put :update, id: @teacher_section_association.to_param, teacher_section_association: @teacher_section_association.attributes
    assert_redirected_to teacher_section_association_path(assigns(:teacher_section_association))
  end

  test "should destroy teacher_section_association" do
    assert_difference('TeacherSectionAssociation.count', -1) do
      delete :destroy, id: @teacher_section_association.to_param
    end

    assert_redirected_to teacher_section_associations_path
  end
end
