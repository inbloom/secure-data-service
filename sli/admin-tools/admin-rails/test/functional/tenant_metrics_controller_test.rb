require 'test_helper'

class TenantMetricsControllerTest < ActionController::TestCase
  setup do
    @tenant_metric = tenant_metrics(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:tenant_metrics)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create tenant_metric" do
    assert_difference('TenantMetric.count') do
      post :create, tenant_metric: @tenant_metric.attributes
    end

    assert_redirected_to tenant_metric_path(assigns(:tenant_metric))
  end

  test "should show tenant_metric" do
    get :show, id: @tenant_metric.to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @tenant_metric.to_param
    assert_response :success
  end

  test "should update tenant_metric" do
    put :update, id: @tenant_metric.to_param, tenant_metric: @tenant_metric.attributes
    assert_redirected_to tenant_metric_path(assigns(:tenant_metric))
  end

  test "should destroy tenant_metric" do
    assert_difference('TenantMetric.count', -1) do
      delete :destroy, id: @tenant_metric.to_param
    end

    assert_redirected_to tenant_metrics_path
  end
end
