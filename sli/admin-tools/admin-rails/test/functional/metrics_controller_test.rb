require 'test_helper'

class MetricsControllerTest < ActionController::TestCase
  setup do
    @metric = metrics(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:metrics)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create metric" do
    assert_difference('Metric.count') do
      post :create, metric: @metric.attributes
    end

    assert_redirected_to metric_path(assigns(:metric))
  end

  test "should show metric" do
    get :show, id: @metric.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @metric.to_param
    assert_response :success
  end

  test "should update metric" do
    put :update, id: @metric.to_param, metric: @metric.attributes
    assert_redirected_to metric_path(assigns(:metric))
  end

  test "should destroy metric" do
    assert_difference('Metric.count', -1) do
      delete :destroy, id: @metric.to_param
    end

    assert_redirected_to metrics_path
  end
end
